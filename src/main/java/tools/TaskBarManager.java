package tools;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;


/**
 * Gestor de la barra de tareas del escritorio del juego.
 * <p>
 * Mantiene un panel horizontal de botones, uno por cada {@link JInternalFrame}
 * abierto. Cada botón muestra el título e icono de la ventana y permite
 * minimizarla, restaurarla o darle el foco con un solo clic, emulando el
 * comportamiento de la barra de tareas de Windows 95/98.
 * </p>
 */
public class TaskBarManager {

    /** Panel Swing que contiene los botones de la barra de tareas. */
    private final JPanel taskBarPanel;

    /** Mapa de ventana interna → botón de la barra de tareas. */
    private final Map<JInternalFrame, JButton> buttons = new HashMap<>();

    /**
     * Construye el gestor y configura el panel de la barra de tareas
     * con un {@link FlowLayout} alineado a la izquierda.
     *
     * @param taskBarPanel panel Swing donde se añadirán los botones de ventana
     */
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

    /**
     * Registra una ventana interna en la barra de tareas creando su botón asociado.
     * <p>
     * El botón se añade al panel y se configura para eliminarse automáticamente
     * cuando la ventana se cierre.
     * </p>
     *
     * @param frame ventana interna que se registra
     * @param icon  icono que se mostrará en el botón de la barra de tareas
     */
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

    /**
     * Crea y configura el botón de la barra de tareas para la ventana indicada.
     * <p>
     * El botón implementa la lógica de minimizar/restaurar/focus:
     * si la ventana está minimizada la restaura; si está activa la minimiza;
     * en cualquier otro caso le da el foco.
     * </p>
     *
     * @param frame ventana interna a la que representa el botón
     * @param icon  icono del botón
     * @return el {@link JButton} configurado y listo para añadir al panel
     */
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

    /**
     * Elimina el botón de la barra de tareas asociado a la ventana dada.
     * Si la ventana no tiene botón registrado, el método no hace nada.
     *
     * @param frame ventana cuyo botón debe eliminarse
     */
    public void removeWindow(JInternalFrame frame) {

        JButton button = buttons.remove(frame);

        if (button != null) {

            taskBarPanel.remove(button);

            taskBarPanel.revalidate();
            taskBarPanel.repaint();
        }
    }
}