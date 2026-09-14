package dev.paulobernardes.simplesglobalchat.comandos;

import dev.paulobernardes.simplesglobalchat.SimplesGlobalChat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AjudaComando implements CommandExecutor {

    private final SimplesGlobalChat plugin;

    public AjudaComando(SimplesGlobalChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        sender.sendMessage("");

        sender.sendMessage(
                colorir("&6&l===== SimplesGlobalChat =====")
        );

        sender.sendMessage("");

        sender.sendMessage(
                colorir("&e/chat <global|local> &7- Ativa ou desativa um chat.")
        );

        sender.sendMessage(
                colorir("&e/broadcast <mensagem> &7- Envia uma mensagem para todos.")
        );

        sender.sendMessage(
                colorir("&e/aviso <mensagem> &7- Exibe um aviso na tela de todos.")
        );

        sender.sendMessage("");

        sender.sendMessage(
                colorir("&6&lAnúncios Automáticos")
        );

        sender.sendMessage("");

        sender.sendMessage(
                colorir("&e/criaranuncio <mensagem> <tempo> &7- Cria um anúncio automático.")
        );

        sender.sendMessage(
                colorir("&e/apagaranuncio <id> &7- Remove um anúncio automático.")
        );

        sender.sendMessage(
                colorir("&e/editaranuncio <id> <mensagem> <tempo> &7- Edita um anúncio.")
        );

        sender.sendMessage(
                colorir("&e/listaranuncios &7- Lista os anúncios cadastrados.")
        );

        sender.sendMessage("");

        sender.sendMessage(
                colorir("&7Tempo: &f30s, 5m, 30m, 1h, 2h, 1d")
        );

        sender.sendMessage("");

        return true;
    }

    private String colorir(String mensagem) {
        return ChatColor.translateAlternateColorCodes(
                '&',
                mensagem
        );
    }
}