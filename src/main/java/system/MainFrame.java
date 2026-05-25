package system;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.Timer;

import tools.Clock;
import tools.ScreenManager;

public class MainFrame extends JFrame {

    private ScreenManager       screenManager;
    private GraphBackgroundPanel background;
    private JLayeredPane        layeredPane;
    private Clock               time = new Clock();

    // Resolución real de la pantalla
    public static final Dimension SCREEN =
        Toolkit.getDefaultToolkit().getScreenSize();

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

    private void initLayout() {

        background.setBounds(0, 0, SCREEN.width, SCREEN.height);

        JPanel screens = screenManager.getContainer();
        screens.setBounds(0, 0, SCREEN.width, SCREEN.height);
        screens.setOpaque(false);

        layeredPane.add(background, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(screens,    JLayeredPane.PALETTE_LAYER);

        setContentPane(layeredPane);
    }

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

    private void initAnimation() {
        Timer graphTimer = new Timer(16, e -> repaint());
        graphTimer.start();
    }
}