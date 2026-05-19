package main.java.tools;
import javax.swing.*;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ScreenManager {

    private JPanel container;
    private CardLayout layout;
    private Map<String, JPanel> screens;
    

    public ScreenManager() {
        layout = new CardLayout();
        container = new JPanel(layout);
        screens = new HashMap<>();
    }

    public JPanel getContainer() {
        return container;
    }

    public void addScreen(String name, JPanel panel) {
        screens.put(name, panel);
        container.add(panel, name);
    }

    public void showScreen(String name) {

        JPanel screen = screens.get(name);

        if (screen == null) {
            throw new IllegalArgumentException("Pantalla no encontrada: " + name);
        }

        // onHide de todas
        JPanel current = null;
        for (Map.Entry<String, JPanel> entry : screens.entrySet()) {
            if (entry.getValue().isVisible()) {
                current = entry.getValue();
                break;
            }
        }

        if (current instanceof Screen) {
            ((Screen) current).onHide();
        }

        layout.show(container, name);

        // onShow de la actual
        if (screen instanceof Screen) {
            ((Screen) screen).onShow();
        }
        container.revalidate();
        container.repaint();
    }
}