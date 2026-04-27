package tools;

import java.awt.Image;

import javax.swing.ImageIcon;

public class Utils {

	
	public Image escalarImagen(Image imagen, double factor) {
		
		//return imagen.getScaledInstance((int)(imagen.),(int)factor,(int)factor);
		return null;
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
}
