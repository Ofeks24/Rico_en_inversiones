package system;

import javax.swing.*;

import tools.Candle;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;


/**
 * Panel decorativo de fondo que dibuja un gráfico de velas japonesas
 * animado y generado de forma procedural.
 *
 * <p>Se usa como capa de fondo en {@link MainFrame} para dotar al menú
 * principal y a las pantallas del juego de un ambiente financiero visual.
 * Las velas se generan aleatoriamente con un desplazamiento de precio
 * continuo y se actualizan cada 500 ms mediante un {@link Timer}.</p>
 *
 * <p>El número máximo de velas visibles está limitado a 50; las más
 * antiguas se descartan automáticamente al añadir nuevas.</p>
 */
public class GraphBackgroundPanel extends JPanel {

    private final ArrayList<Candle> candles = new ArrayList<>();
    private final Random r = new Random();

    private double scaleX = 18;
    private double offsetX = 0;

    private Timer graphTimer;

    /**
     * Construye el panel, establece el color de fondo oscuro, genera las
     * velas iniciales y arranca el temporizador de animación.
     */
    public GraphBackgroundPanel() {
        setBackground(new Color(15, 15, 15));
        setDoubleBuffered(true);
        initCandles();
        initTimer();
    }

    /**
     * Genera un conjunto inicial de 120 velas con precios aleatorios a
     * partir de un precio de apertura de 400 unidades.
     *
     * <p>Cada vela se construye con un drift aleatorio de ±15 sobre el
     * cierre anterior y mechas proporcionales al tamaño del cuerpo.</p>
     */
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

    /**
     * Crea e inicia el {@link Timer} que añade una nueva vela al historial
     * cada 500 milisegundos y solicita el repintado del panel.
     */
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

    /**
     * Genera una nueva vela basada en el cierre de la última vela existente
     * y la añade al historial. Si el historial supera las 50 velas, elimina
     * la más antigua para mantener la ventana de visualización constante.
     */
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

    /**
     * Pinta el gráfico de velas sobre el componente.
     *
     * <p>El proceso de pintado sigue estos pasos:</p>
     * <ol>
     *   <li>Calcula el rango de precios (mín/máx) de todas las velas visibles
     *       y añade un 10% de margen en cada extremo.</li>
     *   <li>Dibuja el fondo oscuro y una cuadrícula horizontal sutil.</li>
     *   <li>Renderiza cada vela con mecha vertical y cuerpo redondeado;
     *       las velas alcistas se dibujan en verde ({@code #00FF8C}) y las
     *       bajistas en rojo ({@code #FF5050}).</li>
     * </ol>
     *
     * @param g Contexto gráfico proporcionado por el sistema Swing.
     */
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