package dev.paulobernardes.simplesglobalchat.comandos;

import dev.paulobernardes.simplesglobalchat.SimplesGlobalChat;
import dev.paulobernardes.simplesglobalchat.gerenciadores.GerenciadorAnuncios;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnuncioAutomaticoComando implements CommandExecutor, TabCompleter {

    private final SimplesGlobalChat plugin;
    private final GerenciadorAnuncios gerenciadorAnuncios;

    public AnuncioAutomaticoComando(
            SimplesGlobalChat plugin,
            GerenciadorAnuncios gerenciadorAnuncios
    ) {
        this.plugin = plugin;
        this.gerenciadorAnuncios = gerenciadorAnuncios;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (!sender.isOp() && !sender.hasPermission("simplesglobalchat.anuncios")) {
            sender.sendMessage(
                    colorir("&cVocê não possui permissão para isso.")
            );
            return true;
        }

        switch (command.getName().toLowerCase()) {

            case "criaranuncio":
                criarAnuncio(sender, args);
                break;

            case "apagaranuncio":
                apagarAnuncio(sender, args);
                break;

            case "editaranuncio":
                editarAnuncio(sender, args);
                break;

            case "listaranuncios":
                listarAnuncios(sender);
                break;

            default:
                return false;
        }

        return true;
    }

    private void criarAnuncio(
            CommandSender sender,
            String[] args
    ) {

        if (args.length < 2) {
            sender.sendMessage(
                    colorir("&cUtilize: /criaranuncio <mensagem> <tempo>")
            );
            return;
        }

        String intervalo = args[args.length - 1];

        String mensagem = String.join(
                " ",
                Arrays.copyOf(
                        args,
                        args.length - 1
                )
        );

        mensagem = removerAspas(mensagem);

        if (!gerenciadorAnuncios.criarAnuncio(
                mensagem,
                intervalo
        )) {

            sender.sendMessage(
                    colorir("&cIntervalo inválido. Use formatos como: &e30s, 5m, 30m, 1h, 2h, 1d")
            );

            return;
        }

        sender.sendMessage(
                colorir(
                        "&aAnúncio automático criado com sucesso!"
                )
        );
    }

    private void apagarAnuncio(
            CommandSender sender,
            String[] args
    ) {

        if (args.length != 1) {
            sender.sendMessage(
                    colorir("&cUtilize: /apagaranuncio <id>")
            );
            return;
        }

        String id = args[0];

        if (!gerenciadorAnuncios.apagarAnuncio(id)) {

            sender.sendMessage(
                    colorir("&cO anúncio &e" + id + " &cnão existe.")
            );

            return;
        }

        sender.sendMessage(
                colorir(
                        "&aAnúncio &e" + id + " &aapagado com sucesso!"
                )
        );
    }

    private void editarAnuncio(
            CommandSender sender,
            String[] args
    ) {

        if (args.length < 3) {
            sender.sendMessage(
                    colorir("&cUtilize: /editaranuncio <id> <mensagem> <tempo>")
            );
            return;
        }

        String id = args[0];

        String intervalo = args[args.length - 1];

        String mensagem = String.join(
                " ",
                Arrays.copyOfRange(
                        args,
                        1,
                        args.length - 1
                )
        );

        mensagem = removerAspas(mensagem);

        if (!gerenciadorAnuncios.editarAnuncio(
                id,
                mensagem,
                intervalo
        )) {

            sender.sendMessage(
                    colorir(
                            "&cNão foi possível editar. Verifique o ID e o intervalo."
                    )
            );

            return;
        }

        sender.sendMessage(
                colorir(
                        "&aAnúncio &e" + id + " &aeditado com sucesso!"
                )
        );
    }

    private void listarAnuncios(
            CommandSender sender
    ) {

        List<String> anuncios =
                gerenciadorAnuncios.listarAnuncios();

        if (anuncios.isEmpty()) {

            sender.sendMessage(
                    colorir("&7Não existem anúncios automáticos cadastrados.")
            );

            return;
        }

        sender.sendMessage(
                colorir("&5&m------------------------------")
        );

        sender.sendMessage(
                colorir("&5&lAnúncios Automáticos")
        );

        sender.sendMessage("");

        for (String id : anuncios) {

            sender.sendMessage(
                    colorir("&7- &e" + id)
            );
        }

        sender.sendMessage("");

        sender.sendMessage(
                colorir("&5&m------------------------------")
        );
    }

    private String removerAspas(String mensagem) {

        mensagem = mensagem.trim();

        if (mensagem.startsWith("\"") && mensagem.endsWith("\"")) {

            mensagem = mensagem.substring(
                    1,
                    mensagem.length() - 1
            );
        }

        return mensagem;
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

        if (command.getName().equalsIgnoreCase("apagaranuncio")
                || command.getName().equalsIgnoreCase("editaranuncio")) {

            if (args.length == 1) {

                List<String> ids =
                        new ArrayList<>(
                                gerenciadorAnuncios.listarAnuncios()
                        );

                String digitado = args[0].toLowerCase();

                ids.removeIf(id ->
                        !id.toLowerCase().startsWith(digitado)
                );

                return ids;
            }
        }

        return new ArrayList<>();
    }
}