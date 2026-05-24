package system;

import javax.swing.*;

import tools.Candle;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class GraphBackgroundPanel extends JPanel {

    private final ArrayList<Candle> candles = new ArrayList<>();
    private final Random r = new Random();

    private double scaleX = 18;
    private double offsetX = 0;

    private Timer graphTimer;

    public GraphBackgroundPanel() {
        setBackground(new Color(15, 15, 15));
        setDoubleBuffered(true);
        initCandles();
        initTimer();
    }

    private void initCandles() {
        double price = 400;

        for (int i = 0; i < 120; i++) {
            double open = price;
            double drift = (r.nextDouble() * 30 - 15);
            double close = open + drift;

            double bodySize = Math.abs(close - open);

            double wickFactor = 0.5; // ajusta esto (0.2 = muy corta, 1 = normal)

            double high = Math.max(open, close) + r.nextDouble() * bodySize * wickFactor;
            double low  = Math.min(open, close) - r.nextDouble() * bodySize * wickFactor;

            candles.add(new Candle(open, high, low, close));
            price = close;
        }
    }

    private void initTimer() {
        graphTimer = new Timer(500, e -> {
            updateGraph();
        });
        graphTimer.start();
    }

    /*private void initMouseControls() {
        addMouseWheelListener(e -> {
            double factor = (e.getPreciseWheelRotation() < 0) ? 1.1 : 0.9;

            if (e.isShiftDown()) {
                scaleY *= factor;
                scaleY = Math.max(0.5, Math.min(scaleY, 5));
            }

            
        });
    }*/

    private void updateGraph() {
        double lastClose = candles.get(candles.size() - 1).getClose();

        double open = lastClose;
        double drift = (r.nextDouble() * 30 - 15);
        double close = open + drift;

        double bodySize = Math.abs(close - open);

        double wickFactor = 0.5; // ajusta esto (0.2 = muy corta, 1 = normal)

        double high = Math.max(open, close) + r.nextDouble() * bodySize * wickFactor;
        double low  = Math.min(open, close) - r.nextDouble() * bodySize * wickFactor;

        candles.add(new Candle(open, high, low, close));

        if (candles.size() > 50) {
            candles.remove(0);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(2f)); // mechas más visibles

        int w = getWidth();
        int h = getHeight();

        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;

        for (Candle c : candles) {
            min = Math.min(min, c.getLow());
            max = Math.max(max, c.getHigh());
        }

        double range = max - min;
        double padding = range * 0.1;

        min -= padding;
        max += padding;

        double scaleYDynamic = h / (max - min);

        // Fondo
        g2.setColor(new Color(15, 15, 15));
        g2.fillRect(0, 0, w, h);

        // Grid
        g2.setColor(new Color(255, 255, 255, 15));
        for (int y = 100; y < h; y += 100) {
            g2.drawLine(0, y, w, y);
        }

        int startX = (int) offsetX;

        for (int i = 0; i < candles.size(); i++) {
            Candle c = candles.get(i);
            int x = startX + (int)(i * scaleX);

            if (x < -100 || x > w + 100) continue;

            int openY  = (int)(h - (c.getOpen()  - min) * scaleYDynamic);
            int closeY = (int)(h - (c.getClose() - min) * scaleYDynamic);
            int highY  = (int)(h - (c.getHigh()  - min) * scaleYDynamic);
            int lowY   = (int)(h - (c.getLow()   - min) * scaleYDynamic);

            boolean bullish = c.getClose() >= c.getOpen();

            g2.setColor(bullish
                    ? new Color(0, 255, 140)
                    : new Color(255, 80, 80));

            // mecha
            g2.drawLine(x, highY, x, lowY);

            // cuerpo
            int bodyTop = Math.min(openY, closeY);
            int bodyHeight = Math.max(2, Math.abs(openY - closeY));

            g2.fillRoundRect(x - 5, bodyTop, 10, bodyHeight, 4, 4);
        }

        g2.dispose();
    }
    
}