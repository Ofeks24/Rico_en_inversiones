package tools;

import java.awt.Image;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Normalizer;

import javax.swing.ImageIcon;

public class Utils {

	
	public static Image escalarImagen(Image imagen, double factor) {
	    int w = (int)(imagen.getWidth(null) * factor);
	    int h = (int)(imagen.getHeight(null) * factor);

	    return imagen.getScaledInstance(w, h, Image.SCALE_SMOOTH);
	}
	
	public static ImageIcon escalarIcono(String ruta, int factor) {
		ImageIcon imagen= new ImageIcon(ruta);
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
	
	public static ImageIcon escalarIcono(String ruta, double scale) {
	    ImageIcon imagen = new ImageIcon(ruta);

	    int ancho = (int) (imagen.getIconWidth() * scale);
	    int alto = (int) (imagen.getIconHeight() * scale);

	    Image img = imagen.getImage().getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);

	    return new ImageIcon(img);
	}
	
	
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

	public static boolean esBisiesto(int anio) {
	    return (anio % 4 == 0 && anio % 100 != 0) || (anio % 400 == 0);
	}
	
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
	
	
	public static void conexion() {
		String ruta= "res/db/app.db";
		String url = "jdbc:sqlite:"+ruta;

	    // Conexión y consulta usando try-with-resources para cerrar recursos automáticamente
	    try (Connection conn = DriverManager.getConnection(url)) {
	        if (conn != null) {
	            System.out.println("Conexión exitosa a la base de datos.");
	            
	            String sql = "SELECT * FROM Empresas"; // Cambia por tu consulta
	            try (Statement stmt = conn.createStatement();
	                 ResultSet rs = stmt.executeQuery(sql)) {
	                
	                while (rs.next()) {
	                    // Extrae datos por nombre de columna o índice
	                    System.out.println("Columna 1: " + rs.getString(1));
	                }
	            }
	        }
	    } catch (SQLException e) {
	        System.out.println("Error de conexión: " + e.getMessage());
	    }
	}
}
