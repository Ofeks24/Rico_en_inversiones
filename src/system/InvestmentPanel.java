package system;

import javax.swing.*;
import java.awt.*;

public class InvestmentPanel extends JPanel {

    

    public InvestmentPanel() {
        

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        setBackground(Color.WHITE);

        add(crearBarraSuperior(), BorderLayout.NORTH);
        add(crearContenido(), BorderLayout.CENTER);
    }

    private JPanel crearBarraSuperior() {
        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);

        JLabel titulo = new JLabel("(Nombre de empresa en panel desplegable)");
        titulo.setForeground(Color.BLACK);

        JButton cerrar = new JButton("X");
        cerrar.setForeground(Color.BLACK);
        cerrar.setBackground(Color.WHITE);
        cerrar.setFocusPainted(false);

        cerrar.addActionListener(e -> {
            Container parent = getParent();
            if (parent != null) {
                parent.remove(this);
                parent.repaint();
            }
        });

        top.add(titulo, BorderLayout.WEST);
        top.add(cerrar, BorderLayout.EAST);

        return top;
    }

    private JPanel crearContenido() {

        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();

        // =========================
        // IZQUIERDA (GRÁFICA + SLIDER)
        // =========================
        JPanel izquierda = new JPanel(new BorderLayout());
        izquierda.setBackground(Color.WHITE);

        JPanel grafica = new JPanel(new BorderLayout());
        grafica.setPreferredSize(new Dimension(500, 300));
        grafica.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        grafica.setBackground(Color.WHITE);
        

        JLabel lblGrafica = new JLabel("(Gráfica)", SwingConstants.CENTER);
        lblGrafica.setForeground(Color.BLACK);
        grafica.add(lblGrafica);

        JPanel barra = new JPanel();
        barra.setPreferredSize(new Dimension(500, 40));
        barra.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        barra.setBackground(Color.WHITE);

        izquierda.add(grafica, BorderLayout.CENTER);
        izquierda.add(barra, BorderLayout.SOUTH);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10,10,10,10);

        root.add(izquierda, gbc);

        // =========================
        // DERECHA (INFO + BOTONES)
        // =========================
        JPanel derecha = new JPanel();
        derecha.setLayout(new BoxLayout(derecha, BoxLayout.Y_AXIS));
        derecha.setBackground(Color.WHITE);

        derecha.add(crearLabel("Nombre: (Nombre entero de la empresa)"));
        derecha.add(Box.createVerticalStrut(10));
        derecha.add(crearLabel("Actividad principal: (Actividad de la empresa)"));

        derecha.add(Box.createVerticalStrut(30));

        derecha.add(crearLabel("Acciones en el mercado: (Número)"));
        derecha.add(Box.createVerticalStrut(10));
        derecha.add(crearLabel("Acciones en propiedad: (Número)"));

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

    private JLabel crearLabel(String txt) {
        JLabel l = new JLabel(txt);
        l.setForeground(Color.BLACK);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
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
}