package system;

import java.awt.*;
import javax.swing.*;

public class LoadingScreen extends JPanel {

    private float alpha = 0f;
    private Image logo;
    private Runnable onFinish; // callback para cambiar de pantalla

    public LoadingScreen(Runnable onFinish) {
    	//setOpaque(false);
        this.onFinish = onFinish;

        logo = new ImageIcon(
        	    getClass().getResource("/res/logos/theIMPERIALOne(TEXTO)_logo (pequeño).png")
        	).getImage();
        if (logo == null) return;
        setFocusable(true);
    }

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