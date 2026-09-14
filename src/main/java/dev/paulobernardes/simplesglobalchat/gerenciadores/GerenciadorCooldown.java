package dev.paulobernardes.simplesglobalchat.gerenciadores;

import dev.paulobernardes.simplesglobalchat.SimplesGlobalChat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GerenciadorCooldown {

    private final SimplesGlobalChat plugin;

    private final Map<UUID, Long> cooldownLocal =
            new HashMap<>();

    private final Map<UUID, Long> cooldownGlobal =
            new HashMap<>();

    private final Map<UUID, Long> cooldownAnuncio =
            new HashMap<>();

    public GerenciadorCooldown(SimplesGlobalChat plugin) {
        this.plugin = plugin;
    }

    public long verificarLocal(UUID uuid) {

        return verificar(
                cooldownLocal,
                uuid,
                plugin.getConfig().getLong(
                        "cooldown.local",
                        0
                )
        );
    }

    public long verificarGlobal(UUID uuid) {

        return verificar(
                cooldownGlobal,
                uuid,
                plugin.getConfig().getLong(
                        "cooldown.global",
                        3
                )
        );
    }

    public long verificarAnuncio(UUID uuid) {

        return verificar(
                cooldownAnuncio,
                uuid,
                plugin.getConfig().getLong(
                        "cooldown.anuncio",
                        60
                )
        );
    }

    public void iniciarLocal(UUID uuid) {

        iniciar(
                cooldownLocal,
                uuid
        );
    }

    public void iniciarGlobal(UUID uuid) {

        iniciar(
                cooldownGlobal,
                uuid
        );
    }

    public void iniciarAnuncio(UUID uuid) {

        iniciar(
                cooldownAnuncio,
                uuid
        );
    }

    private long verificar(
            Map<UUID, Long> mapa,
            UUID uuid,
            long segundos
    ) {

        if (segundos <= 0) {
            return 0;
        }

        Long ultimoUso = mapa.get(uuid);

        if (ultimoUso == null) {
            return 0;
        }

        long agora =
                System.currentTimeMillis();

        long intervalo =
                segundos * 1000L;

        long restante =
                intervalo - (agora - ultimoUso);

        if (restante <= 0) {
            return 0;
        }

        return (restante + 999) / 1000;
    }

    private void iniciar(
            Map<UUID, Long> mapa,
            UUID uuid
    ) {

        mapa.put(
                uuid,
                System.currentTimeMillis()
        );
    }

    public void limpar(UUID uuid) {

        cooldownLocal.remove(uuid);
        cooldownGlobal.remove(uuid);
        cooldownAnuncio.remove(uuid);
    }
}