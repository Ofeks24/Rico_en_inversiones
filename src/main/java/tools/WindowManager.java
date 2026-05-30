package tools;

import javax.swing.*;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;


/**
 * Gestor de ventanas internas ({@link JInternalFrame}) para el escritorio del juego.
 * <p>
 * Controla la apertura, el foco y el cierre de ventanas dentro de un
 * {@link JDesktopPane}, evitando duplicados: si se intenta abrir una ventana
 * ya existente, simplemente se le da el foco. Además, registra cada ventana
 * en el {@link TaskBarManager} para que aparezca en la barra de tareas.
 * </p>
 */
public class WindowManager {

    /** Escritorio Swing sobre el que se gestionan las ventanas internas. */
    private final JDesktopPane desktop;

    /** Desplazamiento acumulado en cascada para posicionar nuevas ventanas. */
    private int cascadeOffset = 0;

    /** Gestor de la barra de tareas al que se registran y eliminan las ventanas. */
    private final TaskBarManager taskBarManager;

    /** Mapa de id → ventana para controlar las ventanas abiertas. */
    private final Map<String, JInternalFrame> windows = new HashMap<>();

    /**
     * Construye un {@code WindowManager} asociado al escritorio y barra de tareas indicados.
     *
     * @param desktop        escritorio Swing donde se añaden las ventanas internas
     * @param taskBarManager gestor de la barra de tareas del sistema
     */
    public WindowManager(
            JDesktopPane desktop,
            TaskBarManager taskBarManager
    ) {

        this.desktop = desktop;
        this.taskBarManager = taskBarManager;
    }

    /**
     * Abre una ventana interna identificada por {@code id}.
     * <p>
     * Si la ventana ya está abierta, simplemente la trae al frente y le da el foco.
     * En caso contrario, invoca el {@code creator} para construirla, la posiciona
     * en cascada, la añade al escritorio y la registra en la barra de tareas.
     * Al cerrarse, se elimina automáticamente del mapa interno.
     * </p>
     *
     * @param id      identificador único de la ventana
     * @param icon    icono que se mostrará en la barra de tareas
     * @param creator proveedor que construye el {@link JInternalFrame} si no existe
     */
    public void openWindow(
            String id,
            ImageIcon icon,
            Supplier<JInternalFrame> creator
            
    ) {

        // si ya existe
        if (windows.containsKey(id)) {

            JInternalFrame existing = windows.get(id);

            // si sigue viva
            if (!existing.isClosed()) {

                focusWindow(existing);
                return;
            }

            // limpiar referencia rota
            windows.remove(id);
        }

        // crear nueva
        JInternalFrame frame = creator.get();
        
        frame.setLocation(
        	    50 + cascadeOffset,
        	    50 + cascadeOffset
        	);

        	cascadeOffset += 30;

        windows.put(id, frame);

        desktop.add(frame);
        taskBarManager.registerWindow(frame, icon);

        focusWindow(frame);

        // eliminar automáticamente al cerrar
        frame.addInternalFrameListener(new javax.swing.event.InternalFrameAdapter() {
            @Override
            public void internalFrameClosed(
                    javax.swing.event.InternalFrameEvent e
            ) {
                windows.remove(id);
            }
        });

        frame.setVisible(true);
    }

    /**
     * Trae al frente la ventana indicada, la restaura si estaba minimizada
     * y le otorga el foco.
     *
     * @param frame ventana interna a la que se dará el foco
     */
    public void focusWindow(JInternalFrame frame) {

        try {

            frame.setIcon(false);

            frame.moveToFront();

            frame.setSelected(true);

        } catch (PropertyVetoException ignored) {}
    }

    /**
     * Cierra y elimina la ventana registrada con el identificador dado.
     * Si no existe ninguna ventana con ese id, el método no hace nada.
     *
     * @param id identificador de la ventana a cerrar
     */
    public void closeWindow(String id) {

        JInternalFrame frame = windows.get(id);

        if (frame == null) return;

        frame.dispose();

        windows.remove(id);
    }

    /**
     * Indica si existe actualmente una ventana abierta con el identificador dado.
     *
     * @param id identificador de la ventana
     * @return {@code true} si la ventana está abierta; {@code false} en caso contrario
     */
    public boolean isOpen(String id) {
        return windows.containsKey(id);
    }

    /**
     * Devuelve la ventana interna asociada al identificador dado, o {@code null}
     * si no existe ninguna ventana abierta con ese id.
     *
     * @param id identificador de la ventana
     * @return el {@link JInternalFrame} correspondiente, o {@code null}
     */
    public JInternalFrame getWindow(String id) {
        return windows.get(id);
    }
}