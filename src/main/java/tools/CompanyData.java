package main.java.tools;

public class CompanyData {

    String nombre;
    String actividad;
    int accionesMercado;
    int accionesPropiedad;
    double valorAccion;

    public CompanyData(
            String nombre,
            String actividad,
            int accionesMercado,
            int accionesPropiedad,
            double valorAccion
    ) {
        this.nombre = nombre;
        this.actividad = actividad;
        this.accionesMercado = accionesMercado;
        this.accionesPropiedad = accionesPropiedad;
        this.valorAccion = valorAccion;
    }

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getActividad() {
		return actividad;
	}

	public void setActividad(String actividad) {
		this.actividad = actividad;
	}

	public int getAccionesMercado() {
		return accionesMercado;
	}

	public void setAccionesMercado(int accionesMercado) {
		this.accionesMercado = accionesMercado;
	}

	public int getAccionesPropiedad() {
		return accionesPropiedad;
	}

	public void setAccionesPropiedad(int accionesPropiedad) {
		this.accionesPropiedad = accionesPropiedad;
	}
	
	public double getValorAccion() {
		return valorAccion;
	}
	
	public void setValorAccion(int valorAccion) {
		this.valorAccion = valorAccion;
	}
	
	@Override
	public String toString() {
	    return nombre;
	}
}