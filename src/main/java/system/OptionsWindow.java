package system;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import tools.AudioManager;
import tools.RoundedPanel;
import tools.Screen;

public class OptionsWindow extends JPanel implements Screen {

    private Runnable onBack;

    public OptionsWindow(Runnable onBack) {
    	setOpaque(false);

        this.onBack = onBack;

        setLayout(new GridBagLayout());

        RoundedPanel overlay = new RoundedPanel(30);
        overlay.setPreferredSize(new Dimension(500, 400));
        overlay.setLayout(new BorderLayout());
        
        JCheckBox musicCheck = new JCheckBox("Música", true);
        musicCheck.addActionListener(e ->
            AudioManager.getInstance().setMusicEnabled(musicCheck.isSelected())
        );
        overlay.add(musicCheck, BorderLayout.EAST);

        JCheckBox sfxCheck = new JCheckBox("Efectos", true);
        sfxCheck.addActionListener(e ->
            AudioManager.getInstance().setSfxEnabled(sfxCheck.isSelected())
        );
        overlay.add(sfxCheck, BorderLayout.EAST);
        
        
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