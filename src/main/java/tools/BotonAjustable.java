package tools;

import javax.swing.JButton;

public class BotonAjustable{
	JButton boton;
	int delay;
	
	public BotonAjustable(JButton boton, int delay){
		super();
		this.boton=boton;
		this.delay=delay;
	}

	public JButton getBoton() {
		return boton;
	}

	public void setBoton(JButton boton) {
		this.boton = boton;
	}

	public int getDelay() {
		return delay;
	}

	public void setDelay(int delay) {
		this.delay = delay;
	}

	@Override
	public String toString() {
		return "BotonAjustable [boton=" + boton + ", delay=" + delay + "]";
	}

}
