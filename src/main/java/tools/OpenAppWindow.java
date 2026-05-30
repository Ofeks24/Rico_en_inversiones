package tools;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;


/**
 * Ventana interna ({@link JInternalFrame}) de estilo escritorio Windows 95/98
 * que actúa como contenedor genérico para cualquier panel de la aplicación.
 * <p>
 * Es creada por {@link tools.WindowManager} cada vez que el jugador abre
 * una aplicación desde el escritorio (Robbin Hub, Telégrafo, Stats.U…).
 * Soporta redimensionado, arrastre, maximizado y cierre.
 * </p>
 */
public class OpenAppWindow extends JInternalFrame {

	/**
     * Construye y muestra la ventana interna con el contenido indicado.
     *
     * @param title   título que se muestra en la barra de la ventana
     * @param content panel Swing que se añade como contenido principal
     * @param width   anchura inicial de la ventana en píxeles
     * @param height  altura inicial de la ventana en píxeles
     * @param icon    icono pequeño que aparece en la barra de título
     *                y en el botón de la barra de tareas
     */
	public OpenAppWindow(
	        String title,
	        JPanel content,
	        int width,
	        int height,
	        ImageIcon icon
	) {

	    super(title, true, true, true, true);

	    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

	    setSize(width, height);
	    setLayout(new BorderLayout());

	    getContentPane().setBackground(new Color(192,192,192));

	    add(content, BorderLayout.CENTER);
	    setFrameIcon(icon);
	    setVisible(true);
	}
}