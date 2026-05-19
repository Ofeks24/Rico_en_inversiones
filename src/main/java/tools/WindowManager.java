package main.java.tools;

import javax.swing.*;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class WindowManager {

    private final JDesktopPane desktop;
    private int cascadeOffset = 0;
    private final TaskBarManager taskBarManager;

    // ventanas abiertas
    private final Map<String, JInternalFrame> windows = new HashMap<>();

    public WindowManager(
            JDesktopPane desktop,
            TaskBarManager taskBarManager
    ) {

        this.desktop = desktop;
        this.taskBarManager = taskBarManager;
    }

    // =====================================================
    // ABRIR VENTANA
    // =====================================================

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

    // =====================================================
    // FOCUS
    // =====================================================

    public void focusWindow(JInternalFrame frame) {

        try {

            frame.setIcon(false);

            frame.moveToFront();

            frame.setSelected(true);

        } catch (PropertyVetoException ignored) {}
    }

    // =====================================================
    // CERRAR
    // =====================================================

    public void closeWindow(String id) {

        JInternalFrame frame = windows.get(id);

        if (frame == null) return;

        frame.dispose();

        windows.remove(id);
    }

    // =====================================================
    // GETTERS
    // =====================================================

    public boolean isOpen(String id) {
        return windows.containsKey(id);
    }

    public JInternalFrame getWindow(String id) {
        return windows.get(id);
    }
}