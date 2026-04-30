package windows;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Image imagen=new Image("res/logos/Rico en inversiones_logo.png");

    public MainFrame() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        CardLayout layout = new CardLayout();
        JPanel container = new JPanel(layout);

        LoadingScreen loading = new LoadingScreen(() -> {
            layout.show(container, "MENU");
        });

        MainMenu menu = new MainMenu();

        container.add(loading, "LOADING");
        container.add(menu, "MENU");

        add(container);

        // iniciar animación
        loading.startAnimation();
        layout.show(container, "LOADING");

        setTitle("Rico en inversiones");
        setIconImage();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void showScreen(String name) {
        cardLayout.show(mainPanel, name);
    }
}