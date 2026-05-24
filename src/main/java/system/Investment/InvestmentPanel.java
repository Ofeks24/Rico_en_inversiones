package system.Investment;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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

import system.Stats.StatsController;
import tools.CompanyData;
import tools.MarketService;



public class InvestmentPanel extends JPanel {
	
	private static final Color BG = new Color(245, 247, 250);
	private static final Color CARD = Color.WHITE;
	private static final Color TEXT = new Color(30, 30, 30);
	private static final Color ACCENT = new Color(52, 120, 246);

	private static final java.awt.Font TITLE_FONT =
	        new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 22);

	private static final java.awt.Font SUBTITLE_FONT =
	        new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16);

	private static final java.awt.Font NORMAL_FONT =
	        new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14);

	private static final java.awt.Font BUTTON_FONT =
	        new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14);



    // =====================================================
    // COMPONENTES DINÁMICOS
    // =====================================================

    private JComboBox<CompanyData> companyDropdown;
    private CompanyChartPanel chart;

    private JLabel nombreLabel;
    private JPanel descripcionPanel;   // el bloque expandible completo
    private boolean descripcionExpanded = false;
    private JLabel accionesMercadoLabel;
    private JLabel accionesPropiedadLabel;
    private JLabel valorAccionLabel;
    
    private JSlider accionesSlider;

    JTextField accionesField;
    private JLabel costeTotalLabel;
    
    private JButton comprarButton;
    private JButton venderButton;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public InvestmentPanel(MarketService market, int empresaIdInicial) {



        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        setBackground(BG);

        add(crearBarraSuperior(), BorderLayout.NORTH);
        add(crearContenido(market, empresaIdInicial), BorderLayout.CENTER);

        // empresa inicial

    }



    // =====================================================
    // BARRA SUPERIOR
    // =====================================================

    private JPanel crearBarraSuperior() {

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);

        // =========================
        // DROPDOWN
        // =========================


        companyDropdown = new JComboBox<>();
        companyDropdown.setFont(NORMAL_FONT);

        companyDropdown.setPreferredSize(
            new Dimension(250, 35)
        );
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.setOpaque(false);
        left.add(companyDropdown);

        // =========================
        // BOTÓN CERRAR
        // =========================

        top.add(left, BorderLayout.WEST);

        return top;
    }

    // =====================================================
    // CONTENIDO
    // =====================================================

    private JPanel crearContenido(MarketService market, int empresaIdInicial) {

        // =========================================================
        // Panel raíz con layout proporcional manual
        // Proporciones fijas:
        //   izquierda: 70% ancho | derecha: 30% ancho
        //   gráfica:   75% alto  | barra:   25% alto
        // =========================================================

        JPanel root = new JPanel(null) {
            @Override
            public void doLayout() {
                if (getComponentCount() < 3) return;
                int w = getWidth();
                int h = getHeight();
                if (w == 0 || h == 0) return;

                int gap        = 8;
                int derechaW   = (int)(w * 0.28);
                int izquierdaW = w - derechaW - gap * 3;

                int graficaH = (int)(h * 0.72);
                int barraH   = h - graficaH - gap * 3;

                getComponent(0).setBounds(
                    gap, gap,
                    izquierdaW, graficaH
                );
                getComponent(1).setBounds(
                    gap, gap + graficaH + gap,
                    izquierdaW, barraH
                );
                getComponent(2).setBounds(
                    gap + izquierdaW + gap, gap,
                    derechaW, h - gap * 2
                );
            }
        };

        root.setBackground(BG);

        // =================================================
        // GRÁFICA (componente 0)
        // =================================================

        JPanel grafica = new JPanel(new BorderLayout());
        grafica.setBackground(CARD);
        grafica.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)
            )
        );

        chart = new CompanyChartPanel(market, empresaIdInicial);
        grafica.add(chart, BorderLayout.CENTER);

        root.add(grafica);  // índice 0

        // =================================================
        // BARRA SLIDER (componente 1)
        // =================================================

        JPanel barra = new JPanel();
        barra.setLayout(new BoxLayout(barra, BoxLayout.Y_AXIS));
        barra.setBackground(CARD);
        barra.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
            )
        );

        accionesSlider = new JSlider();
        accionesSlider.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        accionesSlider.setUI(new BasicSliderUI(accionesSlider) {
            @Override
            public void paintThumb(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                Rectangle knobBounds = thumbRect;
                g2.setColor(ACCENT);
                g2.fillRect(
                    knobBounds.x + knobBounds.width / 2 - 2,
                    knobBounds.y,
                    8,
                    knobBounds.height
                );
                g2.dispose();
            }
        });

        accionesSlider.setMinimum(0);
        accionesSlider.setMaximum(100);
        accionesSlider.setBackground(CARD);
        accionesSlider.setForeground(ACCENT);
        accionesSlider.setPaintTicks(false);
        accionesSlider.setPaintLabels(false);
        accionesSlider.setValue(0);

        accionesField = new JTextField("0");
        accionesField.setFont(SUBTITLE_FONT);
        accionesField.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
            )
        );
        accionesField.setMaximumSize(new Dimension(120, 30));
        accionesField.setHorizontalAlignment(JTextField.CENTER);
        accionesField.setColumns(10);

        costeTotalLabel = new JLabel("Coste total: ₲0");
        costeTotalLabel.setFont(SUBTITLE_FONT);
        costeTotalLabel.setForeground(ACCENT);
        costeTotalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        barra.add(accionesSlider);
        barra.add(Box.createVerticalStrut(4));
        barra.add(accionesField);
        barra.add(Box.createVerticalStrut(4));
        barra.add(costeTotalLabel);

        root.add(barra);    // índice 1

        // =================================================
        // PANEL DERECHO — info empresa (componente 2)
        // =================================================

        JPanel derecha = new JPanel();
        derecha.setLayout(new BoxLayout(derecha, BoxLayout.Y_AXIS));
        derecha.setBackground(CARD);
        derecha.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
            )
        );

        nombreLabel            = crearLabel("");
        accionesMercadoLabel   = crearLabel("");
        accionesPropiedadLabel = crearLabel("");
        valorAccionLabel       = crearLabel("");

        // Nombre de la empresa
        nombreLabel.setFont(TITLE_FONT);
        nombreLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        derecha.add(nombreLabel);
        derecha.add(Box.createVerticalStrut(8));

        // Descripción expandible
        derecha.add(crearDescripcionExpandible(""));
        derecha.add(Box.createVerticalStrut(12));

        // Separador fino
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        derecha.add(sep);
        derecha.add(Box.createVerticalStrut(8));

        // Datos de mercado
        derecha.add(accionesMercadoLabel);
        derecha.add(valorAccionLabel);
        derecha.add(accionesPropiedadLabel);

        derecha.add(Box.createVerticalGlue());

        comprarButton = crearBoton("Comprar");
        comprarButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        comprarButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        venderButton = crearBoton("Vender");
        venderButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        venderButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        derecha.add(comprarButton);
        derecha.add(Box.createVerticalStrut(6));
        derecha.add(venderButton);

        root.add(derecha);  // índice 2

        return root;
    }
    
    

    // =====================================================
    // HELPERS
    // =====================================================

    private JLabel crearLabel(String txt) {

        JLabel l = new JLabel(txt);

        l.setForeground(TEXT);
        l.setFont(NORMAL_FONT);

        l.setAlignmentX(Component.LEFT_ALIGNMENT);

        l.setBorder(
            BorderFactory.createEmptyBorder(4, 0, 4, 0)
        );

        l.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 40)
        );

        return l;
    }

    private JButton crearBoton(String txt) {

        JButton b = new JButton(txt);

        b.setFont(BUTTON_FONT);

        b.setAlignmentX(Component.CENTER_ALIGNMENT);

        b.setFocusPainted(false);

        b.setBackground(ACCENT);
        b.setForeground(Color.WHITE);

        b.setBorder(
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        );

        b.setCursor(
            new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR)
        );

        return b;
    }
    
    public void updateMarketInfo(CompanyData c) {
        if (c == null) return;

        valorAccionLabel.setText(
            String.format("Valor por acción: ₲%.2f", c.getValorAccion())
        );

        int disponibles = c.getAccionesMercado() - c.getAccionesPropiedad();
        accionesMercadoLabel.setText(
            "Acciones disponibles: " + disponibles
        );
    }
    
    private JPanel crearDescripcionExpandible(String texto) {

        descripcionPanel = new JPanel();
        descripcionPanel.setLayout(new BorderLayout());
        descripcionPanel.setBackground(CARD);
        descripcionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        descripcionPanel.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE)
        );

        // ── Cabecera clicable ──────────────────────────────
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setBackground(new Color(235, 240, 255));
        cabecera.setBorder(
            BorderFactory.createEmptyBorder(4, 6, 4, 6)
        );
        cabecera.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 28)
        );
        cabecera.setCursor(
            new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR)
        );

        JLabel flecha = new JLabel((descripcionExpanded ? "-" : "+") + "  Descripción");
        flecha.setFont(NORMAL_FONT);
        flecha.setForeground(ACCENT);
        cabecera.add(flecha, BorderLayout.WEST);

        // ── Cuerpo con scroll (oculto por defecto) ─────────
        JTextArea area = new JTextArea(texto);
        area.setFont(NORMAL_FONT);
        area.setForeground(TEXT);
        area.setBackground(new Color(248, 250, 255));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);
        area.setBorder(
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        );

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(null);
        scroll.setPreferredSize(new Dimension(0, 80));
        scroll.setVisible(false);  // oculto por defecto

        // ── Toggle al hacer clic en la cabecera ────────────
        cabecera.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                descripcionExpanded = !descripcionExpanded;
                scroll.setVisible(descripcionExpanded);
                flecha.setText(
                    (descripcionExpanded ? "-" : "+") + "  Descripción"
                );
                descripcionPanel.revalidate();
                descripcionPanel.repaint();
                // Forzar reflow del panel padre
                descripcionPanel.getParent().revalidate();
                descripcionPanel.getParent().repaint();
            }
        });

        descripcionPanel.add(cabecera, BorderLayout.NORTH);
        descripcionPanel.add(scroll,   BorderLayout.CENTER);

        return descripcionPanel;
    }
    
    public void setDescripcion(String texto) {
        // Busca el JTextArea dentro del scroll dentro del descripcionPanel
        if (descripcionPanel == null) return;
        BorderLayout bl = (BorderLayout) descripcionPanel.getLayout();
        Component center = bl.getLayoutComponent(BorderLayout.CENTER);
        if (center instanceof JScrollPane sp) {
            if (sp.getViewport().getView() instanceof JTextArea area) {
                area.setText(texto);
            }
        }
    }
    
    public void refreshOwnership(CompanyData c) {
        if (c == null) return;

        accionesPropiedadLabel.setText(
            "En propiedad: " + c.getAccionesPropiedad()
        );

        int disponibles = c.getAccionesMercado() - c.getAccionesPropiedad();
        accionesMercadoLabel.setText(
            "Disponibles: " + disponibles
        );
    }
    
    public void setCompanies(
            java.util.List<CompanyData> companies
    ) {

        companyDropdown.removeAllItems();

        for (CompanyData c : companies) {
            companyDropdown.addItem(c);
        }
    }
    
    public void setCompanyInfo(CompanyData c) {
        if (c == null) return;

        nombreLabel.setFont(TITLE_FONT);
        nombreLabel.setText(c.getNombre());

        // Descripción en el panel expandible
        setDescripcion(c.getActividad() != null ? c.getActividad() : "");

        // Los datos de mercado los actualiza updateMarketInfo
        updateMarketInfo(c);

        // Acciones en propiedad (estático hasta compra/venta)
        accionesPropiedadLabel.setText(
            "En propiedad: " + c.getAccionesPropiedad()
        );
    }
    
    public void setSelectedActions(int actions) {

        accionesField.setText(
                String.valueOf(actions)
        );
    }
    
    public void setCost(double cost) {

        costeTotalLabel.setText(
                "Coste total: ₲" + cost
        );
    }
    
    public JComboBox<CompanyData> getCompanyDropdown() {
        return companyDropdown;
    }
    
    public JSlider getSlider() {
        return accionesSlider;
    }
    
    public JTextField getAccionesField() {
        return accionesField;
    }
    
    public JButton getComprarButton() {
        return comprarButton;
    }
    
    public JButton getVenderButton() { 
    	return venderButton; 
    }
    
    public void setChartEmpresa(int id) {
        chart.setEmpresaId(id);
    }
    
    
}