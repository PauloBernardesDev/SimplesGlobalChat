package dev.paulobernardes.simplesglobalchat.gerenciadores;

import dev.paulobernardes.simplesglobalchat.SimplesGlobalChat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GerenciadorAnuncios {

    private final SimplesGlobalChat plugin;

    private File arquivo;
    private FileConfiguration configuracao;

    private final List<BukkitTask> tarefas = new ArrayList<>();

    public GerenciadorAnuncios(SimplesGlobalChat plugin) {
        this.plugin = plugin;

        criarArquivo();

        carregarAnuncios();
    }

    private void criarArquivo() {

        arquivo = new File(
                plugin.getDataFolder(),
                "anuncios.yml"
        );

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        if (!arquivo.exists()) {

            try {

                arquivo.createNewFile();

                configuracao =
                        YamlConfiguration.loadConfiguration(
                                arquivo
                        );

                configuracao.set(
                        "configuracao.prefixo",
                        "&6&l[AVISO] &r"
                );

                configuracao.createSection(
                        "anuncios"
                );

                configuracao.save(arquivo);

            } catch (IOException e) {

                plugin.getLogger().severe(
                        "Não foi possível criar o arquivo anuncios.yml."
                );

                e.printStackTrace();
            }

        } else {

            configuracao =
                    YamlConfiguration.loadConfiguration(
                            arquivo
                    );

            if (!configuracao.contains(
                    "configuracao.prefixo"
            )) {

                configuracao.set(
                        "configuracao.prefixo",
                        "&6&l[AVISO] &r"
                );

                salvar();
            }

            if (!configuracao.contains("anuncios")) {

                configuracao.createSection(
                        "anuncios"
                );

                salvar();
            }
        }
    }

    public void carregarAnuncios() {

        cancelarTarefas();

        configuracao =
                YamlConfiguration.loadConfiguration(
                        arquivo
                );

        ConfigurationSection secao =
                configuracao.getConfigurationSection(
                        "anuncios"
                );

        if (secao == null) {
            return;
        }

        for (String id :
                secao.getKeys(false)) {

            String caminho =
                    "anuncios." + id;

            String mensagem =
                    configuracao.getString(
                            caminho + ".mensagem"
                    );

            String intervalo =
                    configuracao.getString(
                            caminho + ".intervalo"
                    );

            boolean ativo =
                    configuracao.getBoolean(
                            caminho + ".ativo",
                            true
                    );

            if (mensagem == null
                    || intervalo == null) {
                continue;
            }

            if (!ativo) {
                continue;
            }

            long ticks =
                    converterTempo(intervalo);

            if (ticks <= 0) {

                plugin.getLogger().warning(
                        "Intervalo inválido no anúncio "
                                + id
                                + ": "
                                + intervalo
                );

                continue;
            }

            iniciarAnuncio(
                    mensagem,
                    ticks
            );
        }
    }

    private void iniciarAnuncio(
            String mensagem,
            long ticks
    ) {

        BukkitTask tarefa =
                Bukkit.getScheduler().runTaskTimer(
                        plugin,
                        () -> {

                            String prefixo =
                                    configuracao.getString(
                                            "configuracao.prefixo",
                                            "&6&l[AVISO] &r"
                                    );

                            String mensagemFinal =
                                    prefixo + mensagem;

                            mensagemFinal =
                                    ChatColor.translateAlternateColorCodes(
                                            '&',
                                            mensagemFinal
                                    );

                            for (Player jogador :
                                    Bukkit.getOnlinePlayers()) {

                                jogador.sendMessage(
                                        mensagemFinal
                                );
                            }
                        },
                        ticks,
                        ticks
                );

        tarefas.add(tarefa);
    }

    public boolean criarAnuncio(
            String mensagem,
            String intervalo
    ) {

        long ticks =
                converterTempo(intervalo);

        if (ticks <= 0) {
            return false;
        }

        ConfigurationSection secao =
                configuracao.getConfigurationSection(
                        "anuncios"
                );

        if (secao == null) {

            configuracao.createSection(
                    "anuncios"
            );
        }

        String id =
                gerarId();

        String caminho =
                "anuncios." + id;

        configuracao.set(
                caminho + ".mensagem",
                mensagem
        );

        configuracao.set(
                caminho + ".intervalo",
                intervalo
        );

        configuracao.set(
                caminho + ".ativo",
                true
        );

        salvar();

        carregarAnuncios();

        return true;
    }

    public boolean apagarAnuncio(
            String id
    ) {

        String caminho =
                "anuncios." + id;

        if (!configuracao.contains(
                caminho
        )) {

            return false;
        }

        configuracao.set(
                caminho,
                null
        );

        salvar();

        carregarAnuncios();

        return true;
    }

    public boolean editarAnuncio(
            String id,
            String mensagem,
            String intervalo
    ) {

        long ticks =
                converterTempo(intervalo);

        if (ticks <= 0) {
            return false;
        }

        String caminho =
                "anuncios." + id;

        if (!configuracao.contains(
                caminho
        )) {

            return false;
        }

        configuracao.set(
                caminho + ".mensagem",
                mensagem
        );

        configuracao.set(
                caminho + ".intervalo",
                intervalo
        );

        salvar();

        carregarAnuncios();

        return true;
    }

    public List<String> listarAnuncios() {

        List<String> anuncios =
                new ArrayList<>();

        ConfigurationSection secao =
                configuracao.getConfigurationSection(
                        "anuncios"
                );

        if (secao == null) {
            return anuncios;
        }

        anuncios.addAll(
                secao.getKeys(false)
        );

        return anuncios;
    }

    private String gerarId() {

        int numero = 1;

        while (
                configuracao.contains(
                        "anuncios.anuncio-" + numero
                )
        ) {

            numero++;
        }

        return "anuncio-" + numero;
    }

    private long converterTempo(
            String tempo
    ) {

        if (tempo == null
                || tempo.isEmpty()) {

            return -1;
        }

        tempo =
                tempo.toLowerCase();

        try {

            long valor =
                    Long.parseLong(
                            tempo.substring(
                                    0,
                                    tempo.length() - 1
                            )
                    );

            char unidade =
                    tempo.charAt(
                            tempo.length() - 1
                    );

            if (valor <= 0) {
                return -1;
            }

            return switch (unidade) {

                case 's' ->
                        valor * 20;

                case 'm' ->
                        valor * 60 * 20;

                case 'h' ->
                        valor * 60 * 60 * 20;

                case 'd' ->
                        valor * 24 * 60 * 60 * 20;

                default ->
                        -1;
            };

        } catch (NumberFormatException e) {

            return -1;
        }
    }

    private void cancelarTarefas() {

        for (BukkitTask tarefa :
                tarefas) {

            tarefa.cancel();
        }

        tarefas.clear();
    }

    private void salvar() {

        try {

            configuracao.save(
                    arquivo
            );

        } catch (IOException e) {

            plugin.getLogger().severe(
                    "Não foi possível salvar o anuncios.yml."
            );

            e.printStackTrace();
        }
    }

    public void desligar() {
        cancelarTarefas();
    }
}