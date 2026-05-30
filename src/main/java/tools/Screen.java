package tools;


/**
 * Interfaz que deben implementar todos los paneles que actúan como
 * pantallas gestionadas por {@link ScreenManager}.
 * <p>
 * Permite al gestor notificar a cada pantalla cuándo pasa a ser visible
 * y cuándo deja de estarlo, de forma que cada una pueda arrancar o
 * detener sus recursos (música, temporizadores, animaciones, etc.).
 * </p>
 */
public interface Screen {

	/**
     * Llamado por {@link ScreenManager} justo después de mostrar esta pantalla.
     * <p>
     * Úsalo para iniciar música, animaciones o cualquier recurso que sólo
     * deba estar activo mientras la pantalla es visible.
     * </p>
     */
	void onShow();

	/**
     * Llamado por {@link ScreenManager} justo antes de ocultar esta pantalla.
     * <p>
     * Úsalo para detener temporizadores, pausar música o liberar recursos
     * que no deban seguir consumiendo CPU en segundo plano.
     * </p>
     */
	void onHide();
}
