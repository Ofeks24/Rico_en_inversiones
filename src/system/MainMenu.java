package system;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import tools.BotonAjustable;
import tools.Screen;
import tools.Utils;

public class MainMenu extends JPanel implements Screen {

	private Runnable onStart;
	private Runnable onOptions;
	private Runnable onExit;

    private JLabel logo;
    
    private final List<Point> posicionesFinales = new ArrayList<>();
    private Timer animationTimer;
    private long animationStart;

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

            btn.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Empezamos fuera de pantalla
            btn.setLocation(-800, 0);

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
    
    private void startButtonsAnimation() {

        // Esperamos a que Swing termine el layout
        SwingUtilities.invokeLater(() -> {

            posicionesFinales.clear();

            // Guardamos posiciones reales
            for (BotonAjustable b : botones) {
                JButton btn = b.getBoton();
                posicionesFinales.add(btn.getLocation());
            }

            // Movemos todos fuera de pantalla
            for (BotonAjustable b : botones) {
                JButton btn = b.getBoton();
                btn.setLocation(-700, btn.getY());
            }

            animationStart = System.currentTimeMillis();

            if (animationTimer != null && animationTimer.isRunning()) {
                animationTimer.stop();
            }

            animationTimer = new Timer(1, e -> {

                long tiempoActual = System.currentTimeMillis();
                long elapsed = tiempoActual - animationStart;

                boolean terminado = true;

                for (int i = 0; i < botones.length; i++) {

                    JButton btn = botones[i].getBoton();
                    Point destino = posicionesFinales.get(i);

                    // Cascada
                    long delay = i * 200;

                    long tiempoBoton = elapsed - delay;

                    if (tiempoBoton < 0) {
                        terminado = false;
                        continue;
                    }

                    double duracion = 1000.0;
                    double t = Math.min(tiempoBoton / duracion, 1.0);

                    // EaseOutBack = efecto rebote
                    double c1 = 1.70158;
                    double c3 = c1 + 1;

                    double eased = 1 + c3 * Math.pow(t - 1, 3)
                            + c1 * Math.pow(t - 1, 2);

                    int startX = -700;
                    int finalX = destino.x;

                    int x = (int) (startX + (finalX - startX) * eased);

                    btn.setLocation(x, destino.y);

                    if (t < 1.0) {
                        terminado = false;
                    }
                }

                //repaint();

                if (terminado) {
                    animationTimer.stop();
                }
            });

            animationTimer.start();
        });
    }

	@Override
	public void onShow() {
		startButtonsAnimation();
		
	}

	@Override
	public void onHide() {
		// TODO Auto-generated method stub
		
	}
}