
package system.Investment;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicSliderUI;

import tools.CompanyData;
import tools.MarketService;

public class InvestmentPanel extends JPanel {

    // ── Colores ───────────────────────────────────────────
    private static final Color BG     = new Color(245, 247, 250);
    private static final Color CARD   = Color.WHITE;
    private static final Color TEXT   = new Color(30, 30, 30);
    private static final Color ACCENT = new Color(52, 120, 246);
    private static final Color BUY    = new Color(0, 160, 80);
    private static final Color SELL   = new Color(200, 50, 50);

    // ── Fuentes ───────────────────────────────────────────
    private static final java.awt.Font TITLE_FONT =
            new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 22);
    private static final java.awt.Font SUBTITLE_FONT =
            new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16);
    private static final java.awt.Font NORMAL_FONT =
            new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14);
    private static final java.awt.Font BUTTON_FONT =
            new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14);
    private static final java.awt.Font LABEL_SMALL =
            new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12);

    // ── Componentes dinámicos ─────────────────────────────
    private JComboBox<CompanyData> companyDropdown;
    private CompanyChartPanel chart;

    private JLabel nombreLabel;
    private JPanel descripcionPanel;
    private boolean descripcionExpanded = false;
    private JLabel accionesMercadoLabel;
    private JLabel accionesPropiedadLabel;
    private JLabel valorAccionLabel;

    // Slider COMPRAR
    private JSlider  buySlider;
    private JTextField buyField;
    private JLabel   buyTotalLabel;

    // Slider VENDER
    private JSlider  sellSlider;
    private JTextField sellField;
    private JLabel   sellTotalLabel;

    private JButton comprarButton;
    private JButton venderButton;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public InvestmentPanel(MarketService market, int empresaIdInicial) {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        setBackground(BG);

        add(crearBarraSuperior(),                   BorderLayout.NORTH);
        add(crearContenido(market, empresaIdInicial), BorderLayout.CENTER);
    }

    // =====================================================
    // BARRA SUPERIOR — dropdown
    // =====================================================

    private JPanel crearBarraSuperior() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(CARD);

        companyDropdown = new JComboBox<>();
        companyDropdown.setFont(NORMAL_FONT);
        companyDropdown.setPreferredSize(new Dimension(250, 35));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.setOpaque(false);
        left.add(companyDropdown);

        top.add(left, BorderLayout.WEST);
        return top;
    }

    // =====================================================
    // CONTENIDO PRINCIPAL
    // =====================================================

    private JPanel crearContenido(MarketService market, int empresaIdInicial) {

        // Layout proporcional manual:
        //   izquierda 72% | derecha 28%
        //   gráfica   65% | barras  35%
        JPanel root = new JPanel(null) {
            @Override
            public void doLayout() {
                if (getComponentCount() < 3) return;
                int w = getWidth(), h = getHeight();
                if (w == 0 || h == 0) return;

                int gap        = 8;
                int derechaW   = (int)(w * 0.28);
                int izquierdaW = w - derechaW - gap * 3;

                int graficaH = (int)(h * 0.62);
                int barrasH  = h - graficaH - gap * 3;

                // comp 0 → gráfica
                getComponent(0).setBounds(gap, gap, izquierdaW, graficaH);
                // comp 1 → panel de sliders
                getComponent(1).setBounds(gap, gap + graficaH + gap, izquierdaW, barrasH);
                // comp 2 → panel derecho
                getComponent(2).setBounds(gap + izquierdaW + gap, gap, derechaW, h - gap * 2);
            }
        };
        root.setBackground(BG);

        // ── Gráfica (índice 0) ────────────────────────────
        JPanel grafica = new JPanel(new BorderLayout());
        grafica.setBackground(CARD);
        grafica.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
        chart = new CompanyChartPanel(market, empresaIdInicial);
        grafica.add(chart, BorderLayout.CENTER);
        root.add(grafica);  // índice 0

        // ── Panel de sliders (índice 1) ───────────────────
        root.add(crearPanelSliders());  // índice 1

        // ── Panel derecho — info empresa (índice 2) ───────
        root.add(crearPanelDerecho());  // índice 2

        return root;
    }

    // =====================================================
    // PANEL DE SLIDERS (comprar + vender)
    // =====================================================

    private JPanel crearPanelSliders() {

        JPanel contenedor = new JPanel(new BorderLayout(8, 0));
        contenedor.setBackground(BG);

        // ── Fila COMPRAR ──────────────────────────────────
        JPanel filaBuy = crearFilaSlider(
            "COMPRAR", BUY,
            true  // es la fila de compra
        );

        // ── Fila VENDER ───────────────────────────────────
        JPanel filaSell = crearFilaSlider(
            "VENDER", SELL,
            false
        );

        contenedor.add(filaBuy,  BorderLayout.NORTH);
        contenedor.add(filaSell, BorderLayout.SOUTH);

        return contenedor;
    }

    /**
     * Crea una fila completa con:
     *   [etiqueta] [slider] [campo numérico] [label coste/ingreso]
     */
    private JPanel crearFilaSlider(String etiqueta, Color color, boolean esBuy) {

        JPanel fila = new JPanel(new BorderLayout(6, 0));
        fila.setBackground(CARD);
        fila.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        // Etiqueta lateral con color
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(LABEL_SMALL);
        lbl.setForeground(color);
        lbl.setPreferredSize(new Dimension(68, 20));

        // Slider con thumb de color
        JSlider slider = crearSlider(color);

        // Campo numérico
        JTextField field = new JTextField("0");
        field.setFont(SUBTITLE_FONT);
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setColumns(5);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));
        field.setMaximumSize(new Dimension(80, 28));
        field.setPreferredSize(new Dimension(80, 28));

        // Label de coste/ingreso
        JLabel totalLabel = new JLabel(esBuy ? "Coste: ₲0" : "Ingreso: ₲0");
        totalLabel.setFont(LABEL_SMALL);
        totalLabel.setForeground(color);
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        totalLabel.setPreferredSize(new Dimension(130, 20));

        // Guardar referencias
        if (esBuy) {
            buySlider     = slider;
            buyField      = field;
            buyTotalLabel = totalLabel;
        } else {
            sellSlider     = slider;
            sellField      = field;
            sellTotalLabel = totalLabel;
        }

        // Panel central: slider + campo
        JPanel centro = new JPanel(new BorderLayout(6, 0));
        centro.setOpaque(false);
        centro.add(slider, BorderLayout.CENTER);
        centro.add(field,  BorderLayout.EAST);

        fila.add(lbl,        BorderLayout.WEST);
        fila.add(centro,     BorderLayout.CENTER);
        fila.add(totalLabel, BorderLayout.EAST);

        return fila;
    }

    /** Slider con thumb personalizado del color dado */
    private JSlider crearSlider(Color color) {
        JSlider s = new JSlider(0, 100, 0);
        s.setBackground(CARD);
        s.setForeground(color);
        s.setPaintTicks(false);
        s.setPaintLabels(false);
        s.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        s.setUI(new BasicSliderUI(s) {
            @Override
            public void paintThumb(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Rectangle r = thumbRect;
                g2.setColor(color);
                g2.fillRoundRect(
                    r.x + r.width / 2 - 4,
                    r.y + 2,
                    8,
                    r.height - 4,
                    4, 4
                );
                g2.dispose();
            }
        });
        return s;
    }

    // =====================================================
    // PANEL DERECHO — info + botones
    // =====================================================

    private JPanel crearPanelDerecho() {

        JPanel derecha = new JPanel();
        derecha.setLayout(new BoxLayout(derecha, BoxLayout.Y_AXIS));
        derecha.setBackground(CARD);
        derecha.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        nombreLabel            = crearLabel("");
        accionesMercadoLabel   = crearLabel("");
        accionesPropiedadLabel = crearLabel("");
        valorAccionLabel       = crearLabel("");

        nombreLabel.setFont(TITLE_FONT);
        nombreLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        derecha.add(nombreLabel);
        derecha.add(Box.createVerticalStrut(8));

        derecha.add(crearDescripcionExpandible(""));
        derecha.add(Box.createVerticalStrut(12));

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        derecha.add(sep);
        derecha.add(Box.createVerticalStrut(8));

        derecha.add(accionesMercadoLabel);
        derecha.add(valorAccionLabel);
        derecha.add(accionesPropiedadLabel);

        derecha.add(Box.createVerticalGlue());

        comprarButton = crearBoton("Comprar", BUY);
        comprarButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        comprarButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        venderButton = crearBoton("Vender", SELL);
        venderButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        venderButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        derecha.add(comprarButton);
        derecha.add(Box.createVerticalStrut(6));
        derecha.add(venderButton);

        return derecha;
    }

    // =====================================================
    // HELPERS
    // =====================================================

    private JLabel crearLabel(String txt) {
        JLabel l = new JLabel(txt);
        l.setForeground(TEXT);
        l.setFont(NORMAL_FONT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return l;
    }

    private JButton crearBoton(String txt, Color color) {
        JButton b = new JButton(txt);
        b.setFont(BUTTON_FONT);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setFocusPainted(false);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        b.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        return b;
    }

    private JPanel crearDescripcionExpandible(String texto) {
        descripcionPanel = new JPanel();
        descripcionPanel.setLayout(new BorderLayout());
        descripcionPanel.setBackground(CARD);
        descripcionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        descripcionPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setBackground(new Color(235, 240, 255));
        cabecera.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        cabecera.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        cabecera.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        JLabel flecha = new JLabel((descripcionExpanded ? "-" : "+") + "  Descripción");
        flecha.setFont(NORMAL_FONT);
        flecha.setForeground(ACCENT);
        cabecera.add(flecha, BorderLayout.WEST);

        JTextArea area = new JTextArea(texto);
        area.setFont(NORMAL_FONT);
        area.setForeground(TEXT);
        area.setBackground(new Color(248, 250, 255));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(null);
        scroll.setPreferredSize(new Dimension(0, 80));
        scroll.setVisible(false);

        cabecera.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                descripcionExpanded = !descripcionExpanded;
                scroll.setVisible(descripcionExpanded);
                flecha.setText((descripcionExpanded ? "-" : "+") + "  Descripción");
                descripcionPanel.revalidate();
                descripcionPanel.repaint();
                descripcionPanel.getParent().revalidate();
                descripcionPanel.getParent().repaint();
            }
        });

        descripcionPanel.add(cabecera, BorderLayout.NORTH);
        descripcionPanel.add(scroll,   BorderLayout.CENTER);
        return descripcionPanel;
    }

    // =====================================================
    // API PÚBLICA (usada por el Controller)
    // =====================================================

    public void setDescripcion(String texto) {
        if (descripcionPanel == null) return;
        BorderLayout bl = (BorderLayout) descripcionPanel.getLayout();
        Component center = bl.getLayoutComponent(BorderLayout.CENTER);
        if (center instanceof JScrollPane sp
                && sp.getViewport().getView() instanceof JTextArea area) {
            area.setText(texto);
        }
    }

    public void setCompanies(java.util.List<CompanyData> companies) {
        companyDropdown.removeAllItems();
        for (CompanyData c : companies) companyDropdown.addItem(c);
    }

    public void setCompanyInfo(CompanyData c) {
        if (c == null) return;
        nombreLabel.setFont(TITLE_FONT);
        nombreLabel.setText(c.getNombre());
        setDescripcion(c.getActividad() != null ? c.getActividad() : "");
        updateMarketInfo(c);
        accionesPropiedadLabel.setText("En propiedad: " + c.getAccionesPropiedad());
    }

    public void updateMarketInfo(CompanyData c) {
        if (c == null) return;
        valorAccionLabel.setText(
            String.format("Valor por acción: ₲%.2f", c.getValorAccion()));
        int disponibles = c.getAccionesMercado() - c.getAccionesPropiedad();
        accionesMercadoLabel.setText("Disponibles: " + disponibles);
    }

    public void refreshOwnership(CompanyData c) {
        if (c == null) return;
        accionesPropiedadLabel.setText("En propiedad: " + c.getAccionesPropiedad());
        int disponibles = c.getAccionesMercado() - c.getAccionesPropiedad();
        accionesMercadoLabel.setText("Disponibles: " + disponibles);
    }

    // ── Comprar ───────────────────────────────────────────
    public void setBuyMax(int max) {
        buySlider.setMaximum(Math.max(0, max));
    }

    public void setBuyValue(int v) {
        buySlider.setValue(v);
        buyField.setText(String.valueOf(v));
    }

    public void setBuyCost(double cost) {
        buyTotalLabel.setText(String.format("Coste: ₲%.2f", cost));
    }

    // ── Vender ───────────────────────────────────────────
    public void setSellMax(int max) {
        sellSlider.setMaximum(Math.max(0, max));
    }

    public void setSellValue(int v) {
        sellSlider.setValue(v);
        sellField.setText(String.valueOf(v));
    }

    public void setSellIncome(double income) {
        sellTotalLabel.setText(String.format("Ingreso: ₲%.2f", income));
    }

    // ── Getters de componentes ────────────────────────────
    public JComboBox<CompanyData> getCompanyDropdown() { return companyDropdown; }
    public JSlider  getBuySlider()    { return buySlider;  }
    public JSlider  getSellSlider()   { return sellSlider; }
    public JTextField getBuyField()   { return buyField;   }
    public JTextField getSellField()  { return sellField;  }
    public JButton  getComprarButton(){ return comprarButton; }
    public JButton  getVenderButton() { return venderButton;  }

    public void setChartEmpresa(int id) { chart.setEmpresaId(id); }

    // Compat — el controller viejo usaba getSlider() y getAccionesField()
    // Se mantienen apuntando al slider de compra para no romper nada
    /** @deprecated Usar getBuySlider() */
    public JSlider    getSlider()       { return buySlider; }
    /** @deprecated Usar getBuyField() */
    public JTextField getAccionesField(){ return buyField;  }
}