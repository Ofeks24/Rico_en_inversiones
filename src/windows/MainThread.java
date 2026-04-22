package windows;

import javax.swing.SwingUtilities;

public class MainThread {

	public static void main(String[] args) {

		SwingUtilities.invokeLater(() -> {

		    GameWindow gameWindow = new GameWindow();
		    gameWindow.setVisible(false);

		    MainMenu mainMenu = new MainMenu(gameWindow);
		    mainMenu.setVisible(false);

		    LoadingScreen splash = new LoadingScreen(mainMenu);

		    splash.startAnimation();
		});

    }

}
