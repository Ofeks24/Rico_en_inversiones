package main.java.tools;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;

public class TaskBarManager {

    private final JPanel taskBarPanel;

    // ventana -> botón
    private final Map<JInternalFrame, JButton> buttons = new HashMap<>();

    public TaskBarManager(JPanel taskBarPanel) {

        this.taskBarPanel = taskBarPanel;

        taskBarPanel.setLayout(
            new FlowLayout(
                FlowLayout.LEFT,
                4,
                4
            )
        );
    }

    // =====================================================
    // AÑADIR VENTANA
    // =====================================================

    public void registerWindow(JInternalFrame frame, ImageIcon icon) {

        JButton button = createTaskButton(frame, icon);

        buttons.put(frame, button);

        taskBarPanel.add(button);

        taskBarPanel.revalidate();
        taskBarPanel.repaint();

        // eliminar automáticamente al cerrar
        frame.addInternalFrameListener(
            new javax.swing.event.InternalFrameAdapter() {

                @Override
                public void internalFrameClosed(
                        javax.swing.event.InternalFrameEvent e
                ) {

                    removeWindow(frame);
                }
            }
        );
    }

    // =====================================================
    // CREAR BOTÓN
    // =====================================================
    private JButton createTaskButton(JInternalFrame frame, ImageIcon icon) {

        JButton button = new JButton(frame.getTitle(), icon);

        button.setFocusPainted(false);

        button.setPreferredSize(
            new Dimension(140, 30)
        );

        // estilo Win95
        button.setBackground(
            new Color(195,199,200)
        );

        button.addActionListener(e -> {

            try {

                // si está minimizada → restaurar
                if (frame.isIcon()) {

                    frame.setIcon(false);

                    frame.setSelected(true);

                    frame.moveToFront();

                    return;
                }

                // si está activa → minimizar
                if (frame.isSelected()) {

                    frame.setIcon(true);

                    return;
                }

                // focus normal
                frame.setSelected(true);

                frame.moveToFront();

            } catch (PropertyVetoException ignored) {}
        });

        return button;
    }

    // =====================================================
    // ELIMINAR
    // =====================================================

    public void removeWindow(JInternalFrame frame) {

        JButton button = buttons.remove(frame);

        if (button != null) {

            taskBarPanel.remove(button);

            taskBarPanel.revalidate();
            taskBarPanel.repaint();
        }
    }
}