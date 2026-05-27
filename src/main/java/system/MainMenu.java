package system;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import tools.AudioManager;
import tools.BotonAjustable;
import tools.ButtonSoundHelper;
import tools.Screen;
import tools.Utils;

/**
 * Pantalla del menú principal del juego.
 *
 * <p>Presenta el logotipo del juego centrado y tres botones de acción
 * ({@code Empezar}, {@code Opciones}, {@code Salir}) alineados a la
 * izquierda con animación de entrada tipo <em>ease-out-back</em> (efecto
 * rebote) que se lanza cada vez que la pantalla se hace visible.</p>
 *
 * <p>Implementa {@link Screen} para responder al ciclo de vida gestionado
 * por {@link tools.ScreenManager}.</p>
 */
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
    
    
    private String ruta = "/main/resources/logos/";
    private String ruta2 = "/main/resources/sprites/";


    private final ImageIcon logoIcon = Utils.icon(ruta+"Rico en inversiones_logo.png");

    /**
     * Construye el menú principal registrando los callbacks de navegación y
     * construyendo la interfaz de usuario.
     *
     * @param toGame    {@link Runnable} que navega a la pantalla de juego
     *                  ({@code GAME}).
     * @param toOptions {@link Runnable} que navega a la pantalla de opciones
     *                  ({@code OPTIONS}).
     * @param exit      {@link Runnable} que cierra la aplicación.
     */
    public MainMenu(Runnable toGame, Runnable toOptions, Runnable exit) {

        this.onStart = toGame;
        this.onOptions = toOptions;
        this.onExit = exit;

        setLayout(null);
        setDoubleBuffered(true);

        initUI();
        
    }

    /**
     * Construye y configura todos los componentes visuales del menú:
     * panel izquierdo con los botones, panel central con el logotipo,
     * iconos de estado hover/click y sonidos de hover para cada botón.
     */
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
        ButtonSoundHelper.addHoverSound(botones[0].getBoton(),"/main/resources/audio/sfx/ping-menu-sound.wav");

        botones[1].getBoton().addActionListener(e -> {
            if (onOptions != null) onOptions.run();
        });
        ButtonSoundHelper.addHoverSound(botones[1].getBoton(),"/main/resources/audio/sfx/ping-menu-sound.wav");

        botones[2].getBoton().addActionListener(e -> {
            if (onExit != null) onExit.run();
        });
        ButtonSoundHelper.addHoverSound(botones[2].getBoton(),"/main/resources/audio/sfx/ping-menu-sound.wav");

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

    /**
     * Crea un botón de menú con imágenes para los estados normal, hover y
     * pulsado, sin borde ni fondo visible.
     *
     * @param texto Nombre del botón; determina la ruta de los recursos de
     *              imagen ({@code texto(Normal).png}, {@code texto(Hover).png},
     *              {@code texto(Click).png}).
     * @return      {@link JButton} configurado y listo para añadir al panel.
     */
    private JButton crearBoton(String texto) {
    	//int factor = 454;
        JButton b = new JButton(Utils.icon(ruta2+texto+"(Normal).png"));
        b.setRolloverIcon(Utils.icon(ruta2+texto+"(Hover).png"));
        b.setPressedIcon(Utils.icon(ruta2+texto+"(Click).png"));


        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setDoubleBuffered(true);
        

        return b;
    }
    
    /**
     * Lanza la animación de entrada de los tres botones del menú.
     *
     * <p>Los botones comienzan fuera de pantalla (x = -700) y se deslizan
     * hasta su posición de layout con una curva <em>ease-out-back</em> que
     * produce un ligero rebote al llegar al destino. Cada botón tiene un
     * retardo de 200 ms respecto al anterior (efecto cascada).</p>
     *
     * <p>El método utiliza {@link SwingUtilities#invokeLater} para asegurarse
     * de que las posiciones finales de layout ya están calculadas antes de
     * iniciar la animación.</p>
     */
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

    /**
     * Llamado por {@link tools.ScreenManager} cuando esta pantalla se hace
     * visible. Reproduce la música del menú y lanza la animación de entrada
     * de los botones.
     */
    @Override
    public void onShow() {
        AudioManager.getInstance().playMusic("/main/resources/audio/music/Cambio-Pixelado.wav");
        startButtonsAnimation();
    }

    /**
     * Llamado por {@link tools.ScreenManager} cuando esta pantalla deja de
     * ser visible. No realiza ninguna acción en la implementación actual.
     */
	@Override
	public void onHide() {
		// TODO Auto-generated method stub
		
	}
}