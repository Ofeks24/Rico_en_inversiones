package tools;


/**
 * Enumerado que representa los sectores económicos del juego.
 * <p>
 * Cada empresa pertenece a un sector concreto, lo que determina qué
 * noticias la afectan (ver {@link tools.NewsTemplate}) y qué imagen
 * ilustra sus artículos en el periódico (ver {@link system.News.NewsWindow}).
 * El valor {@code GLOBAL} indica que una noticia o efecto afecta a todas
 * las empresas sin distinción de sector.
 * </p>
 */
public enum Sector {
    /** Empresas navieras y de astilleros. */
	NAVAL,        // Naviera & Astilleros Dorado
    /** Talleres e industria mecánica. */
    INDUSTRIA,    // Talleres Mécanique Royale
    /** Compañías de botánica, especias y tinturas. */
    BOTANICA,     // Compañía Botánica del Atlante
    /** Empresas agrícolas, molinos y cereales. */
    AGRICULTURA,  // Molinos Reunidos del Valle
    /** Fábricas de manufactura artesanal (velas, jabones, etc.). */
    MANUFACTURA,  // Fábrica de Velas & Jabones Santa Águeda
    /**
     * Sector transversal: las noticias con este sector afectan a todas
     * las empresas del mercado de forma simultánea.
     */
    GLOBAL;
	
    /**
     * Convierte una cadena de texto al {@code Sector} correspondiente de
     * forma tolerante a errores.
     * <p>
     * La conversión ignora mayúsculas/minúsculas y espacios en los extremos.
     * Si el valor no coincide con ningún sector conocido, o si es {@code null}
     * o está en blanco, devuelve {@link #GLOBAL} como valor de reserva y
     * escribe un aviso en {@code System.err}.
     * </p>
     *
     * @param value cadena leída de la base de datos u otra fuente externa
     * @return el {@code Sector} correspondiente, o {@code GLOBAL} si no se reconoce
     */
	public static Sector fromString(String value) {
        if (value == null || value.isBlank()) return GLOBAL;
        try {
            return Sector.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("[Sector] Valor desconocido en BD: '"
                    + value + "' → usando GLOBAL como fallback");
            return GLOBAL;
        }
    }
}

