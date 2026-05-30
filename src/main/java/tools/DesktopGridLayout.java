package tools;

import java.awt.*;


/**
 * Layout personalizado que organiza los iconos del escritorio en columnas
 * verticales, de arriba hacia abajo y de izquierda a derecha.
 * <p>
 * Divide el ancho disponible del contenedor en un número fijo de columnas
 * ({@code cols = 20}) y coloca cada componente en una celda de tamaño
 * uniforme. Cuando los componentes ya no caben verticalmente en la columna
 * actual, se avanza a la siguiente columna, imitando el comportamiento
 * del escritorio de Windows 95/98.
 * </p>
 */
public class DesktopGridLayout implements LayoutManager {

    /** Ancho de cada celda del grid en píxeles (recalculado en cada layout). */
    private int cellWidth = 90;

    /** Alto de cada celda del grid en píxeles. */
    private int cellHeight = 110;

    /** Espacio horizontal entre columnas en píxeles. */
    private int hGap = 20;

    /** Espacio vertical entre filas en píxeles. */
    private int vGap = 20;

    /** Margen exterior del grid respecto al borde del contenedor en píxeles. */
    private int margin = 10;

    /**
     * Requerido por {@link LayoutManager}; no se utiliza en esta implementación.
     */
    @Override
    public void addLayoutComponent(String name, Component comp) {}

    /**
     * Requerido por {@link LayoutManager}; no se utiliza en esta implementación.
     */
    @Override
    public void removeLayoutComponent(Component comp) {}

    /**
     * Devuelve el tamaño preferido del contenedor, equivalente a su tamaño actual.
     *
     * @param parent el contenedor que se va a distribuir
     * @return dimensión preferida igual al tamaño actual del contenedor
     */
    @Override
    public Dimension preferredLayoutSize(Container parent) {
        return parent.getSize();
    }

    /**
     * Devuelve el tamaño mínimo del contenedor, equivalente a su tamaño actual.
     *
     * @param parent el contenedor que se va a distribuir
     * @return dimensión mínima igual al tamaño actual del contenedor
     */
    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return parent.getSize();
    }

    /**
     * Distribuye los componentes del contenedor en columnas verticales.
     * <p>
     * El ancho de cada celda se recalcula dividiendo el ancho del contenedor
     * entre 20 columnas fijas. Los componentes se colocan de arriba a abajo;
     * cuando uno se saldría del borde inferior, se inicia una nueva columna
     * a la derecha.
     * </p>
     *
     * @param parent el contenedor cuyos hijos se van a posicionar
     */
    @Override
    public void layoutContainer(Container parent) {
    	int cols = 20;
    	cellWidth = (parent.getWidth() - margin * 2) / cols;

        int height = parent.getHeight();

        int x = margin;
        int y = margin;

        for (Component comp : parent.getComponents()) {

            comp.setBounds(x, y, cellWidth, cellHeight);

            y += cellHeight + vGap;

            // si se sale verticalmente → nueva columna
            if (y + cellHeight > height) {
                y = margin;
                x += cellWidth + hGap;
            }
        }
    }
}