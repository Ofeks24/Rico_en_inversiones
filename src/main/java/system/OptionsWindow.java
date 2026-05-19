package main.java.system;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import main.java.tools.RoundedPanel;
import main.java.tools.Screen;

public class OptionsWindow extends JPanel implements Screen {

    private Runnable onBack;

    public OptionsWindow(Runnable onBack) {
    	setOpaque(false);

        this.onBack = onBack;

        setLayout(new GridBagLayout());

        RoundedPanel overlay = new RoundedPanel(30);
        overlay.setPreferredSize(new Dimension(500, 400));
        overlay.setLayout(new BorderLayout());

        JButton volver = new JButton("Volver");
        volver.addActionListener(e -> {
            if (this.onBack != null) this.onBack.run();
        });

        overlay.add(volver, BorderLayout.SOUTH);

        add(overlay);
    }

    @Override
    public void onShow() {}

    @Override
    public void onHide() {}

}