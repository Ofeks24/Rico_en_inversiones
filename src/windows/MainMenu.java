package windows;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class MainMenu extends JFrame {

    JLabel logo;

    JButton b1;
    JButton b2;
    JButton b3;
    
    Timer timer;
    
    double speed = 1200;

    long lastTime;

    int delayB1 = 500;
    int delayB2 = 1000;
    int delayB3 = 1500;

    
    


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
        b1 = crearBoton("Jugar", -300, 300);
        b2 = crearBoton("Opciones", -300, 400);
        b3 = crearBoton("Salir", -300, 500);

        panel.add(b1);
        panel.add(b2);
        panel.add(b3);


        setVisible(true);
    }

    private JButton crearBoton(String texto, int x, int y) {

        JButton b = new JButton(texto);
        b.setBounds(x, y, 250, 60);
        b.setFocusPainted(false);

        if(texto.equals("Salir"))
            b.addActionListener(e -> System.exit(0));

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



            if (elapsed > delayB1Start()) moverBoton(b1, 100, buttonSpeed);
            if (elapsed > delayB2Start()) moverBoton(b2, 100, buttonSpeed);
            if (elapsed > delayB3Start()) moverBoton(b3, 100, buttonSpeed);

            repaint();


            if (logo.getY() >= 80 &&
                b1.getX() >= 100 &&
                b2.getX() >= 100 &&
                b3.getX() >= 100) {

                timer.stop();
            }

        });

        timer.start();
    }
    
    private long startTime;

    private long delayB1Start() {
        if (startTime == 0) startTime = System.currentTimeMillis();
        return startTime + delayB1;
    }

    private long delayB2Start() {
        return startTime + delayB2;
    }

    private long delayB3Start() {
        return startTime + delayB3;
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