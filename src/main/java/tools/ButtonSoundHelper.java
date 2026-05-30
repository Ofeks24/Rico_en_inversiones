package tools;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


/**
 * Utilidad estática para añadir efectos de sonido a botones Swing.
 * <p>
 * Centraliza la lógica de reproducción de sonidos de interfaz, evitando
 * duplicar listeners en cada punto de la aplicación donde se necesite
 * retroalimentación sonora al pasar el cursor sobre un botón.
 * </p>
 */
public class ButtonSoundHelper {

    /**
     * Registra un listener de ratón en el botón indicado para reproducir
     * un efecto de sonido cada vez que el cursor entra en su área.
     * <p>
     * El sonido se reproduce a través de {@link AudioManager#playSfx(String)},
     * por lo que respeta el estado de habilitación y el volumen configurados
     * en dicho gestor de audio.
     * </p>
     *
     * @param button    el botón al que se añade el sonido de hover
     * @param soundPath ruta del recurso de audio en el classpath
     *                  (ej. {@code "/main/resources/audio/sfx/ping-menu-sound.wav"})
     */
    public static void addHoverSound(JButton button, String soundPath) {

        button.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                AudioManager.getInstance().playSfx(soundPath);
            }
        });
    }
}