package windows;

import javax.swing.SwingUtilities;

public class MainProcess {

	public static void main(String[] args) {

		SwingUtilities.invokeLater(() -> {
		    GameWindow gameWindow = new GameWindow();
		});

    }

}
