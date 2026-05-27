package system;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import tools.Clock;
import tools.ScreenManager;

/**
 * Ventana principal de la aplicación. Actúa como contenedor raíz de todas
 * las pantallas del juego y configura la estructura de capas de Swing.
 *
 * <p>Usa un {@link JLayeredPane} para superponer:</p>
 * <ul>
 *   <li>Capa {@code DEFAULT_LAYER}: {@link GraphBackgroundPanel} animado.</li>
 *   <li>Capa {@code PALETTE_LAYER}: contenedor de pantallas gestionado por
 *       {@link tools.ScreenManager}.</li>
 * </ul>
 *
 * <p>Registra las pantallas {@code LOADING}, {@code MENU}, {@code GAME} y
 * {@code OPTIONS}, y arranca la aplicación mostrando la pantalla de carga.</p>
 */
public class MainFrame extends JFrame {

    private ScreenManager       screenManager;
    private GraphBackgroundPanel background;
    private JLayeredPane        layeredPane;
    private Clock               time = new Clock();

    // Resolución real de la pantalla
    public static final Dimension SCREEN =
        Toolkit.getDefaultToolkit().getScreenSize();

    /**
     * Construye el frame principal: inicializa el layout en capas, registra
     * todas las pantallas y arranca el temporizador de repintado del fondo.
     *
     * <p>El frame se crea sin decoración ({@code setUndecorated(true)}) y se
     * maximiza al inicio. El tamaño inicial se establece al 85% de la
     * resolución de pantalla antes de maximizar.</p>
     */
    public MainFrame() {

        screenManager = new ScreenManager();
        background    = new GraphBackgroundPanel();
        layeredPane   = new JLayeredPane();

        setUndecorated(true);

        initLayout();
        initScreens();
        initAnimation();

        setTitle("Rico en inversiones");
        // Tamaño inicial = 2/3 de la pantalla; arranca maximizado de todos modos
        setSize((int)(SCREEN.width * 0.85), (int)(SCREEN.height * 0.85));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    /**
     * Configura el {@link JLayeredPane} como panel de contenido y añade el
     * fondo animado y el contenedor de pantallas en sus capas respectivas.
     * Ambas capas se dimensionan a la resolución completa de la pantalla.
     */
    private void initLayout() {

        background.setBounds(0, 0, SCREEN.width, SCREEN.height);

        JPanel screens = screenManager.getContainer();
        screens.setBounds(0, 0, SCREEN.width, SCREEN.height);
        screens.setOpaque(false);

        layeredPane.add(background, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(screens,    JLayeredPane.PALETTE_LAYER);

        setContentPane(layeredPane);
    }

    /**
     * Crea e instancia todas las pantallas del juego y las registra en el
     * {@link tools.ScreenManager}.
     *
     * <p>El orden de dependencias es el siguiente:</p>
     * <ol>
     *   <li>{@link GameWindow} — necesita el {@link Clock} y el callback de salida.</li>
     *   <li>{@link MainMenu}   — necesita callbacks hacia {@code GAME} y {@code OPTIONS}.</li>
     *   <li>{@link OptionsWindow} — necesita el callback de vuelta y el {@link Clock}.</li>
     *   <li>{@link LoadingScreen} — necesita el callback hacia {@code MENU}.</li>
     * </ol>
     *
     * <p>Tras registrar todas las pantallas, muestra {@code LOADING} y arranca
     * su animación de fade-in.</p>
     */
    private void initScreens() {

        GameWindow game = new GameWindow(
            () -> screenManager.showScreen("MENU"), time
        );

        MainMenu menu = new MainMenu(
            () -> screenManager.showScreen("GAME"),
            () -> screenManager.showScreen("OPTIONS"),
            () -> System.exit(0)
        );

        OptionsWindow options = new OptionsWindow(
            () -> screenManager.showScreen("MENU"),
            () -> game.getStatsController().reset(),
            time
        );

        LoadingScreen loading = new LoadingScreen(
            () -> screenManager.showScreen("MENU")
        );

        menu.setOpaque(false);
        options.setOpaque(false);

        screenManager.addScreen("LOADING", loading);
        screenManager.addScreen("MENU",    menu);
        screenManager.addScreen("GAME",    game);
        screenManager.addScreen("OPTIONS", options);

        screenManager.showScreen("LOADING");
        loading.startAnimation();
    }

    /**
     * Inicia el temporizador de repintado continuo del panel de fondo animado.
     * El repintado se realiza cada 16 ms, lo que corresponde a aproximadamente
     * 60 fotogramas por segundo.
     */
    private void initAnimation() {
        Timer graphTimer = new Timer(16, e -> repaint());
        graphTimer.start();
    }
}