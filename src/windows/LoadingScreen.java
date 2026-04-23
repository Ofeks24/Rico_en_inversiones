package windows;

import java.awt.*;
import javax.swing.*;

public class LoadingScreen extends JWindow {

    private float alpha = 0f;
    private Image logo;
    private MainMenu mainMenu;

    public LoadingScreen(MainMenu mainMenu) {
        this.mainMenu = mainMenu;

        logo = new ImageIcon("res/logos/theIMPERIALOne(TEXTO)_logo (pequeño).png").getImage();

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screen.width, screen.height);
        setLocation(0, 0);

        setBackground(Color.BLACK);

        JPanel panel = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, getWidth(), getHeight());

                g2.setComposite(
                    AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER, alpha
                    )
                );

                int imgW = logo.getWidth(null);
                int imgH = logo.getHeight(null);

                int x = (getWidth() - imgW) / 2;
                int y = (getHeight() - imgH) / 2;

                g2.drawImage(logo, x, y, null);

                g2.dispose();
            }
        };

        panel.setOpaque(true);
        setContentPane(panel);
        setAlwaysOnTop(true);
        requestFocus();
    }

    public void startAnimation() {
        Timer timer = new Timer(30, null);

        timer.addActionListener(e -> {

            alpha += 0.02f;
            
            if (alpha >= 1f) {
                alpha = 1f;
                repaint();
                
                timer.stop();
                mainMenu.setVisible(true);

                Timer espera = new Timer(2000, ev -> {
                	setAlwaysOnTop(false);
                	mainMenu.setVisible(true);
                	mainMenu.toFront();
                	mainMenu.requestFocus();

                	dispose();

                	new Timer(150, ex -> {
                		mainMenu.iniciarMenuAnimado();
                	}).start();
                	timer.setRepeats(false);
                });

                espera.setRepeats(false);
                espera.start();
                
                return;
            }

            repaint();
        });

        timer.start();
        setVisible(true);
    }
}