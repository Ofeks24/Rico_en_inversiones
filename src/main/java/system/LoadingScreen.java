package system;

import java.awt.*;
import javax.swing.*;


/**
 * Pantalla de carga inicial que muestra el logotipo del juego con una
 * animación de aparición gradual (fade-in).
 *
 * <p>El alpha del logo aumenta de 0 a 1 en pasos de 0,02 cada 30 ms.
 * Al alcanzar la opacidad máxima, espera 2 segundos y ejecuta el
 * callback {@code onFinish} para que {@link tools.ScreenManager} pase
 * al menú principal.</p>
 */
public class LoadingScreen extends JPanel {

    private float alpha = 0f;
    private Image logo;
    private Runnable onFinish; // callback para cambiar de pantalla

    /**
     * Construye la pantalla de carga cargando el recurso del logotipo y
     * almacenando el callback de finalización.
     *
     * @param onFinish {@link Runnable} que se invoca cuando la animación
     *                 termina y el tiempo de espera ha transcurrido.
     *                 Normalmente muestra la pantalla del menú principal.
     */
    public LoadingScreen(Runnable onFinish) {
        this.onFinish = onFinish;

        logo = new ImageIcon(
        	    getClass().getResource("/main/resources/logos/theIMPERIALOne(TEXTO)_logo (pequeño).png")
        	).getImage();
        if (logo == null) return;
        setFocusable(true);
    }

    /**
     * Pinta el fondo negro sólido y el logotipo centrado con el nivel de
     * alpha actual, produciendo el efecto de fade-in.
     *
     * @param g Contexto gráfico proporcionado por el sistema Swing.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setColor(new Color(0,0,0,255));
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.setComposite(
            AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha)
        );

        int imgW = logo.getWidth(null);
        int imgH = logo.getHeight(null);

        int x = (getWidth() - imgW) / 2;
        int y = (getHeight() - imgH) / 2;

        g2.drawImage(logo, x, y, null);

        g2.dispose();
    }

    /**
     * Inicia la animación de fade-in del logotipo.
     *
     * <p>Un {@link Timer} incrementa el campo {@code alpha} cada 30 ms.
     * Cuando {@code alpha} llega a 1,0 el timer se detiene y se programa
     * otro timer de un solo disparo (2 000 ms) tras el cual se ejecuta
     * {@code onFinish}.</p>
     */
    public void startAnimation() {
        Timer timer = new Timer(30, null);

        timer.addActionListener(e -> {

            alpha += 0.02f;

            if (alpha >= 1f) {
                alpha = 1f;
                timer.stop();

                // Espera antes de cambiar
                Timer espera = new Timer(2000, ev -> {
                    if (onFinish != null) {
                        onFinish.run(); // cambia de pantalla
                    }
                });

                espera.setRepeats(false);
                espera.start();
                return;
            }
        });

        timer.start();
    }
}