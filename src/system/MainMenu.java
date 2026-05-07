package system;

import javax.swing.*;
import java.awt.*;

import tools.BotonAjustable;
import tools.Utils;

public class MainMenu extends JPanel {

	private Runnable onStart;
	private Runnable onOptions;
	private Runnable onExit;

    private JLabel logo;

    private final BotonAjustable[] botones = {
            new BotonAjustable(new JButton(), 500),
            new BotonAjustable(new JButton(), 1000),
            new BotonAjustable(new JButton(), 1500)
    };


    private final ImageIcon logoIcon = new ImageIcon("res/logos/Rico en inversiones_logo.png");

    public MainMenu(Runnable toGame, Runnable toOptions, Runnable exit) {

        this.onStart = toGame;
        this.onOptions = toOptions;
        this.onExit = exit;

        setLayout(null);
        setDoubleBuffered(true);

        initUI();
        
    }

    private void initUI() {

        setLayout(new BorderLayout());
        setOpaque(false);

        // ===== PANEL IZQUIERDO (MENÚ) =====
        JPanel panelIzquierdo = new JPanel();
        panelIzquierdo.setLayout(new BoxLayout(panelIzquierdo, BoxLayout.Y_AXIS));
        panelIzquierdo.setOpaque(false);
        panelIzquierdo.setBorder(BorderFactory.createEmptyBorder(0, 80, 0, 0)); // margen izquierda

        panelIzquierdo.add(Box.createVerticalGlue());

        botones[0].setBoton(crearBoton("Empezar"));
        botones[1].setBoton(crearBoton("Opciones"));
        botones[2].setBoton(crearBoton("Salir"));

        botones[0].getBoton().addActionListener(e -> {
            if (onStart != null) onStart.run();
        });

        botones[1].getBoton().addActionListener(e -> {
            if (onOptions != null) onOptions.run();
        });

        botones[2].getBoton().addActionListener(e -> {
            if (onExit != null) onExit.run();
        });

        for (BotonAjustable b : botones) {
            JButton btn = b.getBoton();

            btn.setAlignmentX(Component.LEFT_ALIGNMENT); // 👈 alineado a la izquierda

            panelIzquierdo.add(btn);
            panelIzquierdo.add(Box.createRigidArea(new Dimension(0, 25)));
        }

        panelIzquierdo.add(Box.createVerticalGlue());

        add(panelIzquierdo, BorderLayout.WEST);

        // ===== PANEL CENTRAL (LOGO) =====
        JPanel panelCentro = new JPanel(new GridBagLayout());
        panelCentro.setOpaque(false);

        logo = new JLabel(logoIcon);
        panelCentro.add(logo); // centrado automáticamente

        add(panelCentro, BorderLayout.CENTER);
    }

    private JButton crearBoton(String texto) {
    	int factor = 454;
        JButton b = new JButton(Utils.escalarIcono("res/sprites/"+texto+"(Normal).png",factor));
        b.setRolloverIcon(Utils.escalarIcono("res/sprites/"+texto+"(Hover).png",factor));
        b.setPressedIcon(Utils.escalarIcono("res/sprites/"+texto+"(Click).png",factor));


        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setDoubleBuffered(true);
        

        return b;
    }
}