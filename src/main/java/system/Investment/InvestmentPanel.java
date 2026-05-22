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
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicSliderUI;

import system.Stats.StatsController;
import tools.CompanyData;



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

    private JLabel nombreLabel;
    private JLabel actividadLabel;
    private JLabel accionesMercadoLabel;
    private JLabel accionesPropiedadLabel;
    private JLabel valorAccionLabel;

    private JLabel lblGrafica;
    
    private JSlider accionesSlider;

    JTextField accionesField;
    private JLabel costeTotalLabel;
    
    private JButton comprarButton;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public InvestmentPanel() {



        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        setBackground(BG);

        add(crearBarraSuperior(), BorderLayout.NORTH);
        add(crearContenido(), BorderLayout.CENTER);

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

    private JPanel crearContenido() {

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();

        // =================================================
        // IZQUIERDA
        // =================================================

        JPanel izquierda = new JPanel(new BorderLayout());
        izquierda.setBackground(Color.WHITE);

        JPanel grafica = new JPanel(new BorderLayout());
        grafica.setPreferredSize(new Dimension(500, 300));
        grafica.setBackground(CARD);

        grafica.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220,220,220)),
                BorderFactory.createEmptyBorder(15,15,15,15)
            )
        );
        

        lblGrafica = new JLabel(
                "(Gráfica)",
                SwingConstants.CENTER
        );
        lblGrafica.setFont(TITLE_FONT);

        lblGrafica.setForeground(Color.BLACK);

        grafica.add(lblGrafica);

        JPanel barra = new JPanel();
        barra.setLayout(new BoxLayout(barra, BoxLayout.Y_AXIS));
        barra.setBackground(CARD);

        barra.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220,220,220)),
                BorderFactory.createEmptyBorder(15,15,15,15)
            )
        );

        accionesSlider = new JSlider();
        
        accionesSlider.setBorder(
        	    BorderFactory.createEmptyBorder(
        	        15, 5, 15, 5
        	    )
        	);
        
        accionesSlider.setUI(new BasicSliderUI(accionesSlider) {

            @Override
            public void paintThumb(Graphics g) {

                Graphics2D g2 = (Graphics2D) g.create();
                
                Rectangle knobBounds = thumbRect;
                

                g2.setColor(ACCENT);

                // barra vertical
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
                BorderFactory.createLineBorder(new Color(200,200,200)),
                BorderFactory.createEmptyBorder(8,10,8,10)
            )
        );
        


        accionesField.setMaximumSize(
            new Dimension(120, 25)
        );

        accionesField.setHorizontalAlignment(
            JTextField.CENTER
        );

        costeTotalLabel = new JLabel(
            "Coste total: $0"
        );
        
        costeTotalLabel.setFont(SUBTITLE_FONT);
        costeTotalLabel.setForeground(ACCENT);
        
        accionesField.setColumns(10);

        //accionesSeleccionadasLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        costeTotalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        barra.add(accionesSlider);
        barra.add(Box.createVerticalStrut(5));
        barra.add(accionesField);
        barra.add(costeTotalLabel);

        izquierda.add(grafica, BorderLayout.CENTER);
        izquierda.add(barra, BorderLayout.SOUTH);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10,10,10,10);

        root.add(izquierda, gbc);

        // =================================================
        // DERECHA
        // =================================================

        JPanel derecha = new JPanel();

        derecha.setLayout(
                new BoxLayout(derecha, BoxLayout.Y_AXIS)
        );

        derecha.setBackground(Color.WHITE);
        derecha.setBackground(CARD);

        derecha.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220,220,220)),
                BorderFactory.createEmptyBorder(20,20,20,20)
            )
        );

        nombreLabel = crearLabel("");
        actividadLabel = crearLabel("");
        accionesMercadoLabel = crearLabel("");
        accionesPropiedadLabel = crearLabel("");
        valorAccionLabel = crearLabel("");

        derecha.add(nombreLabel);
        derecha.add(Box.createVerticalStrut(10));

        derecha.add(actividadLabel);
        derecha.add(Box.createVerticalStrut(30));

        derecha.add(accionesMercadoLabel);
        derecha.add(Box.createVerticalStrut(10));
        
        derecha.add(valorAccionLabel);
        derecha.add(Box.createVerticalStrut(10));

        derecha.add(accionesPropiedadLabel);

        derecha.add(Box.createVerticalGlue());

        comprarButton = crearBoton("Comprar");
        
        
        
        JButton vender = crearBoton("Vender");

        comprarButton.setMaximumSize(new Dimension(140, 30));
        vender.setMaximumSize(new Dimension(140, 30));

        derecha.add(comprarButton);
        derecha.add(Box.createVerticalStrut(10));
        derecha.add(vender);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10,0,10,10);

        root.add(derecha, gbc);

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
    
    public void setCompanies(
            java.util.List<CompanyData> companies
    ) {

        companyDropdown.removeAllItems();

        for (CompanyData c : companies) {
            companyDropdown.addItem(c);
        }
    }
    
    public void setCompanyInfo(CompanyData c) {
    	nombreLabel.setFont(TITLE_FONT);
    	actividadLabel.setFont(NORMAL_FONT);

        if (c == null) return;

        nombreLabel.setText(c.getNombre());

        actividadLabel.setText(
                "<html><div style='text-align:left;'>Descripcion:<br>" +
                c.getActividad()+
                "</div></html>"
        );

        accionesMercadoLabel.setText(
                "Acciones en el mercado: " +
                c.getAccionesMercado()
        );

        accionesPropiedadLabel.setText(
                "Acciones en propiedad: " +
                c.getAccionesPropiedad()
        );

        valorAccionLabel.setText(
                "Valor por acción: $" +
                c.getValorAccion()
        );

        lblGrafica.setText(
                "(Gráfica de " + c.getNombre() + ")"
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
    
    
}