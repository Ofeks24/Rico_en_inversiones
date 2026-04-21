package windows;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import tools.BotonAjustable;

public class MainMenu extends JFrame {

    JLabel logo;

    BotonAjustable[] botones= {new BotonAjustable(new JButton(),500),
    		new BotonAjustable(new JButton(),1000),
    		new BotonAjustable(new JButton(),1500)};
    
    Timer timer;
    
    double speed = 1200;

    long lastTime;

    
    


    public MainMenu() {


        setTitle("Menu");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(30,30,30));
        setContentPane(panel);

        // LOGO
        logo = new JLabel(new ImageIcon("res/logos/Rico en inversiones_logo.png"));
        logo.setFont(new Font("Arial", Font.BOLD, 40));
        logo.setForeground(Color.WHITE);
        logo.setBounds(700, -1000, new ImageIcon("res/logos/Rico en inversiones_logo.png").getIconWidth(), new ImageIcon("res/logos/Rico en inversiones_logo.png").getIconHeight()); // empieza arriba
        panel.add(logo);

        // BOTONES
        botones[0].setBoton(crearBoton("Empezar", -300, 300));
        botones[1].setBoton(crearBoton("Opciones", -300, 400));
        botones[2].setBoton(crearBoton("Salir", -300, 500));

        panel.add(botones[0].getBoton());
        panel.add(botones[1].getBoton());
        panel.add(botones[2].getBoton());


        setVisible(true);
    }

    private JButton crearBoton(String texto, int x, int y) {

        JButton b = new JButton(texto);
        b.setBounds(x, y, 454, 75);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        
        if(texto.equals("Empezar")) {
        	b.setIcon(new ImageIcon("res/sprites/Empezar(Normal).png"));
        	b.setRolloverIcon(new ImageIcon("res/sprites/Empezar(Hover).png"));
        	b.setPressedIcon(new ImageIcon("res/sprites/Empezar(Click).png"));
        }
        
        if(texto.equals("Opciones")) {
        	b.setIcon(new ImageIcon("res/sprites/Opciones(Normal).png"));
        	b.setRolloverIcon(new ImageIcon("res/sprites/Opciones(Hover).png"));
        	b.setPressedIcon(new ImageIcon("res/sprites/Opciones(Click).png"));
        }
        if(texto.equals("Salir")) {
        	b.setIcon(new ImageIcon("res/sprites/Salir(Normal).png"));
    		b.setRolloverIcon(new ImageIcon("res/sprites/Salir(Hover).png"));
    		b.setPressedIcon(new ImageIcon("res/sprites/Salir(Click).png"));
            b.addActionListener(e -> System.exit(0));
        }

        return b;
    }

    private void iniciarAnimacion() {

    	timer = new Timer(16, e -> {

            if (lastTime == 0) {
                lastTime = System.nanoTime();
            }

            long now = System.nanoTime();
            double delta = (now - lastTime) / 1_000_000_000.0;
            lastTime = now;

            double buttonSpeed = speed * delta;
            double logoSpeed = 2000 * delta;
            
            long elapsed = System.currentTimeMillis();


            if (logo.getY() < 80) {
                logo.setLocation(
                        logo.getX(),
                        logo.getY() + (int)(logoSpeed)
                );
            }



            if (elapsed > delayB1Start()) moverBoton(botones[0].getBoton(), 100, buttonSpeed);
            if (elapsed > delayB2Start()) moverBoton(botones[1].getBoton(), 100, buttonSpeed);
            if (elapsed > delayB3Start()) moverBoton(botones[2].getBoton(), 100, buttonSpeed);

            repaint();


            if (logo.getY() >= 80 &&
            		botones[0].getBoton().getX() >= 100 &&
            		botones[1].getBoton().getX() >= 100 &&
            		botones[2].getBoton().getX() >= 100) {

                timer.stop();
            }

        });

        timer.start();
    }
    
    private long startTime;

    private long delayB1Start() {
        if (startTime == 0) startTime = System.currentTimeMillis();
        return startTime + botones[0].getDelay();
    }

    private long delayB2Start() {
        return startTime + botones[1].getDelay();
    }

    private long delayB3Start() {
        return startTime + botones[2].getDelay();
    }

    private void moverBoton(JButton b, int destinoX, double dx) {

        if (b.getX() < destinoX) {

            int newX = (int)(b.getX() + dx);

            if (newX > destinoX) {
                newX = destinoX;
            }


            int eased = b.getX() + (int)((newX - b.getX()) * 0.8);

            b.setLocation(eased, b.getY());
        }
    }
    
    public void iniciarMenuAnimado() {
        iniciarAnimacion();
    }
    
   

    
}