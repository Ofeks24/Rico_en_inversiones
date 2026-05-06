package windows;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import tools.Clock;
import tools.DesktopGridLayout;
import tools.Utils;

import java.awt.*;
import java.awt.event.*;

public class GameWindow extends JPanel {

    private JPanel iconoSeleccionado = null;
    private Clock time;

    public GameWindow(Runnable exit, Clock time) {

        //setLayout(new GridBagLayout());
    	this.time=time;
        JPanel root = new JPanel(new GridBagLayout());
        setLayout(new BorderLayout());
        add(root, BorderLayout.CENTER);
        //root.setOpaque(true);
        //setOpaque(true);

        GridBagConstraints gbc = new GridBagConstraints();

        // =================================================
        // ESCRITORIO (FONDO)
        // =================================================
        JPanel escritorio = new JPanel();
        escritorio.setLayout(new DesktopGridLayout());
        escritorio.setBackground(new Color(85, 171, 170));

        escritorio.add(crearIcono("Mi PC", Utils.escalarIcono("res/logos/Doors(Closed).png", 50)));
        escritorio.add(crearIcono("Documentos", Utils.escalarIcono("res/logos/Doors(Closed).png", 50)));
        escritorio.add(crearIcono("Internet", Utils.escalarIcono("res/logos/Doors(Closed).png", 50)));
        escritorio.add(crearIcono("Papelera", Utils.escalarIcono("res/logos/Doors(Closed).png", 50)));

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

        inicio.addActionListener(e -> {
            if (exit != null) exit.run();
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
        JLabel hora = new JLabel();
        JLabel fecha = new JLabel();
        hora.setForeground(Color.BLACK);
        hora.setBorder(new EmptyBorder(0, 0, 0, 15));
        fecha.setForeground(Color.BLACK);
        fecha.setBorder(new EmptyBorder(0, 0, 0, 15));

        // actualización inicial
        hora.setText(String.format(
    	    "%02d:%02d",
    	    time.getHour(),
    	    time.getMinute()
    	));
        fecha.setText(String.format(
    	    "%02d/%02d/%04d",
    	    time.getDay(),
    	    time.getMonth(),
    	    time.getYear()
    	));

        // escuchar cambios
        time.addListener(() -> {
            hora.setText(String.format(
        	    "%02d:%02d",
        	    time.getHour(),
        	    time.getMinute()
        	));
            fecha.setText(String.format(
        	    "%02d/%02d/%04d",
        	    time.getDay(),
        	    time.getMonth(),
        	    time.getYear()
        	));
        });

        JPanel panelHora = new JPanel(new GridLayout(2,1));
        panelHora.setOpaque(false);

        panelHora.add(hora);
        panelHora.add(fecha);

        barra.add(panelHora, BorderLayout.EAST);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        root.add(barra, gbc);

        add(root);
    }

    // =====================================================
    // ICONO DE ESCRITORIO
    // =====================================================
    private JPanel crearIcono(String texto, ImageIcon imagen) {

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(true);
        p.setBackground(new Color(0, 0, 0, 0));

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