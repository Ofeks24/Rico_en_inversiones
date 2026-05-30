package tools;
import javax.swing.*;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;


/**
 * Gestor de pantallas principal de la aplicación, basado en {@link CardLayout}.
 * <p>
 * Mantiene un registro de todos los paneles registrados como pantallas y
 * controla cuál está visible en cada momento. Al cambiar de pantalla, notifica
 * a la anterior mediante {@link Screen#onHide()} y a la nueva mediante
 * {@link Screen#onShow()}, siempre que implementen la interfaz {@link Screen}.
 * </p>
 */
public class ScreenManager {

    /** Panel contenedor raíz que alberga todas las pantallas bajo un {@link CardLayout}. */
    private JPanel container;

    /** Layout de tarjetas que gestiona la visibilidad de las pantallas. */
    private CardLayout layout;

    /** Mapa de nombre → panel para localizar pantallas por identificador. */
    private Map<String, JPanel> screens;
    
    /**
     * Construye un nuevo {@code ScreenManager} con un contenedor vacío
     * y un {@link CardLayout} listo para registrar pantallas.
     */
    public ScreenManager() {
        layout = new CardLayout();
        container = new JPanel(layout);
        screens = new HashMap<>();
    }

    /**
     * Devuelve el panel contenedor raíz que debe añadirse a la ventana principal.
     *
     * @return el {@link JPanel} con {@link CardLayout} que alberga todas las pantallas
     */
    public JPanel getContainer() {
        return container;
    }

    /**
     * Registra una pantalla con el nombre indicado y la añade al contenedor.
     * <p>
     * Si ya existe una pantalla con el mismo nombre, será reemplazada en el mapa,
     * aunque el panel anterior permanecerá en el {@link CardLayout}.
     * </p>
     *
     * @param name  identificador único de la pantalla
     * @param panel panel Swing que representa la pantalla
     */
    public void addScreen(String name, JPanel panel) {
        screens.put(name, panel);
        container.add(panel, name);
    }

    /**
     * Muestra la pantalla registrada bajo el nombre indicado.
     * <p>
     * Antes de cambiar la vista, llama a {@link Screen#onHide()} sobre la
     * pantalla actualmente visible (si implementa {@link Screen}). Después,
     * llama a {@link Screen#onShow()} sobre la pantalla entrante.
     * </p>
     *
     * @param name identificador de la pantalla a mostrar
     * @throws IllegalArgumentException si no existe ninguna pantalla con ese nombre
     */
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