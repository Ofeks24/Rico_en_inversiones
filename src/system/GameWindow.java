package system;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import system.Investment.InvestmentWindow;
import system.News.NewsWindow;
import tools.Clock;
import tools.DesktopGridLayout;
import tools.OpenAppWindow;
import tools.Screen;
import tools.TaskBarManager;
import tools.Utils;
import tools.WindowManager;

import java.awt.*;
import java.awt.event.*;

public class GameWindow extends JPanel implements Screen {

    private JPanel iconoSeleccionado = null;
    private Clock time;
    private JDesktopPane appsLayer = new JDesktopPane();
    private WindowManager windowManager;
    private TaskBarManager taskBarManager;

    public GameWindow(Runnable exit, Clock time) {

    	this.time=time;
        JPanel root = new JPanel(new GridBagLayout());
        setLayout(new BorderLayout());
        add(root, BorderLayout.CENTER);


        GridBagConstraints gbc = new GridBagConstraints();

        // =================================================
        // ESCRITORIO (FONDO)
        // =================================================

        JLayeredPane desktopContainer = new JLayeredPane();
        desktopContainer.setOpaque(true);
        desktopContainer.setBackground(new Color(85,171,170));
        
        JPanel desktopIconsLayer = new JPanel();
        desktopIconsLayer.setLayout(new DesktopGridLayout());
        desktopIconsLayer.setOpaque(false);
        
        appsLayer.setOpaque(false);
        appsLayer.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
        
        
        JPanel icono1 = crearIcono("Robbin Hub", Utils.escalarIcono("res/logos/RobbinHub(icono).png", 75));
        desktopIconsLayer.add(icono1);
        JPanel icono2 = crearIcono("Telégrafo de Montecristo", Utils.escalarIcono("res/logos/TelegrafoDeMontecristo(icono)(1).png", 75));
        desktopIconsLayer.add(icono2);
        JPanel icono3 = crearIcono("Stats.U", Utils.escalarIcono("res/logos/Doors(Closed).png", 50));
        desktopIconsLayer.add(icono3);
        JPanel icono4 = crearIcono("Tienda", Utils.escalarIcono("res/logos/Doors(Closed).png", 50));
        desktopIconsLayer.add(icono4);
        JPanel icono5 = crearIcono("BreakRoom", Utils.escalarIcono("res/logos/Doors(Closed).png", 50));
        desktopIconsLayer.add(icono5);
        
        desktopContainer.add(desktopIconsLayer, JLayeredPane.DEFAULT_LAYER);
        desktopContainer.add(appsLayer, JLayeredPane.PALETTE_LAYER);
        
        
        desktopContainer.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {

                int w = desktopContainer.getWidth();
                int h = desktopContainer.getHeight();

                desktopIconsLayer.setBounds(0, 0, w, h);
                appsLayer.setBounds(0, 0, w, h);
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;

        root.add(desktopContainer, gbc);
        

        // =================================================
        // BARRA DE TAREAS
        // =================================================
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(new Color(195, 199, 200));
        barra.setPreferredSize(new Dimension(0, 50));

        // Botón inicio
        JButton inicio = new JButton(Utils.escalarIcono("res/logos/Doors(Closed).png", 25));
        inicio.setRolloverIcon(Utils.escalarIcono("res/logos/Doors(Open).png", 25));
        inicio.setPressedIcon(Utils.escalarIcono("res/logos/Doors(Open).png", 25));

        inicio.addActionListener(e -> {
            if (exit != null) exit.run();
        });

        inicio.setFocusPainted(false);
        inicio.setBorderPainted(false);
        inicio.setContentAreaFilled(false);
        inicio.setOpaque(false);

        barra.add(inicio, BorderLayout.WEST);

        // Zona central
        JPanel centroBarra = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        centroBarra.setOpaque(false);

        centroBarra.add(crearBotonBarra("Explorador"));
        centroBarra.add(crearBotonBarra("Chrome"));
        centroBarra.add(crearBotonBarra("Editor"));
        taskBarManager = new TaskBarManager(centroBarra);
        windowManager = new WindowManager(
            appsLayer,
            taskBarManager
        );
        
        barra.add(centroBarra, BorderLayout.CENTER);
        

        // añadir a la barra
        barra.add(crearReloj(), BorderLayout.EAST);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        root.add(barra, gbc);

        add(root);
    }

    // =====================================================
    // ICONO DE ESCRITORIO
    // =====================================================
    private JPanel crearIcono(String texto, ImageIcon imagen) {

        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(true);
        p.setBackground(new Color(0, 0, 0, 0));
        p.setBorder(BorderFactory.createEmptyBorder(7, 0, 0, 0));
        JLabel icono = new JLabel(imagen);
        icono.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nombre = new JLabel(
        	    "<html><div style='text-align:center; width:70px;'>"
        	    + texto +
        	    "</div></html>"
        	);

    	nombre.setForeground(Color.WHITE);
    	nombre.setAlignmentX(Component.CENTER_ALIGNMENT);
    	nombre.setHorizontalAlignment(SwingConstants.CENTER);
        
        p.add(icono);
        p.add(nombre);

        Color hover = new Color(0,120,255,80);
        Color seleccionado = new Color(0,120,255,140);
        Color transparente = new Color(0,0,0,0);

        p.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (iconoSeleccionado != p) {
                    p.setBackground(hover);
                }
                repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (iconoSeleccionado != p) {
                    p.setBackground(transparente);
                }
                repaint();
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {

                // quitar anterior
                if (iconoSeleccionado != null && iconoSeleccionado != p) {
                    iconoSeleccionado.setBackground(transparente);
                }

                // seleccionar nuevo
                iconoSeleccionado = p;
                p.setBackground(seleccionado);
                repaint();
            }
            @Override
            public void mouseClicked(MouseEvent e) {
            	String ruta = "res/logos/";
                if (e.getClickCount() == 2) {
                	if(texto.equals("Robbin Hub")) {
                    	abrirWindow(texto,
                    			Utils.escalarIcono(ruta+"RobbinHub(icono).png", 25),
                    			InvestmentWindow.create()
                    			);
                    }
                	if (texto.equals("Telégrafo de Montecristo")) {
                    	abrirWindow(texto,
                    			Utils.escalarIcono(ruta+"TelegrafoDeMontecristo(icono)(1).png", 25),
                    			new NewsWindow(10000)
                    			);
                    }
                    if (texto.equals("Stats.U")) {
                    	abrirWindow(texto,
                    			Utils.escalarIcono(ruta+"Doors(Closed).png", 25),
                    			new JPanel()
                    			);
                    }
                    if (texto.equals("Tienda")) {
                    	abrirWindow(texto,
                    			Utils.escalarIcono(ruta+"Doors(Closed).png", 25),
                    			new JPanel()
                    			);
                    }
                    if (texto.equals("BreakRoom")) {
                    	abrirWindow(texto,
                    			Utils.escalarIcono(ruta+"Doors(Closed).png", 25),
                    			new JPanel()
                    			);
                    }
                	
                }
                
            }
        });

        return p;
    }
    
    
    
    private void abrirWindow(String nombre, ImageIcon icono, JPanel window) {
        windowManager.openWindow(
            Utils.convertirEnId(nombre),
            icono,
            () -> {
                OpenAppWindow win = new OpenAppWindow(nombre,
                	window,
                    800,
                    500,
                    icono
                );
                win.setLocation(200, 100);
                return win;
            }
        );
    }
    
    @SuppressWarnings("unused")
	private void abrirWindow(String nombre, ImageIcon icono, JPanel window, int x, int y) {
        windowManager.openWindow(
            Utils.convertirEnId(nombre),
            icono,
            () -> {
                OpenAppWindow win = new OpenAppWindow(nombre,
                	window,
                    x,
                    y,
                    icono
                );
                win.setLocation(200, 100);
                return win;
            }
        );
    }
    
    

    // =====================================================
    // BOTÓN BARRA TAREAS
    // =====================================================
    private JButton crearBotonBarra(String txt) {

        JButton b = new JButton(txt);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(110, 32));

        return b;
    }
    
    private Component crearReloj() {
    	JLabel hora = new JLabel();
        JLabel fecha = new JLabel();

        // Fuente estilo Windows clásico
        Font font = new Font("Dialog", Font.PLAIN, 14);
        hora.setFont(font);
        fecha.setFont(font);

        // Colores
        hora.setForeground(Color.BLACK);
        fecha.setForeground(Color.BLACK);

        // Alineación derecha
        hora.setHorizontalAlignment(SwingConstants.RIGHT);
        fecha.setHorizontalAlignment(SwingConstants.RIGHT);

        // Panel contenedor
        JPanel panelHora = new JPanel();
        panelHora.setLayout(new BoxLayout(panelHora, BoxLayout.Y_AXIS));
        panelHora.setBackground(new Color(195, 199, 200)); // gris Win95

        // Borde 3D hundido (clave visual)
        panelHora.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createBevelBorder(1), // LOWERED
            new EmptyBorder(2, 6, 2, 6)
        ));

        // Añadir labels
        panelHora.add(hora);
        panelHora.add(fecha);

        // ===== ACTUALIZACIÓN =====

        // inicial
        Runnable updateClock = () -> {
            hora.setText(String.format("%02d:%02d",
                time.getHour(),
                time.getMinute()
            ));

            fecha.setText(String.format("%02d/%02d/%04d",
                time.getDay(),
                time.getMonth(),
                time.getYear()
            ));
        };

        updateClock.run();

        // listener del clock
        time.addListener(updateClock);
        return panelHora;
    }

	@Override
	public void onShow() {
		time.initClock(2000);
	}

	@Override
	public void onHide() {
		time.detener();
	}
    
    
}