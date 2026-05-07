package tools;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class OpenAppWindow extends JInternalFrame {

	public OpenAppWindow(
	        String title,
	        JPanel content,
	        int width,
	        int height,
	        ImageIcon icon
	) {

	    super(title, true, true, true, true);

	    setDefaultCloseOperation(DISPOSE_ON_CLOSE);

	    setSize(width, height);
	    setLayout(new BorderLayout());

	    getContentPane().setBackground(new Color(192,192,192));

	    add(content, BorderLayout.CENTER);
	    setFrameIcon(icon);
	    setVisible(true);
	}
}