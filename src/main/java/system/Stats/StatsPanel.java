package system.Stats;

import javax.swing.*;
import java.awt.*;
import java.util.List;


/**
 * Panel de vista (la "V" del subsistema de estadísticas) que muestra al jugador
 * el estado actual de su cartera de inversiones.
 * <p>
 * Contiene una lista scrollable de tarjetas, una por cada posición abierta,
 * con el nombre de la empresa, número de acciones, valor actual y
 * variación porcentual respecto al precio de compra. En la parte inferior
 * se muestra el dinero disponible y el valor total de la cartera.
 * </p>
 * <p>
 * No contiene lógica de negocio; únicamente recibe datos de
 * {@link StatsController} a través de {@link #updatePortfolio}.
 * </p>
 */
public class StatsPanel extends JPanel {

    private JPanel contentPanel;
    private JLabel dineroLabel;
    private JLabel totalLabel;

    /**
     * Construye el panel inicializando la estructura visual:
     * scroll con tarjetas de posiciones y barra inferior de totales.
     */
    public StatsPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));

        // ── Contenido scrollable ──────────────────────────
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(245, 247, 250));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scroll = new JScrollPane(contentPanel);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        add(scroll, BorderLayout.CENTER);

        // ── Panel inferior con totales ────────────────────
        JPanel sur = new JPanel();
        sur.setLayout(new BoxLayout(sur, BoxLayout.Y_AXIS));
        sur.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        sur.setBackground(Color.WHITE);

        dineroLabel = new JLabel("Dinero disponible: ₲0,00");
        dineroLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        dineroLabel.setForeground(new Color(52, 120, 246));

        totalLabel = new JLabel("Valor en cartera: ₲0,00");
        totalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        totalLabel.setForeground(new Color(60, 60, 60));
        totalLabel.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));

        sur.add(dineroLabel);
        sur.add(totalLabel);
        add(sur, BorderLayout.SOUTH);
    }

    /**
     * Actualiza completamente la vista con los datos más recientes de la cartera.
     * <p>
     * Limpia el panel de contenido y lo repuebla con una tarjeta por cada
     * posición de {@code portfolio}. Si la cartera está vacía, muestra un
     * mensaje informativo. Actualiza también las etiquetas de totales.
     * </p>
     * <p>
     * Debe llamarse siempre desde el Event Dispatch Thread (EDT).
     * </p>
     *
     * @param portfolio lista de posiciones abiertas del jugador
     * @param total     valor total de la cartera a precios de mercado (₲)
     * @param dinero    dinero líquido disponible del jugador (₲)
     */
    public void updatePortfolio(List<PortfolioEntry> portfolio,
                                double total,
                                double dinero) {
        contentPanel.removeAll();

        if (portfolio.isEmpty()) {
            JLabel vacio = new JLabel("No tienes acciones en cartera.");
            vacio.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            vacio.setForeground(new Color(150, 150, 150));
            vacio.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.add(Box.createVerticalStrut(16));
            contentPanel.add(vacio);
        } else {
            for (PortfolioEntry p : portfolio) {
                contentPanel.add(createEntry(p));
                contentPanel.add(Box.createVerticalStrut(8));
            }
        }

        totalLabel.setText(String.format("Valor en cartera: ₲%.2f", total));
        dineroLabel.setText(String.format("Dinero disponible: ₲%.2f", dinero));

        revalidate();
        repaint();
    }

    /**
     * Crea la tarjeta visual para una posición de cartera individual.
     * <p>
     * La tarjeta muestra a la izquierda el nombre y número de acciones,
     * y a la derecha el valor actual y el porcentaje de ganancia o pérdida
     * con color verde (beneficio) o rojo (pérdida).
     * </p>
     *
     * @param p la entrada de cartera a representar
     * @return panel con el diseño de tarjeta listo para añadir al scroll
     */
    private JPanel createEntry(PortfolioEntry p) {

        JPanel card = new JPanel(new BorderLayout(8, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ── Columna izquierda: nombre + acciones ─────────
        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel nombre = new JLabel(p.getEmpresaNombre());
        nombre.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nombre.setForeground(new Color(30, 30, 30));

        JLabel acciones = new JLabel(p.getAcciones() + " acciones");
        acciones.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        acciones.setForeground(new Color(100, 100, 100));

        left.add(nombre);
        left.add(Box.createVerticalStrut(3));
        left.add(acciones);

        // ── Columna derecha: valor actual + variación ────
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setOpaque(false);

        double valorActual = p.getValorTotal();
        double pct         = p.getGananciaPct();
        boolean positivo   = pct >= 0;

        JLabel valorLabel = new JLabel(String.format("₲%.2f", valorActual));
        valorLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        valorLabel.setForeground(new Color(30, 30, 30));
        valorLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        String signo = positivo ? "▲ +" : "▼ ";
        JLabel pctLabel = new JLabel(String.format("%s%.2f%%", signo, pct));
        pctLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        pctLabel.setForeground(positivo
            ? new Color(0, 160, 80)
            : new Color(200, 50, 50));
        pctLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        right.add(valorLabel);
        right.add(Box.createVerticalStrut(3));
        right.add(pctLabel);

        card.add(left,  BorderLayout.WEST);
        card.add(right, BorderLayout.EAST);

        return card;
    }
}