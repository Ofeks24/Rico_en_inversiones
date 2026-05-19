package main.java.tools;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class RoundedPanel extends JPanel {

    private int radius;

    public RoundedPanel(int radius) {
        this.radius = radius;
        setOpaque(false); // IMPORTANTE
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        // color oscuro semitransparente
        g2.setColor(new Color(0, 0, 0, 180));

        g2.fillRoundRect(
            0, 0,
            getWidth(),
            getHeight(),
            radius,
            radius
        );

        g2.dispose();

        super.paintComponent(g);
    }
}