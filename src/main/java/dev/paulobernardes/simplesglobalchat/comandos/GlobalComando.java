package dev.paulobernardes.simplesglobalchat.comandos;

import dev.paulobernardes.simplesglobalchat.SimplesGlobalChat;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GlobalComando implements CommandExecutor {

    private final SimplesGlobalChat plugin;
    private final LuckPerms luckPerms;

    public GlobalComando(
            SimplesGlobalChat plugin,
            LuckPerms luckPerms
    ) {
        this.plugin = plugin;
        this.luckPerms = luckPerms;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (!(sender instanceof Player jogador)) {

            sender.sendMessage(
                    colorir(
                            plugin.getConfig().getString(
                                    "mensagens.somente-jogadores",
                                    ""
                            )
                    )
            );

            return true;
        }

        String permissaoGlobal =
                plugin.getConfig()
                        .getString(
                                "permissoes.global",
                                "simplesglobalchat.global"
                        );

        if (!jogador.hasPermission(
                permissaoGlobal
        )) {

            jogador.sendMessage(
                    colorir(
                            plugin.getConfig().getString(
                                    "mensagens.chat-global-sem-permissao",
                                    ""
                            )
                    )
            );

            return true;
        }

        if (args.length == 0) {

            jogador.sendMessage(
                    colorir(
                            plugin.getConfig().getString(
                                    "mensagens.uso-g",
                                    ""
                            )
                    )
            );

            return true;
        }

        if (!plugin.isChatGlobalAtivo()) {

            jogador.sendMessage(
                    colorir(
                            plugin.getConfig().getString(
                                    "mensagens.chat-global-desativado",
                                    ""
                            )
                    )
            );

            return true;
        }

        long restante =
                plugin.getGerenciadorCooldown()
                        .verificarGlobal(
                                jogador.getUniqueId()
                        );

        if (restante > 0) {

            String mensagemCooldown =
                    plugin.getConfig().getString(
                            "mensagens.chat-global-cooldown",
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

            return true;
        }

        String mensagemJogador =
                String.join(
                        " ",
                        args
                );

        mensagemJogador =
                processarMensagem(
                        jogador,
                        mensagemJogador
                );

        String prefixo =
                obterPrefixo(
                        jogador
                );

        String formato =
                plugin.getConfig()
                        .getString(
                                "chat.global.formato",
                                ""
                        );

        String mensagemFinal =
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

        mensagemFinal =
                colorir(
                        mensagemFinal
                );

        for (Player destinatario :
                Bukkit.getOnlinePlayers()) {

            destinatario.sendMessage(
                    mensagemFinal
            );
        }

        plugin.getGerenciadorCooldown()
                .iniciarGlobal(
                        jogador.getUniqueId()
                );

        return true;
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