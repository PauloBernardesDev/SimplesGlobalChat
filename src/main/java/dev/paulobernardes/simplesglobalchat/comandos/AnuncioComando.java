package dev.paulobernardes.simplesglobalchat.comandos;

import dev.paulobernardes.simplesglobalchat.SimplesGlobalChat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AnuncioComando implements CommandExecutor {

    private final SimplesGlobalChat plugin;
    private final Economy economia;

    public AnuncioComando(
            SimplesGlobalChat plugin,
            Economy economia
    ) {
        this.plugin = plugin;
        this.economia = economia;
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
                    "Este comando só pode ser usado por jogadores."
            );
            return true;
        }

        if (args.length == 0) {
            jogador.sendMessage(
                    colorir(
                            plugin.getConfig().getString(
                                    "mensagens.uso-anuncio",
                                    "&cUtilize: /anuncio <mensagem>"
                            )
                    )
            );
            return true;
        }

        boolean ativado = plugin.getConfig().getBoolean(
                "anuncio.ativado",
                true
        );

        if (!ativado) {
            jogador.sendMessage(
                    colorir(
                            "&cO sistema de anúncios está desativado."
                    )
            );
            return true;
        }

        double preco = plugin.getConfig().getDouble(
                "anuncio.preco",
                5000.0
        );

        if (!economia.has(jogador, preco)) {
            jogador.sendMessage(
                    colorir(
                            plugin.getConfig().getString(
                                    "mensagens.dinheiro-insuficiente",
                                    "&cVocê precisa de &e$%preco% &cpara enviar um anúncio."
                            ).replace(
                                    "%preco%",
                                    String.format("%.2f", preco)
                            )
                    )
            );
            return true;
        }

        String mensagem = String.join(" ", args);

        economia.withdrawPlayer(jogador, preco);

        String formato = plugin.getConfig().getString(
                "anuncio.formato",
                "&6&l[ANÚNCIO] &f%player%&7: &e%message%"
        );

        String mensagemFinal = formato
                .replace("%player%", jogador.getName())
                .replace("%message%", mensagem);

        mensagemFinal = colorir(mensagemFinal);

        for (Player destinatario : Bukkit.getOnlinePlayers()) {
            destinatario.sendMessage(mensagemFinal);
        }

        jogador.sendMessage(
                colorir(
                        plugin.getConfig().getString(
                                "mensagens.anuncio-enviado",
                                "&aSeu anúncio foi enviado por &e$%preco%&a."
                        ).replace(
                                "%preco%",
                                String.format("%.2f", preco)
                        )
                )
        );

        return true;
    }

    private String colorir(String mensagem) {
        return ChatColor.translateAlternateColorCodes(
                '&',
                mensagem
        );
    }
}