package tools;

import java.awt.Image;
import java.text.Normalizer;

import javax.swing.ImageIcon;


/**
 * Clase de utilidades estáticas de propósito general para el proyecto.
 * <p>
 * Agrupa métodos de apoyo sin estado que no pertenecen a ninguna entidad
 * de dominio concreta. Cubre tres áreas:
 * </p>
 * <ul>
 *   <li><b>Imágenes:</b> escalado de {@link java.awt.Image} e
 *       {@link javax.swing.ImageIcon} con suavizado.</li>
 *   <li><b>Calendario:</b> cálculo de días por mes y detección de años
 *       bisiestos.</li>
 *   <li><b>Cadenas:</b> conversión de texto a formato identificador
 *       compatible con bases de datos o rutas.</li>
 * </ul>
 */
public class Utils {

	/**
	 * Escala una imagen por un factor multiplicador.
	 *
	 * @param imagen  imagen origen a escalar
	 * @param factor  factor de escala (p. ej. {@code 0.5} reduce a la mitad)
	 * @return nueva imagen escalada con suavizado {@link java.awt.Image#SCALE_SMOOTH}
	 */
	public static Image escalarImagen(Image imagen, double factor) {
	    int w = (int)(imagen.getWidth(null) * factor);
	    int h = (int)(imagen.getHeight(null) * factor);

	    return imagen.getScaledInstance(w, h, Image.SCALE_SMOOTH);
	}
	
	/**
	 * Carga un icono desde el classpath y lo escala al ancho indicado,
	 * manteniendo la proporción original alto/ancho.
	 *
	 * @param ruta   ruta del recurso de imagen en el classpath
	 * @param factor ancho objetivo en píxeles
	 * @return {@link ImageIcon} escalado proporcionalmente al ancho indicado
	 * @throws RuntimeException si el recurso no se encuentra en el classpath
	 */
	public static ImageIcon escalarIcono(String ruta, int factor) {
		ImageIcon imagen= icon(ruta);
		int anchoOriginal = imagen.getIconWidth();
	    int altoOriginal = imagen.getIconHeight();

	    int nuevoAlto = (altoOriginal * factor) / anchoOriginal;

	    Image imagenEscalada = imagen.getImage().getScaledInstance(
	        factor,
	        nuevoAlto,
	        Image.SCALE_SMOOTH
	    );

	    return new ImageIcon(imagenEscalada);
	}
	
	/**
	 * Carga un icono desde el classpath y lo escala por un factor decimal,
	 * manteniendo la proporción original.
	 *
	 * @param ruta  ruta del recurso de imagen en el classpath
	 * @param scale factor de escala (p. ej. {@code 0.75} reduce un 25 %)
	 * @return {@link ImageIcon} escalado por el factor indicado
	 */
	public static ImageIcon escalarIcono(String ruta, double scale) {
	    ImageIcon imagen = new ImageIcon(ruta);

	    int ancho = (int) (imagen.getIconWidth() * scale);
	    int alto = (int) (imagen.getIconHeight() * scale);

	    Image img = imagen.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);

	    return new ImageIcon(img);
	}
	
	/**
	 * Devuelve el número de días del mes indicado para un año concreto,
	 * teniendo en cuenta los años bisiestos en febrero.
	 *
	 * @param mes  mes del año (1 = enero … 12 = diciembre)
	 * @param anio año para evaluar si febrero tiene 28 o 29 días
	 * @return número de días del mes (28, 29, 30 o 31)
	 * @throws IllegalArgumentException si {@code mes} no está entre 1 y 12
	 */
	public static int diasDelMes(int mes, int anio) {
	    switch (mes) {
	        case 1: case 3: case 5: case 7: case 8: case 10: case 12:
	            return 31;
	        case 4: case 6: case 9: case 11:
	            return 30;
	        case 2:
	            return esBisiesto(anio) ? 29 : 28;
	        default:
	            throw new IllegalArgumentException("Mes inválido. Debe estar entre 1 y 12.");
	    }
	}

	/**
	 * Determina si un año es bisiesto según el calendario gregoriano.
	 * <p>
	 * Un año es bisiesto si es divisible por 4, excepto los centenarios,
	 * que solo lo son si además son divisibles por 400.
	 * </p>
	 *
	 * @param anio año a evaluar
	 * @return {@code true} si el año es bisiesto; {@code false} en caso contrario
	 */
	public static boolean esBisiesto(int anio) {
	    return (anio % 4 == 0 && anio % 100 != 0) || (anio % 400 == 0);
	}
	
	/**
	 * Convierte un texto arbitrario en un identificador en minúsculas apto
	 * para bases de datos o rutas de recursos.
	 * <p>
	 * La transformación aplica, en orden: eliminación de tildes y diacríticos,
	 * conversión a minúsculas, sustitución de espacios por guiones bajos ({@code _})
	 * y eliminación de cualquier carácter que no sea letra, dígito o guion bajo.
	 * </p>
	 *
	 * @param texto cadena de origen; si es {@code null} o está en blanco devuelve {@code ""}
	 * @return identificador normalizado sin tildes, espacios ni caracteres especiales
	 */
	public static String convertirEnId(String texto) {
        if (texto == null || texto.isBlank()) {
            return "";
        }

        // Eliminar tildes
        String normalizado = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // Minúsculas
        normalizado = normalizado.toLowerCase();

        // Reemplazar espacios por _
        normalizado = normalizado.replaceAll("\\s+", "_");

        // Eliminar caracteres no válidos
        normalizado = normalizado.replaceAll("[^a-z0-9_]", "");

        return normalizado;
    }
	
	/**
	 * Carga un recurso de imagen desde el classpath y lo devuelve como
	 * {@link ImageIcon}.
	 *
	 * @param ruta ruta del recurso en el classpath
	 *             (ej. {@code "/main/resources/icons/empresa.png"})
	 * @return {@link ImageIcon} listo para usar en componentes Swing
	 * @throws RuntimeException si el recurso no existe en el classpath
	 */
	public static ImageIcon icon(
	        String ruta
	) {

	    java.net.URL resource =
	            Utils.class.getResource(ruta);

	    if (resource == null) {

	        throw new RuntimeException(
	                "Recurso no encontrado: "
	                + ruta
	        );
	    }

	    return new ImageIcon(resource);
	}
}
