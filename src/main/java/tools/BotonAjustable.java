package tools;

import javax.swing.JButton;


/**
 * Contenedor que asocia un {@link JButton} a un retardo de animación en milisegundos.
 * <p>
 * Se utiliza en {@link system.MainMenu} para implementar la animación
 * de entrada en cascada de los botones del menú principal: cada botón tiene
 * un {@code delay} diferente que determina cuánto tiempo espera antes de
 * deslizarse hacia su posición final.
 * </p>
 */
public class BotonAjustable{
	/** El botón Swing asociado. */
	JButton boton;
	/** Retardo en milisegundos antes de iniciar la animación de este botón. */
	int delay;
	
	/**
     * Construye un {@code BotonAjustable} con el botón y el retardo indicados.
     *
     * @param boton el {@link JButton} que se animará
     * @param delay retardo de animación en milisegundos
     */
	public BotonAjustable(JButton boton, int delay){
		super();
		this.boton=boton;
		this.delay=delay;
	}

	/**
     * Devuelve el botón Swing asociado.
     *
     * @return el {@link JButton}
     */
	public JButton getBoton() {
		return boton;
	}

	/**
     * Sustituye el botón Swing asociado.
     *
     * @param boton el nuevo {@link JButton}
     */
	public void setBoton(JButton boton) {
		this.boton = boton;
	}

	/**
	 * Devuelve el retardo de animación en milisegundos.
	 *
	 * @return el retardo de animación
	 */
	public int getDelay() {
		return delay;
	}

	/**
     * Establece el retardo de animación en milisegundos.
     *
     * @param delay nuevo retardo en ms
     */
	public void setDelay(int delay) {
		this.delay = delay;
	}

	/**
     * Devuelve una representación textual del objeto con el botón y el retardo.
     *
     * @return cadena descriptiva
     */
	@Override
	public String toString() {
		return "BotonAjustable [boton=" + boton + ", delay=" + delay + "]";
	}

}
