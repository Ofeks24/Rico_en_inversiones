package tools;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;


/**
 * Gestor de audio singleton de la aplicación.
 * <p>
 * Proporciona reproducción de música de fondo en bucle y efectos de sonido
 * (SFX) puntuales. La música se gestiona con un único {@link Clip} reutilizable;
 * los efectos de sonido se reproducen en hilos independientes para permitir
 * solapamiento sin bloquear la interfaz.
 * </p>
 * <p>
 * El volumen se controla en escala lineal (0.0–1.0) y se convierte
 * internamente a decibelios usando {@code MASTER_GAIN}. Tanto la música
 * como los SFX pueden activarse o desactivarse de forma independiente.
 * </p>
 */
public class AudioManager {

    /** Instancia única del singleton. */
    private static AudioManager instance;

    /** Clip activo para la música de fondo. {@code null} si no hay música. */
    private Clip    musicClip;

    /** Ruta classpath de la música actualmente en reproducción. */
    private String  currentMusic;

    /** Indica si la música de fondo está habilitada. */
    private boolean musicEnabled = true;

    /** Indica si los efectos de sonido están habilitados. */
    private boolean sfxEnabled   = true;

    /** Volumen de la música en escala 0.0–1.0. */
    private float   musicVolume  = 0.8f; // 0.0 a 1.0
    
    /** Volumen de los efectos de sonido en escala 0.0–1.0. */
    private float   sfxVolume    = 1.0f;

    /**
     * Constructor privado para garantizar el patrón singleton.
     */
    private AudioManager() {}

    /**
     * Devuelve la instancia única del {@code AudioManager}, creándola si
     * todavía no existe.
     *
     * @return instancia singleton del gestor de audio
     */
    public static AudioManager getInstance() {
        if (instance == null) instance = new AudioManager();
        return instance;
    }

    /**
     * Inicia la reproducción de música de fondo en bucle continuo.
     * <p>
     * Si la música indicada ya está sonando, el método no hace nada.
     * Detiene cualquier música anterior antes de iniciar la nueva.
     * No reproduce nada si la música está deshabilitada.
     * </p>
     *
     * @param rutaClasspath ruta del recurso de audio en el classpath
     *                      (ej. {@code "/main/resources/audio/music/theme.wav"})
     */
    public void playMusic(String rutaClasspath) {
        if (!musicEnabled) return;
        if (rutaClasspath.equals(currentMusic)
                && musicClip != null
                && musicClip.isRunning()) return;

        stopMusic();

        try {
            InputStream raw = AudioManager.class
                    .getResourceAsStream(rutaClasspath);
            if (raw == null) {
                System.err.println("[AudioManager] No encontrado: "
                        + rutaClasspath);
                return;
            }

            AudioInputStream ais = AudioSystem.getAudioInputStream(
                    new BufferedInputStream(raw)
            );

            musicClip = AudioSystem.getClip();
            musicClip.open(ais);
            setClipVolume(musicClip, musicVolume);
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
            musicClip.start();

            currentMusic = rutaClasspath;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Detiene y libera el clip de música en reproducción.
     * Si no hay música activa, el método no hace nada.
     */
    public void stopMusic() {
        if (musicClip != null) {
            musicClip.stop();
            musicClip.close();
            musicClip = null;
        }
        currentMusic = null;
    }

    /**
     * Pausa la música en la posición actual sin liberar el clip,
     * de modo que pueda reanudarse con {@link #resumeMusic()}.
     */
    public void pauseMusic() {
        if (musicClip != null && musicClip.isRunning())
            musicClip.stop(); // conserva la posición
    }

    /**
     * Reanuda la música pausada desde la posición en que se detuvo.
     * No hace nada si la música está deshabilitada o ya está en reproducción.
     */
    public void resumeMusic() {
        if (!musicEnabled) return;
        if (musicClip != null && !musicClip.isRunning())
            musicClip.start();
    }

    // =========================================================
    // EFECTOS DE SONIDO
    // =========================================================

    /**
     * Reproduce un efecto de sonido de forma asíncrona en un hilo propio.
     * <p>
     * Cada llamada abre un clip independiente, lo que permite que varios
     * efectos se superpongan sin interferir entre sí ni con la música.
     * El clip se cierra automáticamente al finalizar la reproducción.
     * No reproduce nada si los SFX están deshabilitados.
     * </p>
     *
     * @param rutaClasspath ruta del recurso de audio en el classpath
     *                      (ej. {@code "/main/resources/audio/sfx/click.wav"})
     */
    public void playSfx(String rutaClasspath) {
        if (!sfxEnabled) return;

        new Thread(() -> {
            try {
                InputStream raw = AudioManager.class
                        .getResourceAsStream(rutaClasspath);
                if (raw == null) {
                    System.err.println("[AudioManager] No encontrado: "
                            + rutaClasspath);
                    return;
                }

                AudioInputStream ais = AudioSystem.getAudioInputStream(
                        new BufferedInputStream(raw)
                );

                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                setClipVolume(clip, sfxVolume);

                // Cerrar el clip automáticamente al terminar
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) {
                        clip.close();
                    }
                });

                clip.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "sfx-thread").start();
    }

    /**
     * Establece el volumen de la música y lo aplica inmediatamente si
     * hay un clip en reproducción.
     *
     * @param vol nuevo volumen en escala 0.0 (silencio) a 1.0 (máximo)
     */
    public void setMusicVolume(float vol) {
        musicVolume = clamp(vol);
        if (musicClip != null)
            setClipVolume(musicClip, musicVolume);
    }

    /**
     * Establece el volumen de los efectos de sonido para las siguientes
     * reproducciones.
     *
     * @param vol nuevo volumen en escala 0.0 (silencio) a 1.0 (máximo)
     */
    public void setSfxVolume(float vol) {
        sfxVolume = clamp(vol);
    }

    /**
     * Devuelve el volumen actual de la música.
     *
     * @return volumen de música (0.0–1.0)
     */
    public float getMusicVolume() { return musicVolume; }

    /**
     * Devuelve el volumen actual de los efectos de sonido.
     *
     * @return volumen de SFX (0.0–1.0)
     */
    public float getSfxVolume()   { return sfxVolume;   }

    /**
     * Habilita o deshabilita la música de fondo.
     * Si se deshabilita mientras hay música sonando, la detiene.
     *
     * @param enabled {@code true} para habilitar; {@code false} para deshabilitar
     */
    public void setMusicEnabled(boolean enabled) {
        musicEnabled = enabled;
        if (!enabled) stopMusic();
    }

    /**
     * Habilita o deshabilita los efectos de sonido.
     *
     * @param enabled {@code true} para habilitar; {@code false} para deshabilitar
     */
    public void setSfxEnabled(boolean enabled) {
        sfxEnabled = enabled;
    }

    /**
     * Indica si la música de fondo está habilitada.
     *
     * @return {@code true} si la música está activa
     */
    public boolean isMusicEnabled() { return musicEnabled; }

    /**
     * Indica si los efectos de sonido están habilitados.
     *
     * @return {@code true} si los SFX están activos
     */
    public boolean isSfxEnabled()   { return sfxEnabled;   }

    // =========================================================
    // INTERNO
    // =========================================================

    private void setClipVolume(Clip clip, float vol) {
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gain = (FloatControl)
                    clip.getControl(FloatControl.Type.MASTER_GAIN);
            // Convertir 0.0-1.0 a decibelios (rango típico: -80 a 6 dB)
            float dB = (float)(Math.log10(Math.max(vol, 0.0001)) * 20);
            gain.setValue(Math.max(gain.getMinimum(),
                          Math.min(gain.getMaximum(), dB)));
        }
    }

    private float clamp(float v) {
        return Math.max(0f, Math.min(1f, v));
    }
}