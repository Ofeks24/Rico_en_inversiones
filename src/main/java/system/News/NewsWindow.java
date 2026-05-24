package system.News;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import tools.CompanyData;
import tools.MarketService;
import tools.NewsEvent;
import tools.NewsGenerator;
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
	
	
    public NewsWindow(int timeToUpdate,
            MarketService market,
            NewsGenerator newsGen,
            List<CompanyData> companies) {
		
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
        

        add(scroll, BorderLayout.CENTER);
        publicarNoticia(market, newsGen, companies);
    	publicarNoticia(market, newsGen, companies);
    	publicarNoticia(market, newsGen, companies);
    	publicarNoticia(market, newsGen, companies);
        
        toUpdate = new Timer(timeToUpdate, e ->{
        	publicarNoticia(market, newsGen, companies);
        	publicarNoticia(market, newsGen, companies);
        	publicarNoticia(market, newsGen, companies);
        	publicarNoticia(market, newsGen, companies);
        });
        toUpdate.start();
	
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
	
	private void publicarNoticia(MarketService market,
            NewsGenerator newsGen,
            List<CompanyData> companies) {
		NewsEvent ev = newsGen.generate(companies);
		market.applyNews(ev, companies);
		
		// Añadir sección al periódico
		JPanel seccion = crearSeccion(ev.getTitulo(), ev.getCuerpo());
		contenido.add(seccion, 4);   // insertar tras el separador
		contenido.revalidate();
		contenido.repaint();
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
