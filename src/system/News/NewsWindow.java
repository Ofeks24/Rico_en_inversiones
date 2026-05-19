package system.News;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import tools.LocalAI;
import tools.Screen;

public class NewsWindow extends JPanel implements Screen {
	
	Timer toUpdate;
	private final Color fondoPrincipal = new Color(235, 220, 190);
    private final Color fondoSeccion = new Color(222, 205, 175);
    private final Color colorTexto = new Color(60, 45, 30);
    private final Color colorTitulo = new Color(40, 25, 15);
    JPanel contenido = new JPanel();

    String prompt =
    		"""
    		Genera una noticia ficticia para un periódico antiguo.

    		Contexto:
    		- Ciudad: Montecristo
    		- Año: 1895
    		- Ambiente económico tenso

    		Tema:
    		Crisis en la industria naval.

    		Formato:
    		- Título llamativo
    		- Dos párrafos
    		- Tono serio y elegante
    		- Español formal
    		""";
	
	
	public NewsWindow(int timeToUpdate) {
		
		toUpdate = new Timer(timeToUpdate, e ->{
			updateInfo();
		});
		
		setLayout(new BorderLayout());
        setBackground(fondoPrincipal);

        // Scroll principal
        JScrollPane scroll = new JScrollPane();
        scroll.setBorder(null);

        // Panel interior del periódico
        
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(fondoPrincipal);
        contenido.setBorder(new EmptyBorder(25, 40, 40, 40));

        scroll.setViewportView(contenido);

        // =================================================
        // CABECERA DEL PERIÓDICO
        // =================================================

        JLabel titulo = new JLabel("Telégrafo de Montecristo");

        titulo.setFont(new Font("Serif", Font.BOLD, 52));
        titulo.setForeground(colorTitulo);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Edición Imperial • Año XII");

        subtitulo.setFont(new Font("Serif", Font.ITALIC, 18));
        subtitulo.setForeground(colorTexto);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        contenido.add(titulo);
        contenido.add(Box.createVerticalStrut(10));
        contenido.add(subtitulo);
        contenido.add(Box.createVerticalStrut(25));

        // Línea decorativa
        JSeparator sep = new JSeparator();
        sep.setForeground(colorTexto);

        contenido.add(sep);
        contenido.add(Box.createVerticalStrut(25));
        /*Timer timer = new Timer(1000, e->{
    		System.out.println("Sigo pensando");
    	});
    	timer.start();
        new Thread(() -> {

            String noticia = LocalAI.generarNoticia(prompt);

            SwingUtilities.invokeLater(() -> {

            	System.out.println(noticia);

            });
            timer.stop();
        }).start();*/

        

        // =================================================
        // SECCIONES DEL PERIÓDICO
        // =================================================
        
        contenido.add(crearSeccion(
                "Política",
                "El consejo imperial ha aprobado nuevas medidas para reforzar "
                        + "el comercio marítimo entre las colonias occidentales y "
                        + "la capital del reino. Comerciantes y nobles celebran "
                        + "la estabilidad económica que dichas decisiones podrían generar."
        ));

        contenido.add(Box.createVerticalStrut(20));

        contenido.add(crearSeccion(
                "Economía",
                "Las acciones de las compañías navieras continúan creciendo "
                        + "tras las recientes rutas abiertas en el Mediterráneo. "
                        + "Analistas aseguran que la expansión comercial podría "
                        + "marcar una nueva era de prosperidad."
        ));

        contenido.add(Box.createVerticalStrut(20));

        contenido.add(crearSeccion(
                "Sucesos",
                "Vecinos del distrito norte afirman haber visto luces extrañas "
                        + "sobre el antiguo observatorio abandonado. Las autoridades "
                        + "todavía no han emitido declaraciones oficiales."
        ));

        contenido.add(Box.createVerticalStrut(20));
        contenido.add(crearSeccion(
                "Cultura",
                "El teatro real inaugurará esta semana una nueva representación "
                        + "inspirada en las leyendas de Montecristo. La expectación "
                        + "entre la aristocracia es enorme."
        ));
        for(int index=0;index<contenido.getComponentCount();index++) {
        	System.out.println(index+": "+contenido.getComponent(index).getName());
        }
        

        add(scroll, BorderLayout.CENTER);
	
	}
	
	
	private JPanel crearSeccion(String tituloTexto, String contenidoTexto) {

        JPanel seccion = new JPanel();
        seccion.setName(tituloTexto);
        seccion.setLayout(new BorderLayout(10, 10));
        seccion.setBackground(fondoSeccion);
        seccion.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Título sección
        JLabel titulo = new JLabel(tituloTexto);

        titulo.setFont(new Font("Serif", Font.BOLD, 30));
        titulo.setForeground(colorTitulo);

        // Contenido
        JTextArea texto = new JTextArea(contenidoTexto);

        texto.setWrapStyleWord(true);
        texto.setLineWrap(true);
        texto.setEditable(false);
        texto.setOpaque(false);

        texto.setFont(new Font("Serif", Font.PLAIN, 20));
        texto.setForeground(colorTexto);

        seccion.add(titulo, BorderLayout.NORTH);
        seccion.add(texto, BorderLayout.CENTER);
        

        return seccion;
    }

	private void updateInfo() {
		
		
	}

	@Override
	public void onShow() {
		// TODO Auto-generated method stub

	}


	@Override
	public void onHide() {
		// TODO Auto-generated method stub

	}

}
