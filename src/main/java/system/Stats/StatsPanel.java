package system.Stats;

import javax.swing.*;

import java.awt.*;
import java.util.List;

public class StatsPanel extends JPanel {

    private JPanel contentPanel;

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

        totalLabel = new JLabel(
                "Valor total: $0"
        );

        totalLabel.setBorder(
                BorderFactory.createEmptyBorder(
                        10,
                        10,
                        10,
                        10
                )
        );

        add(totalLabel, BorderLayout.SOUTH);
    }

    // =====================================================
    // ACTUALIZAR
    // =====================================================

    public void updatePortfolio(

            List<PortfolioEntry> portfolio,
            double total
    ) {

        contentPanel.removeAll();

        for (PortfolioEntry p : portfolio) {

            contentPanel.add(
                    createEntry(p)
            );

            contentPanel.add(
                    Box.createVerticalStrut(10)
            );
        }

        totalLabel.setText(
                "Valor total: $" + total
        );

        revalidate();
        repaint();
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