package dev.paulobernardes.simplesglobalchat.comandos;

import dev.paulobernardes.simplesglobalchat.SimplesGlobalChat;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class ChatComando implements CommandExecutor, TabCompleter {

    private final SimplesGlobalChat plugin;

    public ChatComando(SimplesGlobalChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (args.length != 1) {
            sender.sendMessage(
                    ChatColor.RED + "Uso: /chat <global|local|help>"
            );
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "global":

                if (!sender.isOp()
                        && !sender.hasPermission("simplesglobalchat.admin")) {

                    sender.sendMessage(
                            ChatColor.RED
                                    + "Você não tem permissão para usar este comando."
                    );

                    return true;
                }

                plugin.setChatGlobalAtivo(
                        !plugin.isChatGlobalAtivo()
                );

                if (plugin.isChatGlobalAtivo()) {
                    sender.sendMessage(
                            ChatColor.GREEN + "Chat global ativado."
                    );
                } else {
                    sender.sendMessage(
                            ChatColor.RED + "Chat global desativado."
                    );
                }

                break;

            case "local":

                if (!sender.isOp()
                        && !sender.hasPermission("simplesglobalchat.admin")) {

                    sender.sendMessage(
                            ChatColor.RED
                                    + "Você não tem permissão para usar este comando."
                    );

                    return true;
                }

                plugin.setChatLocalAtivo(
                        !plugin.isChatLocalAtivo()
                );

                if (plugin.isChatLocalAtivo()) {
                    sender.sendMessage(
                            ChatColor.GREEN + "Chat local ativado."
                    );
                } else {
                    sender.sendMessage(
                            ChatColor.RED + "Chat local desativado."
                    );
                }

                break;

            case "help":

                if (!sender.isOp()
                        && !sender.hasPermission("simplesglobalchat.help")) {

                    sender.sendMessage(
                            ChatColor.RED
                                    + "Você não tem permissão para usar este comando."
                    );

                    return true;
                }

                mostrarAjuda(sender);

                break;

            default:

                sender.sendMessage(
                        ChatColor.RED + "Uso: /chat <global|local|help>"
                );

                break;
        }

        return true;
    }

    private void mostrarAjuda(CommandSender sender) {

        sender.sendMessage("");

        sender.sendMessage(
                colorir("&6&l===== SimplesGlobalChat =====")
        );

        sender.sendMessage("");

        sender.sendMessage(
                colorir("&e/chat <global|local> &7- Ativa ou desativa um chat.")
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
    }

    private String colorir(String mensagem) {
        return ChatColor.translateAlternateColorCodes(
                '&',
                mensagem
        );
    }

    @Override
    public List<String> onTabComplete(
            CommandSender sender,
            Command command,
            String alias,
            String[] args
    ) {

        if (args.length == 1) {

            List<String> opcoes = new ArrayList<>();

            opcoes.add("global");
            opcoes.add("local");
            opcoes.add("help");

            String digitado = args[0].toLowerCase();

            opcoes.removeIf(opcao ->
                    !opcao.startsWith(digitado)
            );

            return opcoes;
        }

        return new ArrayList<>();
    }
}