package windows;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameWindow extends JFrame {

    public GameWindow() {
        // Configuración básica de la ventana
        setTitle("Ventana Pantalla Completa");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true); // Quita bordes y barra de título
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Pantalla completa

        // Panel principal
        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BorderLayout());

        // Rectángulo inferior
        JPanel rectanguloInferior = new JPanel();
        rectanguloInferior.setBackground(Color.BLUE); // Color sólido
        rectanguloInferior.setPreferredSize(new Dimension(0, 120)); // Altura del rectángulo

        // Botón para cerrar
        JButton botonSalir = new JButton("Salir");
        botonSalir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        rectanguloInferior.setLayout(new GridBagLayout());
        rectanguloInferior.add(botonSalir);

        // Añadir componentes
        panelPrincipal.add(rectanguloInferior, BorderLayout.SOUTH);

        add(panelPrincipal);
        setVisible(true);
    }
}