package windows;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.*;

import tools.BotonAjustable;

public class MainMenu extends JFrame {

    private GameWindow gameWindow;

    JLabel logo;

    BotonAjustable[] botones = {
            new BotonAjustable(new JButton(), 500),
            new BotonAjustable(new JButton(), 1000),
            new BotonAjustable(new JButton(), 1500)
    };

    Timer timer;
    double speed = 1200;
    long lastTime;

    // ==========================
    // HILO SECUNDARIO GRÁFICA
    // ==========================
    private GraphBackgroundPanel graphPanel;
    private Thread graphThread;
    private volatile boolean running = true;

    public MainMenu(GameWindow gameWindow) {

        this.gameWindow = gameWindow;

        setTitle("Menu");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        graphPanel = new GraphBackgroundPanel();
        graphPanel.setLayout(null);
        setContentPane(graphPanel);

        // LOGO
        logo = new JLabel(new ImageIcon("res/logos/Rico en inversiones_logo.png"));
        logo.setBounds(
                700,
                -1000,
                new ImageIcon("res/logos/Rico en inversiones_logo.png").getIconWidth(),
                new ImageIcon("res/logos/Rico en inversiones_logo.png").getIconHeight()
        );

        graphPanel.add(logo);

        // BOTONES
        botones[0].setBoton(crearBoton("Empezar", -300, 300));
        botones[1].setBoton(crearBoton("Opciones", -300, 400));
        botones[2].setBoton(crearBoton("Salir", -300, 500));

        graphPanel.add(botones[0].getBoton());
        graphPanel.add(botones[1].getBoton());
        graphPanel.add(botones[2].getBoton());

        iniciarGraficaThread();

        setVisible(true);
    }

    // =====================================
    // SEGUNDO HILO PARA ACTUALIZAR GRÁFICA
    // =====================================
    private void iniciarGraficaThread() {

        graphThread = new Thread(() -> {

            while (running) {

                graphPanel.updateGraph();

                SwingUtilities.invokeLater(() -> graphPanel.repaint());

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    break;
                }
            }

        });

        graphThread.setDaemon(true);
        graphThread.start();
    }

    private JButton crearBoton(String texto, int x, int y) {

        JButton b = new JButton(texto);
        b.setBounds(x, y, 454, 75);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);

        if (texto.equals("Empezar")) {

            b.setIcon(new ImageIcon("res/sprites/Empezar(Normal).png"));
            b.setRolloverIcon(new ImageIcon("res/sprites/Empezar(Hover).png"));
            b.setPressedIcon(new ImageIcon("res/sprites/Empezar(Click).png"));

            b.addActionListener(e -> {

                new Timer(250, el -> {

                    setAlwaysOnTop(false);
                    gameWindow.setVisible(true);
                    gameWindow.toFront();
                    gameWindow.requestFocus();

                    new Timer(10, ex -> setVisible(false)).start();

                }).start();
            });
        }

        if (texto.equals("Opciones")) {

            b.setIcon(new ImageIcon("res/sprites/Opciones(Normal).png"));
            b.setRolloverIcon(new ImageIcon("res/sprites/Opciones(Hover).png"));
            b.setPressedIcon(new ImageIcon("res/sprites/Opciones(Click).png"));
        }

        if (texto.equals("Salir")) {

            b.setIcon(new ImageIcon("res/sprites/Salir(Normal).png"));
            b.setRolloverIcon(new ImageIcon("res/sprites/Salir(Hover).png"));
            b.setPressedIcon(new ImageIcon("res/sprites/Salir(Click).png"));

            b.addActionListener(e -> {
                running = false;
                System.exit(0);
            });
        }

        return b;
    }

    // ==========================
    // ANIMACIÓN MENÚ
    // ==========================
    private void iniciarAnimacion() {

        timer = new Timer(16, e -> {

            if (lastTime == 0) lastTime = System.nanoTime();

            long now = System.nanoTime();
            double delta = (now - lastTime) / 1_000_000_000.0;
            lastTime = now;

            double buttonSpeed = speed * delta;
            double logoSpeed = 2000 * delta;

            long elapsed = System.currentTimeMillis();

            if (logo.getY() < 80) {
                logo.setLocation(
                        logo.getX(),
                        logo.getY() + (int) logoSpeed
                );
            }

            if (elapsed > delayB1Start()) moverBoton(botones[0].getBoton(), 100, buttonSpeed);
            if (elapsed > delayB2Start()) moverBoton(botones[1].getBoton(), 100, buttonSpeed);
            if (elapsed > delayB3Start()) moverBoton(botones[2].getBoton(), 100, buttonSpeed);

            repaint();

            if (logo.getY() >= 80 &&
                    botones[0].getBoton().getX() >= 100 &&
                    botones[1].getBoton().getX() >= 100 &&
                    botones[2].getBoton().getX() >= 100) {

                timer.stop();
            }

        });

        timer.start();
    }

    private long startTime;

    private long delayB1Start() {
        if (startTime == 0) startTime = System.currentTimeMillis();
        return startTime + botones[0].getDelay();
    }

    private long delayB2Start() {
        return startTime + botones[1].getDelay();
    }

    private long delayB3Start() {
        return startTime + botones[2].getDelay();
    }

    private void moverBoton(JButton b, int destinoX, double dx) {

        if (b.getX() < destinoX) {

            int newX = (int) (b.getX() + dx);

            if (newX > destinoX) newX = destinoX;

            int eased = b.getX() + (int) ((newX - b.getX()) * 0.8);

            b.setLocation(eased, b.getY());
        }
    }

    public void iniciarMenuAnimado() {
        iniciarAnimacion();
    }

    // =====================================================
    // PANEL FONDO CON GRÁFICA DIFUMINADA
    // =====================================================
    class GraphBackgroundPanel extends JPanel {

        private ArrayList<Double> values = new ArrayList<>();
        private Random r = new Random();

        public GraphBackgroundPanel() {

            setBackground(new Color(15, 15, 15));

            for (int i = 0; i < 120; i++) {
                values.add(400.0);
            }
        }

        public void updateGraph() {

            double last = values.get(values.size() - 1);

            double next = last + (r.nextDouble() * 40 - 20);

            if (next < 100) next = 100;
            if (next > getHeight() - 100) next = getHeight() - 100;

            values.add(next);

            if (values.size() > 160) {
                values.remove(0);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int w = getWidth();
            int h = getHeight();

            g2.setColor(new Color(20,20,20));
            g2.fillRect(0,0,w+100,h+100);

            double step = (double) w / (values.size() - 1);

            // líneas difuminadas múltiples pasadas
            for (int blur = 12; blur >= 1; blur--) {

                for (int i = 1; i < values.size(); i++) {

                    int x1 = (int)((i - 1) * step);
                    int y1 = values.get(i - 1).intValue();

                    int x2 = (int)(i * step);
                    int y2 = values.get(i).intValue();

                    boolean subida = y2 < y1;

                    Color c;

                    if (subida)
                        c = new Color(0, 255, 100, 10);
                    else
                        c = new Color(255, 60, 60, 10);

                    g2.setColor(c);
                    g2.setStroke(new BasicStroke(blur));
                    g2.drawLine(x1, y1, x2, y2);
                }
            }

            // línea principal
            for (int i = 1; i < values.size(); i++) {

                int x1 = (int)((i - 1) * step);
                int y1 = values.get(i - 1).intValue();

                int x2 = (int)(i * step);
                int y2 = values.get(i).intValue();

                boolean subida = y2 < y1;

                if (subida)
                    g2.setColor(new Color(0,255,120,90));
                else
                    g2.setColor(new Color(255,80,80,90));

                g2.setStroke(new BasicStroke(3f));
                g2.drawLine(x1, y1, x2, y2);
            }

            g2.dispose();
        }
    }
}

