package tools;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ButtonSoundHelper {

    // =====================================================
    // HOVER SOUND
    // =====================================================

    public static void addHoverSound(

            JButton button,

            String soundPath
    ) {

        button.addMouseListener(
                new MouseAdapter() {

            @Override
            public void mouseEntered(
                    MouseEvent e
            ) {

                AudioManager
                        .getInstance()
                        .playSfx(soundPath);
            }
        });
    }
}