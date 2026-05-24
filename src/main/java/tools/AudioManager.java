package tools;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.BufferedInputStream;
import java.io.InputStream;

public class AudioManager {

    // ── Singleton ─────────────────────────────────────────
    private static AudioManager instance;

    public static AudioManager getInstance() {
        if (instance == null) instance = new AudioManager();
        return instance;
    }

    // ── Estado ────────────────────────────────────────────
    private Thread      musicThread;
    private AdvancedPlayer musicPlayer;
    private String      currentMusic;
    private boolean     musicEnabled = true;
    private boolean     sfxEnabled   = true;
    private float       musicVolume  = 1.0f; // reservado para futuro
    private volatile boolean looping = false;

    private AudioManager() {}

    // =========================================================
    // MÚSICA DE FONDO (en bucle)
    // =========================================================

    /**
     * Reproduce una pista de música en bucle.
     * Si ya suena la misma pista, no hace nada.
     * Ruta relativa al classpath: "/audio/music/menu.mp3"
     */
    public void playMusic(String rutaClasspath) {
        if (!musicEnabled) return;
        if (rutaClasspath.equals(currentMusic)) return;

        stopMusic();

        currentMusic = rutaClasspath;
        looping      = true;

        musicThread = new Thread(() -> {
            while (looping) {
                try {
                    InputStream raw = AudioManager.class
                            .getResourceAsStream(rutaClasspath);
                    if (raw == null) {
                        System.err.println("[AudioManager] "
                                + "No se encontró: " + rutaClasspath);
                        return;
                    }
                    BufferedInputStream bis =
                            new BufferedInputStream(raw);
                    musicPlayer = new AdvancedPlayer(bis);
                    musicPlayer.setPlayBackListener(
                        new PlaybackListener() {
                            @Override
                            public void playbackFinished(PlaybackEvent e) {
                                // el bucle lo controla el while
                            }
                        }
                    );
                    musicPlayer.play(); // bloquea hasta que termina
                } catch (JavaLayerException e) {
                    if (looping) e.printStackTrace();
                    // Si looping es false, es un stop() intencionado
                }
            }
        });

        musicThread.setDaemon(true); // se cierra con la app
        musicThread.start();
    }

    public void stopMusic() {
        looping = false;
        if (musicPlayer != null) {
            musicPlayer.close(); // interrumpe el play() bloqueante
        }
        if (musicThread != null) {
            musicThread.interrupt();
        }
        currentMusic = null;
    }

    public void pauseMusic() {
        // JLayer no soporta pausa real; lo más limpio es stop + recordar posición
        // Para un juego sencillo, stop es suficiente
        stopMusic();
    }

    // =========================================================
    // EFECTOS DE SONIDO (no bloquean el hilo principal)
    // =========================================================

    /**
     * Reproduce un efecto de sonido una sola vez.
     * Cada llamada lanza un hilo independiente para no bloquear la UI.
     */
    public void playSfx(String rutaClasspath) {
        if (!sfxEnabled) return;

        Thread sfxThread = new Thread(() -> {
            try {
                InputStream raw = AudioManager.class
                        .getResourceAsStream(rutaClasspath);
                if (raw == null) {
                    System.err.println("[AudioManager] "
                            + "No se encontró: " + rutaClasspath);
                    return;
                }
                AdvancedPlayer player =
                        new AdvancedPlayer(new BufferedInputStream(raw));
                player.play();
            } catch (JavaLayerException e) {
                e.printStackTrace();
            }
        });

        sfxThread.setDaemon(true);
        sfxThread.start();
    }

    // =========================================================
    // ACTIVAR / DESACTIVAR
    // =========================================================

    public void setMusicEnabled(boolean enabled) {
        musicEnabled = enabled;
        if (!enabled) stopMusic();
    }

    public void setSfxEnabled(boolean enabled) {
        sfxEnabled = enabled;
    }

    public boolean isMusicEnabled() { return musicEnabled; }
    public boolean isSfxEnabled()   { return sfxEnabled;   }
}