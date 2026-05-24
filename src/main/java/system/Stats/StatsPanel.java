package system.Stats;

import javax.swing.*;

import java.awt.*;
import java.util.List;

public class StatsPanel extends JPanel {

    private JPanel contentPanel;
    private JLabel dineroLabel;
    private JLabel totalLabel;

    public StatsPanel() {

        setLayout(new BorderLayout());

        setBackground(Color.WHITE);

        // ============================
        // CONTENIDO
        // ============================

        contentPanel = new JPanel();

        contentPanel.setLayout(
                new BoxLayout(
                        contentPanel,
                        BoxLayout.Y_AXIS
                )
        );

        contentPanel.setBackground(Color.WHITE);

        JScrollPane scroll =
                new JScrollPane(contentPanel);

        add(scroll, BorderLayout.CENTER);

        // ============================
        // TOTAL
        // ============================

        JPanel sur = new JPanel();
        sur.setLayout(new BoxLayout(sur, BoxLayout.Y_AXIS));
        sur.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        sur.setBackground(Color.WHITE);

        dineroLabel = new JLabel("Dinero disponible: ₲0");
        dineroLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        dineroLabel.setForeground(new Color(52, 120, 246));

        totalLabel = new JLabel("Valor en cartera: ₲0");
        totalLabel.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));

        sur.add(dineroLabel);
        sur.add(totalLabel);
        add(sur, BorderLayout.SOUTH);
    }

    // =====================================================
    // ACTUALIZAR
    // =====================================================

    public void updatePortfolio(List<PortfolioEntry> portfolio,
            double total,
            double dinero) {
		contentPanel.removeAll();
		for (PortfolioEntry p : portfolio) {
		contentPanel.add(createEntry(p));
		contentPanel.add(Box.createVerticalStrut(10));
		}
		totalLabel.setText(String.format("Valor en cartera: ₲%.2f", total));
		dineroLabel.setText(String.format("Dinero disponible: ₲%.2f", dinero));
		revalidate();
		//repaint();
	}

    // =====================================================
    // ENTRY VISUAL
    // =====================================================

    private JPanel createEntry(PortfolioEntry p){

        JPanel panel =
                new JPanel(
                        new GridLayout(3,1)
                );

        panel.setBorder(
                BorderFactory.createLineBorder(
                        Color.BLACK
                )
        );

        panel.setBackground(Color.WHITE);

        panel.add(new JLabel(
                "Empresa: " + p.getEmpresaNombre()
        ));

        panel.add(new JLabel(
                "Acciones: " + p.getAcciones()
        ));

        panel.add(new JLabel(
                "Valor: ₲" + p.getValorTotal()
        ));

        return panel;
    }
}