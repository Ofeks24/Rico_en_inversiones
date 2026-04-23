package windows;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

import tools.BotonAjustable;

public class MainMenu extends JFrame {
	

    private GameWindow gameWindow;


    private JLabel logo;

    private final BotonAjustable[] botones = {
            new BotonAjustable(new JButton(), 500),
            new BotonAjustable(new JButton(), 1000),
            new BotonAjustable(new JButton(), 1500)
    };


    private Timer animationTimer;
    private Timer graphTimer;

    private double speed = 1200;
    private long lastTime;
    private long startTime;

    private volatile boolean running = true;


    private GraphBackgroundPanel graphPanel;


    private final ImageIcon logoIcon = new ImageIcon("res/logos/Rico en inversiones_logo.png");

    private final ImageIcon empezarN = new ImageIcon("res/sprites/Empezar(Normal).png");
    private final ImageIcon empezarH = new ImageIcon("res/sprites/Empezar(Hover).png");
    private final ImageIcon empezarC = new ImageIcon("res/sprites/Empezar(Click).png");

    private final ImageIcon opcionesN = new ImageIcon("res/sprites/Opciones(Normal).png");
    private final ImageIcon opcionesH = new ImageIcon("res/sprites/Opciones(Hover).png");
    private final ImageIcon opcionesC = new ImageIcon("res/sprites/Opciones(Click).png");

    private final ImageIcon salirN = new ImageIcon("res/sprites/Salir(Normal).png");
    private final ImageIcon salirH = new ImageIcon("res/sprites/Salir(Hover).png");
    private final ImageIcon salirC = new ImageIcon("res/sprites/Salir(Click).png");


    public MainMenu(GameWindow gameWindow) {

        this.gameWindow = gameWindow;

        setTitle("Menu");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        graphPanel = new GraphBackgroundPanel();
        graphPanel.setLayout(null);
        setContentPane(graphPanel);

        initUI();
        initTimers();

        setVisible(true);
    }


    private void initUI() {

        logo = new JLabel(logoIcon);
        logo.setBounds(700, -1000,
                logoIcon.getIconWidth(),
                logoIcon.getIconHeight());

        graphPanel.add(logo);

        botones[0].setBoton(crearBoton("Empezar", -300, 300));
        botones[1].setBoton(crearBoton("Opciones", -300, 400));
        botones[2].setBoton(crearBoton("Salir", -300, 500));

        graphPanel.add(botones[0].getBoton());
        graphPanel.add(botones[1].getBoton());
        graphPanel.add(botones[2].getBoton());
    }


    private JButton crearBoton(String texto, int x, int y) {

        JButton b = new JButton(texto);
        b.setBounds(x, y, 454, 75);

        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);

        switch (texto) {

            case "Empezar":
                b.setIcon(empezarN);
                b.setRolloverIcon(empezarH);
                b.setPressedIcon(empezarC);

                b.addActionListener(e -> {
                    Timer t = new Timer(250, ev -> {

                        setAlwaysOnTop(false);
                        gameWindow.setVisible(true);
                        gameWindow.toFront();
                        gameWindow.requestFocus();

                        Timer hide = new Timer(10, ex -> setVisible(false));
                        hide.setRepeats(false);
                        hide.start();
                    });
                    t.setRepeats(false);
                    t.start();
                });
            break;

            case "Opciones":
                b.setIcon(opcionesN);
                b.setRolloverIcon(opcionesH);
                b.setPressedIcon(opcionesC);
            break;

            case "Salir":
                b.setIcon(salirN);
                b.setRolloverIcon(salirH);
                b.setPressedIcon(salirC);

                b.addActionListener(e -> {
                    running = false;
                    System.exit(0);
                });
            break;
        }

        return b;
    }


    private void initTimers() {

        animationTimer = new Timer(16, e -> updateAnimation());
        animationTimer.start();

        graphTimer = new Timer(500, e -> {
            graphPanel.updateGraph();
            graphPanel.repaint();
        });
        graphTimer.start();
    }


    private void updateAnimation() {

        if (lastTime == 0) lastTime = System.nanoTime();

        long now = System.nanoTime();
        double delta = (now - lastTime) / 1_000_000_000.0;
        lastTime = now;

        double buttonSpeed = speed * delta;
        double logoSpeed = 2000 * delta;

        long elapsed = System.currentTimeMillis();

        if (logo.getY() < 80) {
            logo.setLocation(logo.getX(), logo.getY() + (int) logoSpeed);
        }

        if (elapsed > delayB1()) mover(botones[0].getBoton(), 100, buttonSpeed);
        if (elapsed > delayB2()) mover(botones[1].getBoton(), 100, buttonSpeed);
        if (elapsed > delayB3()) mover(botones[2].getBoton(), 100, buttonSpeed);

        repaint();

        if (logo.getY() >= 80 &&
                botones[0].getBoton().getX() >= 100 &&
                botones[1].getBoton().getX() >= 100 &&
                botones[2].getBoton().getX() >= 100) {

            animationTimer.stop();
        }
    }

    private long delayB1() {
        if (startTime == 0) startTime = System.currentTimeMillis();
        return startTime + botones[0].getDelay();
    }

    private long delayB2() {
        return startTime + botones[1].getDelay();
    }

    private long delayB3() {
        return startTime + botones[2].getDelay();
    }

    private void mover(JButton b, int targetX, double dx) {

        if (b.getX() < targetX) {

            int newX = (int) (b.getX() + dx);
            if (newX > targetX) newX = targetX;

            int eased = b.getX() + (int) ((newX - b.getX()) * 0.8);

            b.setLocation(eased, b.getY());
        }
    }

    public void iniciarMenuAnimado() {
        animationTimer.start();
    }
    
    
    class Candle {
        double open;
        double high;
        double low;
        double close;

        Candle(double open, double high, double low, double close) {
            this.open = open;
            this.high = high;
            this.low = low;
            this.close = close;
        }
    }


    class GraphBackgroundPanel extends JPanel {

        private ArrayList<Candle> candles = new ArrayList<>();
        private final Random r = new Random();


        private double scaleX = 18;
        private double scaleY = 2.2;
        private double offsetX = 0;

        private int lastMouseX;
        private boolean dragging = false;

        public GraphBackgroundPanel() {

            setBackground(new Color(15, 15, 15));

            double price = 400;

            for (int i = 0; i < 120; i++) {

                double open = price;

                double drift = (r.nextDouble() * 30 - 15);
                double close = open + drift;

                double high = Math.max(open, close) + r.nextDouble() * 20;
                double low = Math.min(open, close) - r.nextDouble() * 20;

                candles.add(new Candle(open, high, low, close));

                price = close;
            }

            initMouseControls();
        }


        public void updateGraph() {

            double lastClose = candles.get(candles.size() - 1).close;

            double open = lastClose;

            double drift = (r.nextDouble() * 30 - 15);
            double close = open + drift;

            double high = Math.max(open, close) + r.nextDouble() * 15;
            double low = Math.min(open, close) - r.nextDouble() * 15;

            candles.add(new Candle(open, high, low, close));

            if (candles.size() > 120) {
                candles.remove(0);
            }
        }


        private void initMouseControls() {

            addMouseWheelListener(e -> {

                double factor = (e.getPreciseWheelRotation() < 0) ? 1.1 : 0.9;

                if (e.isShiftDown()) {
                    scaleY *= factor;
                    scaleY = Math.max(0.5, Math.min(scaleY, 5));
                } 

                repaint();
            });

            /*addMouseListener(new java.awt.event.MouseAdapter() {

                @Override
                public void mousePressed(java.awt.event.MouseEvent e) {
                    dragging = true;
                    lastMouseX = e.getX();
                }

                @Override
                public void mouseReleased(java.awt.event.MouseEvent e) {
                    dragging = false;
                
            });

            addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {

                @Override
                public void mouseDragged(java.awt.event.MouseEvent e) {

                    if (!dragging) return;

                    int dx = e.getX() - lastMouseX;
                    lastMouseX = e.getX();

                    offsetX += dx;

                    repaint();
                }
            });}*/
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
            double centerY = h / 2.0;


            g2.setColor(new Color(15, 15, 15));
            g2.fillRect(0, 0, w, h);


            g2.setColor(new Color(255, 255, 255, 15));
            g2.setStroke(new BasicStroke(1f));

            for (int y = 100; y < h; y += 100)
                g2.drawLine(0, y, w, y);


            int startX = (int) offsetX;

            for (int i = 0; i < candles.size(); i++) {

                Candle c = candles.get(i);

                int x = startX + (int)(i * scaleX);

                if (x < -100 || x > w + 100) continue;

                int openY  = (int)(centerY - (c.open  - 400) * scaleY);
                int closeY = (int)(centerY - (c.close - 400) * scaleY);
                int highY  = (int)(centerY - (c.high  - 400) * scaleY);
                int lowY   = (int)(centerY - (c.low   - 400) * scaleY);

                boolean bullish = c.close >= c.open;

                g2.setColor(bullish
                        ? new Color(0, 255, 140)
                        : new Color(255, 80, 80));


                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(x, highY, x, lowY);


                int bodyTop = Math.min(openY, closeY);
                int bodyHeight = Math.abs(openY - closeY);

                if (bodyHeight < 2) bodyHeight = 2;

                g2.fillRoundRect(
                        x - 5,
                        bodyTop,
                        10,
                        bodyHeight,
                        4,
                        4
                );
            }

            g2.dispose();
        }
    }
}