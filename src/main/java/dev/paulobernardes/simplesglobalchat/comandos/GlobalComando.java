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

    public GlobalComando(SimplesGlobalChat plugin, LuckPerms luckPerms) {
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
            sender.sendMessage("Este comando só pode ser usado por jogadores.");
            return true;
        }

        if (args.length == 0) {
            jogador.sendMessage(colorir(
                    plugin.getConfig().getString(
                            "mensagens.uso-g",
                            "&cUtilize: /g <mensagem>"
                    )
            ));
            return true;
        }

        if (!plugin.isChatGlobalAtivo()) {
            jogador.sendMessage(colorir(
                    plugin.getConfig().getString(
                            "mensagens.chat-global-desativado",
                            "&cO chat global está temporariamente desativado."
                    )
            ));
            return true;
        }

        String mensagemJogador = String.join(" ", args);

        mensagemJogador = processarMensagem(
                jogador,
                mensagemJogador
        );

        String prefixo = obterPrefixo(jogador);

        String formato = plugin.getConfig()
                .getString(
                        "chat.global.formato",
                        "&8[G] %prefix%&7%player%&8: &7%message%"
                );

        String mensagemFinal = formato
                .replace("%prefix%", prefixo)
                .replace("%player%", jogador.getName())
                .replace("%message%", mensagemJogador);

        mensagemFinal = colorir(mensagemFinal);

        for (Player destinatario : Bukkit.getOnlinePlayers()) {
            destinatario.sendMessage(mensagemFinal);
        }

        return true;
    }

    private String processarMensagem(Player jogador, String mensagem) {

        String permissaoCores = plugin.getConfig()
                .getString(
                        "permissoes.cores",
                        "simplesglobalchat.cores"
                );

        if (jogador.isOp() || jogador.hasPermission(permissaoCores)) {
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