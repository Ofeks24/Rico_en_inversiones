package windows;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.*;

import tools.Clock;
import tools.ScreenManager;

public class MainFrame extends JFrame {

    private ScreenManager screenManager;
    private GraphBackgroundPanel background;
    private JLayeredPane layeredPane;

    public MainFrame() {

        screenManager = new ScreenManager();
        background = new GraphBackgroundPanel();
        layeredPane = new JLayeredPane();

        setUndecorated(true);

        initLayout();
        initScreens();
        initAnimation();

        setSize(1280, 720); // o pack()
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void initLayout() {

        // Usamos tamaño del frame (puedes hacerlo dinámico si quieres)
    	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    	background.setBounds(0, 0, screenSize.width, screenSize.height);

    	JPanel screens = screenManager.getContainer();
    	screens.setBounds(0, 0, screenSize.width, screenSize.height);
    	screens.setOpaque(false);

        // Añadir capas
        layeredPane.add(background, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(screens, JLayeredPane.PALETTE_LAYER);

        setContentPane(layeredPane);
    }

    private void initScreens() {

        GameWindow game = new GameWindow(() ->
                screenManager.showScreen("MENU"),new Clock()
        );

        MainMenu menu = new MainMenu(
                () -> screenManager.showScreen("GAME"),
                () -> screenManager.showScreen("OPTIONS"),
                () -> System.exit(0)
        );

        OptionsWindow options = new OptionsWindow(() ->
                screenManager.showScreen("MENU")
        );

        LoadingScreen loading = new LoadingScreen(() ->
                screenManager.showScreen("MENU")
        );

        // 👇 IMPORTANTE: transparencias
        menu.setOpaque(false);
        options.setOpaque(false);

        screenManager.addScreen("LOADING", loading);
        screenManager.addScreen("MENU", menu);
        screenManager.addScreen("GAME", game);
        screenManager.addScreen("OPTIONS", options);

        screenManager.showScreen("LOADING");
        loading.startAnimation();
    }

    private void initAnimation() {

        Timer graphTimer = new Timer(16, e -> {
            repaint();
        });

        graphTimer.start();
    }
}