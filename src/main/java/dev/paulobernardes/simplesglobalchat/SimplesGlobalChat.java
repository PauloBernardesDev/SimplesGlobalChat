package dev.paulobernardes.simplesglobalchat;

import dev.paulobernardes.simplesglobalchat.gerenciadores.GerenciadorCooldown;
import dev.paulobernardes.simplesglobalchat.comandos.AnuncioAutomaticoComando;
import dev.paulobernardes.simplesglobalchat.comandos.AnuncioComando;
import dev.paulobernardes.simplesglobalchat.comandos.ChatComando;
import dev.paulobernardes.simplesglobalchat.comandos.GlobalComando;
import dev.paulobernardes.simplesglobalchat.eventos.ChatListener;
import dev.paulobernardes.simplesglobalchat.gerenciadores.GerenciadorAnuncios;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class SimplesGlobalChat extends JavaPlugin {

    private LuckPerms luckPerms;
    private Economy economia;
    private GerenciadorAnuncios gerenciadorAnuncios;
    private boolean chatGlobalAtivo = true;
    private boolean chatLocalAtivo = true;
    private GerenciadorCooldown gerenciadorCooldown;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        if (!inicializarLuckPerms()) {

            getLogger().severe(
                    "LuckPerms não foi encontrado!"
            );

            getServer().getPluginManager().disablePlugin(this);

            return;
        }

        if (!inicializarEconomia()) {

            getLogger().severe(
                    "Vault ou uma economia compatível não foi encontrada!"
            );

            getServer().getPluginManager().disablePlugin(this);

            return;
        }

        gerenciadorAnuncios =
                new GerenciadorAnuncios(this);

        gerenciadorCooldown = new GerenciadorCooldown(this);

        getServer().getPluginManager().registerEvents(
                new ChatListener(this, luckPerms),
                this
        );

        getCommand("g").setExecutor(
                new GlobalComando(
                        this,
                        luckPerms
                )
        );

        ChatComando chatComando =
                new ChatComando(this);

        getCommand("chat").setExecutor(
                chatComando
        );

        getCommand("chat").setTabCompleter(
                chatComando
        );

        AnuncioComando anuncioComando =
                new AnuncioComando(
                        this,
                        economia
                );

        getCommand("anuncio").setExecutor(
                anuncioComando
        );

        AnuncioAutomaticoComando anuncioAutomaticoComando =
                new AnuncioAutomaticoComando(
                        this,
                        gerenciadorAnuncios
                );

        getCommand("criaranuncio").setExecutor(
                anuncioAutomaticoComando
        );

        getCommand("criaranuncio").setTabCompleter(
                anuncioAutomaticoComando
        );

        getCommand("apagaranuncio").setExecutor(
                anuncioAutomaticoComando
        );

        getCommand("apagaranuncio").setTabCompleter(
                anuncioAutomaticoComando
        );

        getCommand("editaranuncio").setExecutor(
                anuncioAutomaticoComando
        );

        getCommand("editaranuncio").setTabCompleter(
                anuncioAutomaticoComando
        );

        getCommand("listaranuncios").setExecutor(
                anuncioAutomaticoComando
        );

        getCommand("listaranuncios").setTabCompleter(
                anuncioAutomaticoComando
        );

        getLogger().info(
                "SimplesGlobalChat ativado!"
        );
    }

    @Override
    public void onDisable() {

        if (gerenciadorAnuncios != null) {

            gerenciadorAnuncios.desligar();
        }

        getLogger().info(
                "SimplesGlobalChat desativado!"
        );
    }

    private boolean inicializarLuckPerms() {

        RegisteredServiceProvider<LuckPerms> registro =
                getServer()
                        .getServicesManager()
                        .getRegistration(
                                LuckPerms.class
                        );

        if (registro == null) {
            return false;
        }

        luckPerms =
                registro.getProvider();

        return luckPerms != null;
    }

    private boolean inicializarEconomia() {

        RegisteredServiceProvider<Economy> registro =
                getServer()
                        .getServicesManager()
                        .getRegistration(
                                Economy.class
                        );

        if (registro == null) {
            return false;
        }

        economia =
                registro.getProvider();

        return economia != null;
    }

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }

    public Economy getEconomia() {
        return economia;
    }

    public GerenciadorAnuncios getGerenciadorAnuncios() {
        return gerenciadorAnuncios;
    }

    public boolean isChatGlobalAtivo() {
        return chatGlobalAtivo;
    }

    public void setChatGlobalAtivo(
            boolean chatGlobalAtivo
    ) {
        this.chatGlobalAtivo =
                chatGlobalAtivo;
    }

    public boolean isChatLocalAtivo() {
        return chatLocalAtivo;
    }

    public GerenciadorCooldown getGerenciadorCooldown() {
        return gerenciadorCooldown;
    }

    public void setChatLocalAtivo(
            boolean chatLocalAtivo
    ) {
        this.chatLocalAtivo =
                chatLocalAtivo;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {

        if (!command.getName().equalsIgnoreCase("aviso")) {
            return false;
        }

        if (!sender.isOp()
                && !sender.hasPermission(
                "simplesglobalchat.aviso"
        )) {

            sender.sendMessage(
                    ChatColor.RED
                            + "Você não tem permissão para usar este comando."
            );

            return true;
        }

        if (args.length == 0) {

            sender.sendMessage(
                    ChatColor.RED
                            + "Uso: /aviso <mensagem>"
            );

            return true;
        }

        String mensagem =
                String.join(
                        " ",
                        args
                );

        mensagem =
                ChatColor.translateAlternateColorCodes(
                        '&',
                        mensagem
                );

        int fadeIn =
                getConfig().getInt(
                        "aviso.fade-in",
                        10
                );

        int stay =
                getConfig().getInt(
                        "aviso.stay",
                        70
                );

        int fadeOut =
                getConfig().getInt(
                        "aviso.fade-out",
                        20
                );

        for (Player jogador :
                Bukkit.getOnlinePlayers()) {

            jogador.sendTitle(
                    mensagem,
                    "",
                    fadeIn,
                    stay,
                    fadeOut
            );
        }

        return true;
    }
}