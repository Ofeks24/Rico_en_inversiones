package system.Investment;

import tools.Candle;
import tools.MarketService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CompanyChartPanel extends JPanel {

    private final MarketService market;
    private int empresaId;

    // Colores
    private static final Color BG        = new Color(15, 15, 15);
    private static final Color GRID      = new Color(255, 255, 255, 18);
    private static final Color BULL      = new Color(0, 220, 120);
    private static final Color BEAR      = new Color(220, 70, 70);
    private static final Color STAT_BG   = new Color(25, 25, 25);
    private static final Color STAT_TEXT = new Color(200, 200, 200);

    public CompanyChartPanel(MarketService market, int empresaId) {
        this.market    = market;
        this.empresaId = empresaId;
        setBackground(BG);
        setDoubleBuffered(true);
        market.addListener(() ->
            SwingUtilities.invokeLater(this::repaint));
    }

    public void setEmpresaId(int id) {
        this.empresaId = id;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        List<Candle> candles = market.getHistory(empresaId);
        if (candles.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        int w         = getWidth();
        int statsH    = 36;          // altura reservada para barra inferior
        int chartH    = getHeight() - statsH;

        // ── Rango de precios ─────────────────────────────────
        double min = candles.stream()
                            .mapToDouble(Candle::getLow).min().orElse(0);
        double max = candles.stream()
                            .mapToDouble(Candle::getHigh).max().orElse(1);
        double pad = (max - min) * 0.12;
        min -= pad; max += pad;
        double scaleY = chartH / (max - min);

        // ── Fondo + grid ─────────────────────────────────────
        g2.setColor(BG);
        g2.fillRect(0, 0, w, chartH);
        g2.setColor(GRID);
        for (int y = 40; y < chartH; y += 40)
            g2.drawLine(0, y, w, y);

        // ── Velas ─────────────────────────────────────────────
        double scaleX = (double) w / candles.size();

        for (int i = 0; i < candles.size(); i++) {
            Candle c  = candles.get(i);
            int    cx = (int)(i * scaleX + scaleX / 2);

            int openY  = (int)(chartH - (c.getOpen()  - min) * scaleY);
            int closeY = (int)(chartH - (c.getClose() - min) * scaleY);
            int highY  = (int)(chartH - (c.getHigh()  - min) * scaleY);
            int lowY   = (int)(chartH - (c.getLow()   - min) * scaleY);

            boolean bull = c.getClose() >= c.getOpen();
            Color   col  = bull ? BULL : BEAR;

            g2.setColor(col);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawLine(cx, highY, cx, lowY);              // mecha

            int bw  = Math.max(2, (int)(scaleX * 0.55));
            int top = Math.min(openY, closeY);
            int bh  = Math.max(2, Math.abs(openY - closeY));
            g2.fillRoundRect(cx - bw / 2, top, bw, bh, 3, 3);
        }

        // ── Barra de estadísticas ─────────────────────────────
        g2.setColor(STAT_BG);
        g2.fillRect(0, chartH, w, statsH);

        Candle  last    = candles.get(candles.size() - 1);
        double  current = last.getClose();
        double  open0   = market.getOpenDay(empresaId);
        double  pct     = (current - open0) / open0 * 100;
        boolean up      = pct >= 0;

        double dayHigh = candles.stream()
                                .mapToDouble(Candle::getHigh).max().orElse(current);
        double dayLow  = candles.stream()
                                .mapToDouble(Candle::getLow).min().orElse(current);

        String sPrecio = String.format("₲%.2f",  current);
        String sPct    = String.format("%s%.2f%%", up ? "▲+" : "▼", pct);
        String sHigh   = String.format("▲₲%.2f",  dayHigh);
        String sLow    = String.format("▼₲%.2f",  dayLow);

        Font bold   = new Font("Segoe UI", Font.BOLD,  13);
        Font normal = new Font("Segoe UI", Font.PLAIN, 12);

        int baseline = chartH + 23;

        // Precio
        g2.setFont(bold);
        g2.setColor(Color.WHITE);
        g2.drawString(sPrecio, 10, baseline);

        // % cambio
        int precioW = g2.getFontMetrics().stringWidth(sPrecio);
        g2.setFont(bold);
        g2.setColor(up ? BULL : BEAR);
        g2.drawString(sPct, 16 + precioW, baseline);

        // Máximo / mínimo del día (derecha)
        g2.setFont(normal);
        g2.setColor(STAT_TEXT);
        int highW = g2.getFontMetrics().stringWidth(sHigh + "  " + sLow);
        g2.drawString(sHigh + "  " + sLow, w - highW - 10, baseline);

        g2.dispose();
    }
}