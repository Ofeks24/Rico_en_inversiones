package tools;


/**
 * Modelo de datos que representa una empresa cotizada en el mercado del juego.
 * <p>
 * Almacena tanto la información descriptiva de la empresa (nombre, actividad,
 * marca y sector) como su estado bursátil actual (precio de la acción,
 * acciones en el mercado y acciones en propiedad del jugador).
 * Es la entidad central que circula entre {@link tools.MarketService},
 * {@link tools.NewsGenerator} y los paneles de estadísticas.
 * </p>
 */
public class CompanyData {
	
	/** Identificador único de la empresa dentro del sistema. */
	int id;

	/** Nombre completo o razón social de la empresa. */
    String nombre;

	/** Descripción breve de la actividad económica de la empresa. */
    String actividad;

	/** Número total de acciones actualmente disponibles en el mercado. */
    int accionesMercado;

	/** Número de acciones que posee actualmente el jugador. */
    int accionesPropiedad;

	/** Valor actual de una acción de la empresa. */
    double valorAccion;

	/** Marca comercial o nombre abreviado visible para el jugador. */
    String marca;

	/**
     * Sector económico al que pertenece la empresa.
     *
     * @see tools.Sector
     */
    private Sector sector;

	/**
	 * Construye una empresa con todos sus datos iniciales.
	 *
	 * @param id                 identificador único de la empresa
	 * @param nombre             nombre completo de la empresa
	 * @param actividad          descripción breve de su actividad económica
	 * @param accionesMercado    número de acciones disponibles en el mercado
	 * @param accionesPropiedad  número de acciones en posesión del jugador
	 * @param valorAccion        precio inicial de cada acción
	 * @param marca              nombre comercial o marca de la empresa
	 * @param sector             sector económico al que pertenece
	 */
    public CompanyData(
    		int id,
            String nombre,
            String actividad,
            int accionesMercado,
            int accionesPropiedad,
            double valorAccion,
            String marca,
            Sector sector
    ) {
    	this.id = id;
        this.nombre = nombre;
        this.actividad = actividad;
        this.accionesMercado = accionesMercado;
        this.accionesPropiedad = accionesPropiedad;
        this.valorAccion = valorAccion;
        this.marca = marca;
        this.sector = sector;
    }
    
	/**
     * Devuelve el sector económico de la empresa.
     *
     * @return sector al que pertenece la empresa
     */
    public Sector getSector() { 
    	return sector; 
    }

	/**
     * Devuelve la marca comercial de la empresa.
     *
     * @return marca visible de la empresa
     */
	public String getMarca() {
		return marca;
	}

	/**
     * Modifica la marca comercial de la empresa.
     *
     * @param marca nueva marca comercial
     */
	public void setMarca(String marca) {
		this.marca = marca;
	}

	/**
     * Devuelve el identificador único de la empresa.
     *
     * @return identificador interno
     */
	public int getId() {
		return id;
	}

	/**
     * Modifica el identificador de la empresa.
     *
     * @param id nuevo identificador
     */
	public void setId(int id) {
		this.id = id;
	}

	/**
     * Actualiza el valor actual de la acción.
     *
     * @param valorAccion nuevo precio por acción
     */
	public void setValorAccion(double valorAccion) {
		this.valorAccion = valorAccion;
	}

	/**
     * Devuelve el nombre completo de la empresa.
     *
     * @return nombre oficial de la empresa
     */
	public String getNombre() {
		return nombre;
	}

	/**
     * Modifica el nombre de la empresa.
     *
     * @param nombre nuevo nombre oficial
     */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
     * Devuelve la descripción de la actividad económica de la empresa.
     *
     * @return actividad principal de la empresa
     */
	public String getActividad() {
		return actividad;
	}

	/**
     * Modifica la descripción de la actividad de la empresa.
     *
     * @param actividad nueva descripción de actividad
     */
	public void setActividad(String actividad) {
		this.actividad = actividad;
	}

	/**
     * Devuelve el número de acciones disponibles en el mercado.
     *
     * @return acciones actualmente en circulación
     */
	public int getAccionesMercado() {
		return accionesMercado;
	}

	/**
     * Modifica el número de acciones disponibles en el mercado.
     *
     * @param accionesMercado nueva cantidad de acciones
     */
	public void setAccionesMercado(int accionesMercado) {
		this.accionesMercado = accionesMercado;
	}

	/**
     * Devuelve el número de acciones que posee el jugador.
     *
     * @return acciones propiedad del jugador
     */
	public int getAccionesPropiedad() {
		return accionesPropiedad;
	}

	/**
     * Modifica el número de acciones propiedad del jugador.
     *
     * @param accionesPropiedad nueva cantidad de acciones poseídas
     */
	public void setAccionesPropiedad(int accionesPropiedad) {
		this.accionesPropiedad = accionesPropiedad;
	}
	
	/**
     * Devuelve el valor actual de una acción de la empresa.
     *
     * @return precio actual por acción
     */
	public double getValorAccion() {
		return valorAccion;
	}

	/**
     * Modifica el valor actual de la acción.
     *
     * @param valorAccion nuevo valor por acción
     */
	public void setValorAccion(int valorAccion) {
		this.valorAccion = valorAccion;
	}
	
	/**
     * Devuelve la representación textual de la empresa.
     * <p>
     * Actualmente devuelve únicamente la marca comercial, lo que permite
     * mostrar objetos {@code CompanyData} directamente en listas,
     * combos y otros componentes Swing.
     * </p>
     *
     * @return marca comercial de la empresa
     */
	@Override
	public String toString() {
	    return marca;
	}
}