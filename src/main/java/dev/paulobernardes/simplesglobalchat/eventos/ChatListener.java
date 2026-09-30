package dev.paulobernardes.simplesglobalchat.eventos;

import dev.paulobernardes.simplesglobalchat.SimplesGlobalChat;
import me.clip.placeholderapi.PlaceholderAPI;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    private final SimplesGlobalChat plugin;
    private final LuckPerms luckPerms;

    public ChatListener(
            SimplesGlobalChat plugin,
            LuckPerms luckPerms
    ) {
        this.plugin = plugin;
        this.luckPerms = luckPerms;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void aoEnviarMensagem(
            AsyncPlayerChatEvent evento
    ) {

        if (evento.isCancelled()) {
            return;
        }

        Player jogador =
                evento.getPlayer();

        if (!plugin.isChatLocalAtivo()) {

            evento.setCancelled(true);

            jogador.sendMessage(
                    colorir(
                            plugin.getConfig()
                                    .getString(
                                            "mensagens.chat-local-desativado",
                                            ""
                                    )
                    )
            );

            return;
        }

        long restante =
                plugin.getGerenciadorCooldown()
                        .verificarLocal(
                                jogador.getUniqueId()
                        );

        if (restante > 0) {

            evento.setCancelled(true);

            String mensagemCooldown =
                    plugin.getConfig()
                            .getString(
                                    "mensagens.chat-local-cooldown",
                                    ""
                            );

            mensagemCooldown =
                    mensagemCooldown.replace(
                            "%tempo%",
                            String.valueOf(restante)
                    );

            jogador.sendMessage(
                    colorir(
                            mensagemCooldown
                    )
            );

            return;
        }

        double raio =
                plugin.getConfig()
                        .getDouble(
                                "chat.local.raio",
                                50
                        );

        String prefixo =
                obterPrefixo(
                        jogador
                );

        String formato =
                plugin.getConfig()
                        .getString(
                                "chat.local.formato",
                                ""
                        );

        String mensagemJogador =
                processarMensagem(
                        jogador,
                        evento.getMessage()
                );

        String mensagem =
                formato
                        .replace(
                                "%prefix%",
                                prefixo
                        )
                        .replace(
                                "%player%",
                                jogador.getName()
                        )
                        .replace(
                                "%message%",
                                mensagemJogador
                        );

        if (Bukkit.getPluginManager()
                .isPluginEnabled(
                        "PlaceholderAPI"
                )) {

            mensagem =
                    PlaceholderAPI.setPlaceholders(
                            jogador,
                            mensagem
                    );
        }

        mensagem =
                colorir(
                        mensagem
                );

        evento.setCancelled(true);

        boolean encontrouJogador =
                false;

        for (Player destinatario :
                Bukkit.getOnlinePlayers()) {

            if (destinatario.equals(jogador)) {
                continue;
            }

            if (!destinatario.getWorld()
                    .equals(
                            jogador.getWorld()
                    )) {
                continue;
            }

            if (destinatario.getLocation()
                    .distance(
                            jogador.getLocation()
                    ) > raio) {
                continue;
            }

            encontrouJogador = true;

            destinatario.sendMessage(
                    mensagem
            );
        }

        jogador.sendMessage(
                mensagem
        );

        plugin.getGerenciadorCooldown()
                .iniciarLocal(
                        jogador.getUniqueId()
                );

        if (!encontrouJogador) {

            jogador.sendMessage(
                    colorir(
                            plugin.getConfig()
                                    .getString(
                                            "mensagens.chat-local-sem-players",
                                            ""
                                    )
                    )
            );
        }
    }

    private String processarMensagem(
            Player jogador,
            String mensagem
    ) {

        String permissaoCores =
                plugin.getConfig()
                        .getString(
                                "permissoes.cores",
                                "simplesglobalchat.cores"
                        );

        if (jogador.isOp()
                || jogador.hasPermission(
                permissaoCores
        )) {

            return colorir(
                    mensagem
            );
        }

        return mensagem.replaceAll(
                "(?i)&[0-9a-fk-or]",
                ""
        );
    }

    private String obterPrefixo(
            Player jogador
    ) {

        User usuario =
                luckPerms.getUserManager()
                        .getUser(
                                jogador.getUniqueId()
                        );

        if (usuario == null) {
            return "";
        }

        String grupo =
                usuario.getPrimaryGroup();

        if (grupo == null
                || grupo.equalsIgnoreCase(
                "default"
        )) {

            return "";
        }

        String prefixo =
                usuario.getCachedData()
                        .getMetaData()
                        .getPrefix();

        if (prefixo == null
                || prefixo.isEmpty()) {

            return "";
        }

        return colorir(
                prefixo
        ) + " ";
    }

    private String colorir(
            String mensagem
    ) {

        if (mensagem == null) {
            return "";
        }

        return ChatColor.translateAlternateColorCodes(
                '&',
                mensagem
        );
    }
}