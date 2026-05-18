package system.Investment;

import javax.swing.*;

import tools.CompanyData;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;


public class InvestmentPanel extends JPanel {

    // =====================================================
    // EMPRESAS
    // =====================================================

	private final List<CompanyData> companies = new ArrayList<>();

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

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public InvestmentPanel() {

        initCompanies();

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        setBackground(Color.WHITE);

        add(crearBarraSuperior(), BorderLayout.NORTH);
        add(crearContenido(), BorderLayout.CENTER);

        // empresa inicial
        if (!companies.isEmpty()) {
            updateCompany(companies.get(0));
        }
    }

    // =====================================================
    // EMPRESAS DEMO
    // =====================================================

    private void initCompanies() {

    	companies.add(
    		    new CompanyData(
                        "Apple Inc.",
                        "Tecnología y electrónica",
                        1200000,
                        0,
                        12
                )
        );

    	companies.add(
    		    new CompanyData(
                        "Tesla Motors",
                        "Vehículos eléctricos",
                        950000,
                        0,
                        0.03
                )
        );

    	companies.add(
    		    new CompanyData(
                        "Amazon",
                        "Comercio online y cloud",
                        2000000,
                        0,
                        50
                )
        );
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

        companyDropdown = new JComboBox<>(
        	    companies.toArray(new CompanyData[0])
        	);

        companyDropdown.addActionListener(e -> {
        	
        CompanyData selected =
			    (CompanyData) companyDropdown.getSelectedItem();

			updateCompany(selected);
        		
        });

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
        grafica.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        grafica.setBackground(Color.WHITE);

        lblGrafica = new JLabel(
                "(Gráfica)",
                SwingConstants.CENTER
        );

        lblGrafica.setForeground(Color.BLACK);

        grafica.add(lblGrafica);

        JPanel barra = new JPanel();
        barra.setLayout(new BoxLayout(barra, BoxLayout.Y_AXIS));
        barra.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        barra.setBackground(Color.WHITE);

        accionesSlider = new JSlider();
        
        accionesSlider.setBorder(
        	    BorderFactory.createEmptyBorder(
        	        5, 5, 5, 5
        	    )
        	);
        
        accionesSlider.setUI(new BasicSliderUI(accionesSlider) {

            @Override
            public void paintThumb(Graphics g) {

                Graphics2D g2 = (Graphics2D) g.create();

                Rectangle knobBounds = thumbRect;

                g2.setColor(Color.BLACK);

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

        accionesSlider.setValue(0);

        accionesSlider.addChangeListener(e -> {
            actualizarInfoSlider();
        });

        accionesField = new JTextField("0");
        
        accionesField.addActionListener(e -> {
            actualizarSliderDesdeTexto();
        });
        
        accionesField.addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                actualizarSliderDesdeTexto();
            }
        });

        accionesField.setMaximumSize(
            new Dimension(120, 25)
        );

        accionesField.setHorizontalAlignment(
            JTextField.CENTER
        );

        costeTotalLabel = new JLabel(
            "Coste total: $0"
        );
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

        JButton comprar = crearBoton("Comprar");
        JButton vender = crearBoton("Vender");

        comprar.setMaximumSize(new Dimension(140, 30));
        vender.setMaximumSize(new Dimension(140, 30));

        derecha.add(comprar);
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
    // ACTUALIZAR EMPRESA
    // =====================================================

    private void updateCompany(CompanyData c) {

        if (c == null) return;

        nombreLabel.setText(
                "Nombre: " + c.getNombre()
        );

        actividadLabel.setText(
                "Actividad principal: " + c.getActividad()
        );

        accionesMercadoLabel.setText(
                "Acciones en el mercado: " + c.getAccionesMercado()
        );

        accionesPropiedadLabel.setText(
                "Acciones en propiedad: " + c.getAccionesPropiedad()
        );

        lblGrafica.setText(
                "(Gráfica de " + c.getNombre() + ")"
        );
        
        valorAccionLabel.setText(
        	    "Valor por acción: $" + c.getValorAccion()
        	);
        
        accionesSlider.setMaximum(
        	    c.getAccionesMercado()
        	);

        	accionesSlider.setValue(0);
    }
    
    private void actualizarInfoSlider() {

        CompanyData c =
                (CompanyData) companyDropdown.getSelectedItem();

        if (c == null) return;

        int acciones = accionesSlider.getValue();

        double coste =
                acciones * c.getValorAccion();

        accionesField.setText(
        	    String.valueOf(acciones)
        	);

        costeTotalLabel.setText(
                "Coste total: $" + coste
        );
    }

    // =====================================================
    // HELPERS
    // =====================================================

    private JLabel crearLabel(String txt) {

        JLabel l = new JLabel(txt);

        l.setForeground(Color.BLACK);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        return l;
    }

    private JButton crearBoton(String txt) {

        JButton b = new JButton(txt);

        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        b.setBackground(Color.WHITE);
        b.setForeground(Color.BLACK);

        return b;
    }
    
    private void actualizarSliderDesdeTexto() {

        try {

            int valor =
                    Integer.parseInt(
                            accionesField.getText()
                    );

            valor = Math.max(
                    accionesSlider.getMinimum(),
                    valor
            );

            valor = Math.min(
                    accionesSlider.getMaximum(),
                    valor
            );

            accionesSlider.setValue(valor);

        } catch (NumberFormatException ex) {

            accionesField.setText(
                    String.valueOf(
                            accionesSlider.getValue()
                    )
            );
        }
    }
}