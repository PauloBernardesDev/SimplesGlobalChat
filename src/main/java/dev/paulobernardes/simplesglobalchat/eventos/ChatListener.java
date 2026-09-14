package dev.paulobernardes.simplesglobalchat.eventos;

import dev.paulobernardes.simplesglobalchat.SimplesGlobalChat;
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

    public ChatListener(SimplesGlobalChat plugin, LuckPerms luckPerms) {
        this.plugin = plugin;
        this.luckPerms = luckPerms;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void aoEnviarMensagem(AsyncPlayerChatEvent evento) {

        if (evento.isCancelled()) {
            return;
        }

        Player jogador = evento.getPlayer();

        if (!plugin.isChatLocalAtivo()) {
            evento.setCancelled(true);

            jogador.sendMessage(
                    colorir(plugin.getConfig().getString(
                            "mensagens.chat-local-desativado",
                            "&cO chat local está temporariamente desativado."
                    ))
            );

            return;
        }

        double raio = plugin.getConfig()
                .getDouble("chat.local.raio", 50);

        String prefixo = obterPrefixo(jogador);

        String formato = plugin.getConfig()
                .getString(
                        "chat.local.formato",
                        "&7[L] %prefix%&f%player%&7: &f%message%"
                );

        String mensagemJogador = processarMensagem(
                jogador,
                evento.getMessage()
        );

        String mensagem = formato
                .replace("%prefix%", prefixo)
                .replace("%player%", jogador.getName())
                .replace("%message%", mensagemJogador);

        mensagem = colorir(mensagem);

        evento.setCancelled(true);

        boolean encontrouJogador = false;

        for (Player destinatario : Bukkit.getOnlinePlayers()) {

            if (destinatario.equals(jogador)) {
                continue;
            }

            if (!destinatario.getWorld().equals(jogador.getWorld())) {
                continue;
            }

            if (destinatario.getLocation().distance(jogador.getLocation()) > raio) {
                continue;
            }

            encontrouJogador = true;

            destinatario.sendMessage(mensagem);
        }

        jogador.sendMessage(mensagem);

        if (!encontrouJogador) {
            jogador.sendMessage(
                    colorir(plugin.getConfig().getString(
                            "mensagens.chat-local-sem-players",
                            "&7Não há ninguém por perto!"
                    ))
            );
        }
    }

    private String processarMensagem(Player jogador, String mensagem) {

        if (jogador.isOp() || jogador.hasPermission("simplesglobalchat.cores")) {
            return colorir(mensagem);
        }

        return mensagem.replaceAll("(?i)&[0-9a-fk-or]", "");
    }

    private String obterPrefixo(Player jogador) {

        User usuario = luckPerms.getUserManager()
                .getUser(jogador.getUniqueId());

        if (usuario == null) {
            return "";
        }

        String grupo = usuario.getPrimaryGroup();

        if (grupo == null || grupo.equalsIgnoreCase("default")) {
            return "";
        }

        String prefixo = usuario.getCachedData()
                .getMetaData()
                .getPrefix();

        if (prefixo == null || prefixo.isEmpty()) {
            return "";
        }

        return colorir(prefixo) + " ";
    }

    private String colorir(String mensagem) {
        return ChatColor.translateAlternateColorCodes('&', mensagem);
    }
}