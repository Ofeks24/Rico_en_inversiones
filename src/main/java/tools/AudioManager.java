package tools;

import javax.sound.sampled.*;
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
    private Clip    musicClip;
    private String  currentMusic;
    private boolean musicEnabled = true;
    private boolean sfxEnabled   = true;
    private float   musicVolume  = 0.8f; // 0.0 a 1.0
    private float   sfxVolume    = 1.0f;

    private AudioManager() {}

    // =========================================================
    // MÚSICA DE FONDO (en bucle)
    // =========================================================

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

    public void stopMusic() {
        if (musicClip != null) {
            musicClip.stop();
            musicClip.close();
            musicClip = null;
        }
        currentMusic = null;
    }

    public void pauseMusic() {
        if (musicClip != null && musicClip.isRunning())
            musicClip.stop(); // conserva la posición
    }

    public void resumeMusic() {
        if (!musicEnabled) return;
        if (musicClip != null && !musicClip.isRunning())
            musicClip.start();
    }

    // =========================================================
    // EFECTOS DE SONIDO
    // =========================================================

    /**
     * Cada efecto abre su propio Clip en un hilo aparte
     * para no bloquear la UI y permitir solapamiento.
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

    // =========================================================
    // VOLUMEN  (0.0 = silencio, 1.0 = máximo)
    // =========================================================

    public void setMusicVolume(float vol) {
        musicVolume = clamp(vol);
        if (musicClip != null)
            setClipVolume(musicClip, musicVolume);
    }

    public void setSfxVolume(float vol) {
        sfxVolume = clamp(vol);
    }

    public float getMusicVolume() { return musicVolume; }
    public float getSfxVolume()   { return sfxVolume;   }

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