package windows;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import tools.Utils;

import java.awt.*;
import java.awt.event.*;

public class GameWindow extends JPanel {
	
	private JPanel iconoSeleccionado = null;

    public GameWindow() {


        setTitle("Escritorio Simulado");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Panel principal con GridBagLayout
        JPanel root = new JPanel(new GridBagLayout());
        setContentPane(root);
        setUndecorated(true);

        GridBagConstraints gbc = new GridBagConstraints();

        // =================================================
        // FONDO DEL ESCRITORIO
        // =================================================
        JPanel escritorio = new JPanel(null);
        escritorio.setBackground(new Color(85, 171, 170)); // azul escritorio

        // Iconos simulados
        escritorio.add(crearIcono("Mi PC", Utils.escalarIcono("res/logos/Doors(Closed).png", 50), 40, 40));
        escritorio.add(crearIcono("Documentos", Utils.escalarIcono("res/logos/Doors(Closed).png", 50), 40, 150));
        escritorio.add(crearIcono("Internet", Utils.escalarIcono("res/logos/Doors(Closed).png", 50), 40, 260));
        escritorio.add(crearIcono("Papelera", Utils.escalarIcono("res/logos/Doors(Closed).png", 50), 40, 370));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;

        root.add(escritorio, gbc);

        // =================================================
        // BARRA DE TAREAS
        // =================================================
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(new Color(195, 199, 200));
        barra.setPreferredSize(new Dimension(0, 50));

        // Botón inicio
        JButton inicio = new JButton(Utils.escalarIcono("res/logos/Doors(Closed).png", 25));
        inicio.setRolloverIcon(Utils.escalarIcono("res/logos/Doors(Open).png", 25));
        inicio.setPressedIcon(Utils.escalarIcono("res/logos/Doors(Open).png", 25));
        inicio.addActionListener(e->{
        	System.exit(0);
        });
        inicio.setFocusPainted(false);
        inicio.setBorderPainted(false);
        inicio.setContentAreaFilled(false);
        inicio.setOpaque(false);  
        barra.add(inicio, BorderLayout.WEST);

        // Zona central
        JPanel centroBarra = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        centroBarra.setOpaque(false);

        centroBarra.add(crearBotonBarra("Explorador"));
        centroBarra.add(crearBotonBarra("Chrome"));
        centroBarra.add(crearBotonBarra("Editor"));

        barra.add(centroBarra, BorderLayout.CENTER);

        // Hora
        JLabel hora = new JLabel("12:45");
        hora.setForeground(Color.WHITE);
        hora.setBorder(new EmptyBorder(0, 0, 0, 15));
        barra.add(hora, BorderLayout.EAST);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        root.add(barra, gbc);

        setVisible(true);
    }

    // =====================================================
    // ICONO DE ESCRITORIO
    // =====================================================
    private JPanel crearIcono(String texto, ImageIcon imagen, int x, int y) {

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBounds(x, y, 90, 90);
        p.setOpaque(true);
        p.setBackground(new Color(0,0,0,0));

        JLabel icono = new JLabel(imagen);
        icono.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nombre = new JLabel(texto);
        nombre.setForeground(Color.WHITE);
        nombre.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(icono);
        p.add(nombre);

        Color hover = new Color(0,120,255,80);
        Color seleccionado = new Color(0,120,255,140);
        Color transparente = new Color(0,0,0,0);

        p.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (iconoSeleccionado != p) {
                    p.setBackground(hover);
                }
                repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (iconoSeleccionado != p) {
                    p.setBackground(transparente);
                }
                repaint();
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {

                // quitar anterior
                if (iconoSeleccionado != null && iconoSeleccionado != p) {
                    iconoSeleccionado.setBackground(transparente);
                }

                // seleccionar nuevo
                iconoSeleccionado = p;
                p.setBackground(seleccionado);
                repaint();
            }
        });

        return p;
    }

    // =====================================================
    // BOTÓN BARRA TAREAS
    // =====================================================
    private JButton crearBotonBarra(String txt) {

        JButton b = new JButton(txt);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(110, 32));

        return b;
    }
}