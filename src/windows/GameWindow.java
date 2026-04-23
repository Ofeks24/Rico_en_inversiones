package windows;
import javax.swing.*;

import tools.Utils;

import java.awt.*;
import java.awt.event.*;

public class GameWindow extends JFrame {

    public GameWindow() {

        setTitle("Ventana Pantalla Completa");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);


        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BorderLayout());
        panelPrincipal.setBackground(new Color(85, 171, 170));


        JPanel rectanguloInferior = new JPanel();
        rectanguloInferior.setBackground(new Color(195, 199, 200));
        rectanguloInferior.setPreferredSize(new Dimension(0, 90));
        rectanguloInferior.setLayout(new SpringLayout());
        



        JButton botonSalir = new JButton(Utils.escalarIcono(new ImageIcon("res/logos/Doors(Closed).png"), 50));
        botonSalir.setRolloverIcon(Utils.escalarIcono(new ImageIcon("res/logos/Doors(Open).png"), 50));
        botonSalir.setPressedIcon(Utils.escalarIcono(new ImageIcon("res/logos/Doors(Open).png"), 50));
        botonSalir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        botonSalir.setBorderPainted(false);
        botonSalir.setContentAreaFilled(false);
        botonSalir.setFocusPainted(false);
        botonSalir.setOpaque(false);  

        rectanguloInferior.setLayout(new GridBagLayout());
        rectanguloInferior.add(botonSalir);

        panelPrincipal.add(rectanguloInferior, BorderLayout.SOUTH);
        

        add(panelPrincipal);
        setVisible(true);
    }
}