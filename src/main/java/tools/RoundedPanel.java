package tools;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;


/**
 * Panel Swing con fondo redondeado semitransparente de color oscuro.
 * <p>
 * Se utiliza como superposición modal en pantallas como
 * {@link system.OptionsWindow} para crear el efecto de ventana flotante
 * sobre el fondo animado. Al ser no opaco ({@code setOpaque(false)}),
 * permite que el fondo se siga viendo a través de las esquinas.
 * </p>
 */
public class RoundedPanel extends JPanel {

    /** Radio de curvatura de las esquinas en píxeles. */
    private int radius;

    /**
     * Construye un panel redondeado con el radio indicado.
     *
     * @param radius radio de curvatura de las esquinas en píxeles
     */
    public RoundedPanel(int radius) {
        this.radius = radius;
        setOpaque(false); // IMPORTANTE
    }

    /**
     * Pinta el fondo redondeado semitransparente antes de que Swing
     * renderice los componentes hijos.
     * <p>
     * El fondo se dibuja en negro con 180/255 de opacidad
     * ({@code new Color(0, 0, 0, 180)}), lo que crea el efecto de
     * superposición oscura característica de las ventanas de opciones.
     * </p>
     *
     * @param g contexto gráfico proporcionado por Swing
     */
    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        // color oscuro semitransparente
        g2.setColor(new Color(0, 0, 0, 180));

        g2.fillRoundRect(
            0, 0,
            getWidth(),
            getHeight(),
            radius,
            radius
        );

        g2.dispose();

        super.paintComponent(g);
    }
}