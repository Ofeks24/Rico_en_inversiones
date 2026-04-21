package windows;

import java.awt.Color;


import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameWindow extends JFrame {
	ImageIcon startIcon = new ImageIcon("res/logos/Doors(Closed).png");
	JButton b1;
	public GameWindow() {
		setTitle("Main");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(0, 130, 130));
        setContentPane(panel);
        
        b1=crearBoton(startIcon,100,100);
        b1.setRolloverIcon(new ImageIcon("res/logos/Doors(Open).png")); // al pasar el mouse
        b1.setPressedIcon(new ImageIcon("res/logos/Doors(Closed).png"));  // al pulsar
        b1.setBorderPainted(false);
        b1.setContentAreaFilled(false);
        b1.setFocusPainted(false);
        b1.setOpaque(false);
        
	}
	
	private JButton crearBoton(String texto, int x, int y) {

        JButton b = new JButton(texto);
        b.setBounds(x, y, 250, 60);
        b.setFocusPainted(false);

        if(texto.equals("Salir"))
            b.addActionListener(e -> System.exit(0));

        return b;
    }
	private JButton crearBoton(ImageIcon imagen, int x, int y) {

        JButton b = new JButton(imagen);
        b.setBounds(x, y, 250, 60);
        b.setFocusPainted(false);

        //if(texto.equals("Salir"))
        b.addActionListener(e -> System.exit(0));

        return b;
    }
}
