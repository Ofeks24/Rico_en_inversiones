package tools;

import java.awt.*;

public class DesktopGridLayout implements LayoutManager {

    private int cellWidth = 90;
    private int cellHeight = 90;
    private int hGap = 20;
    private int vGap = 20;
    private int margin = 20;

    @Override
    public void addLayoutComponent(String name, Component comp) {}

    @Override
    public void removeLayoutComponent(Component comp) {}

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        return parent.getSize();
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return parent.getSize();
    }

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