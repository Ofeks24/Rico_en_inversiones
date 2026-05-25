package system.News;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
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

    private Timer toUpdate;

    private final Color fondoPrincipal = new Color(235, 220, 190);
    private final Color fondoSeccion   = new Color(222, 205, 175);
    private final Color colorTexto     = new Color(60,  45,  30);
    private final Color colorTitulo    = new Color(40,  25,  15);
    
    private static final int BASE_WIDTH  = 1400;
    private static final int BASE_HEIGHT = 900;
    private float scale = 1f;

    // Zona dinámica donde van las noticias (después de la cabecera)
    private final JPanel zonaNoticas = new JPanel();

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public NewsWindow(int timeToUpdate,
                      MarketService market,
                      NewsGenerator newsGen,
                      List<CompanyData> companies) {
    	
    	addComponentListener(new java.awt.event.ComponentAdapter() {
    	    @Override
    	    public void componentResized(java.awt.event.ComponentEvent e) {

    	        float sx = getWidth()  / (float) BASE_WIDTH;
    	        float sy = getHeight() / (float) BASE_HEIGHT;

    	        scale = Math.min(sx, sy);

    	        actualizarEscala();
    	    }
    	});

        setLayout(new BorderLayout());
        setBackground(fondoPrincipal);

        JScrollPane scroll = new JScrollPane();
        scroll.setBorder(null);

        // Panel interior del periódico
        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(fondoPrincipal);
        new EmptyBorder(
        	    escalar(25),
        	    escalar(40),
        	    escalar(40),
        	    escalar(40)
        	);

        scroll.setViewportView(contenido);

        // =================================================
        // CABECERA DEL PERIÓDICO (estática)
        // =================================================

        JLabel titulo = new JLabel("Telégrafo de Montecristo");
        titulo.setFont(new Font("Serif", Font.BOLD, escalar(52)));
        titulo.setForeground(colorTitulo);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Edición Imperial • Año XII");
        subtitulo.setFont(new Font("Serif", Font.ITALIC, escalar(18)));
        subtitulo.setForeground(colorTexto);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        contenido.add(titulo);
        contenido.add(Box.createVerticalStrut(escalar(10)));
        contenido.add(subtitulo);
        contenido.add(Box.createVerticalStrut(escalar(25)));

        JSeparator sep = new JSeparator();
        sep.setForeground(colorTexto);
        sep.setAlignmentX(Component.CENTER_ALIGNMENT);
        sep.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 2));
        contenido.add(sep);
        contenido.add(Box.createVerticalStrut(25));

        // =================================================
        // ZONA DINÁMICA DE NOTICIAS
        // =================================================

        zonaNoticas.setLayout(new BoxLayout(zonaNoticas, BoxLayout.Y_AXIS));
        zonaNoticas.setBackground(fondoPrincipal);
        zonaNoticas.setAlignmentX(Component.LEFT_ALIGNMENT);
        zonaNoticas.setMaximumSize(
            new java.awt.Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        contenido.add(zonaNoticas);

        add(scroll, BorderLayout.CENTER);

        // Primera tanda
        publicarTanda(market, newsGen, companies);

        // Timer: reemplaza noticias cada [timeToUpdate] ms
        toUpdate = new Timer(timeToUpdate, e ->
            publicarTanda(market, newsGen, companies)
        );
        toUpdate.start();
    }

    // =====================================================
    // PUBLICAR 5 NOTICIAS EN LAYOUT 2 | 1 | 2
    // =====================================================

    private void publicarTanda(MarketService market,
                               NewsGenerator newsGen,
                               List<CompanyData> companies) {

        // Generar y aplicar 5 noticias
        NewsEvent n1 = generar(market, newsGen, companies);
        NewsEvent n2 = generar(market, newsGen, companies);
        NewsEvent n3 = generar(market, newsGen, companies);
        NewsEvent n4 = generar(market, newsGen, companies);
        NewsEvent n5 = generar(market, newsGen, companies);

        // Borrar noticias anteriores
        zonaNoticas.removeAll();

        // ── Fila superior: 2 noticias lado a lado ─────────
        zonaNoticas.add(crearFila2(n1, n2));
        zonaNoticas.add(Box.createVerticalStrut(escalar(12)));

        // ── Noticia central: ancho completo ───────────────
        zonaNoticas.add(crearSeccion(n3.getTitulo(), n3.getCuerpo(), true));
        zonaNoticas.add(Box.createVerticalStrut(escalar(12)));

        // ── Fila inferior: 2 noticias lado a lado ─────────
        zonaNoticas.add(crearFila2(n4, n5));

        zonaNoticas.revalidate();
        zonaNoticas.repaint();
    }

    /** Genera una noticia y aplica su efecto al mercado. */
    private NewsEvent generar(MarketService market,
                              NewsGenerator newsGen,
                              List<CompanyData> companies) {
        NewsEvent ev = newsGen.generate(companies);
        market.applyNews(ev, companies);
        return ev;
    }

    // =====================================================
    // LAYOUT HELPERS
    // =====================================================

    /** Fila con dos noticias una al lado de la otra. */
    private JPanel crearFila2(NewsEvent a, NewsEvent b) {
        JPanel fila = new JPanel(new GridLayout(1, 2, 12, 0));
        fila.setBackground(fondoPrincipal);
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);
        fila.setMaximumSize(
            new java.awt.Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        fila.add(crearSeccion(a.getTitulo(), a.getCuerpo(), false));
        fila.add(crearSeccion(b.getTitulo(), b.getCuerpo(), false));
        return fila;
    }

    /**
     * Crea una tarjeta de noticia.
     *
     * @param grande  true → fuente más grande (para la noticia central).
     */
    private JPanel crearSeccion(String tituloTexto,
                                String contenidoTexto,
                                boolean grande) {

        JPanel seccion = new JPanel(new BorderLayout(10, 10));
        seccion.setBackground(fondoSeccion);
        seccion.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel(
            "<html><body style='width:100%'>" + tituloTexto + "</body></html>");
        titulo.setFont(new Font("Serif", Font.BOLD, grande ? 34 : 26));
        titulo.setForeground(colorTitulo);

        JTextArea texto = new JTextArea(contenidoTexto);
        texto.setWrapStyleWord(true);
        texto.setLineWrap(true);
        texto.setEditable(false);
        texto.setOpaque(false);
        texto.setFont(new Font("Serif", Font.PLAIN, grande ? 22 : 18));
        texto.setForeground(colorTexto);

        seccion.add(titulo, BorderLayout.NORTH);
        seccion.add(texto,  BorderLayout.CENTER);

        return seccion;
    }
    
    private int escalar(int valor) {
        return Math.max(1, Math.round(valor * scale));
    }
    private void actualizarEscala() {

        actualizarComponente(this);

        revalidate();
    }
    
    private void actualizarComponente(Component c) {

        Font f = c.getFont();

        if (f != null) {

            int size = f.getSize();

            // tamaños originales aproximados
            int nuevo = Math.max(10, escalar(size));

            c.setFont(f.deriveFont((float) nuevo));
        }

        if (c instanceof JPanel panel) {

            for (Component child : panel.getComponents()) {
                actualizarComponente(child);
            }
        }
    }

    @Override public void onShow() {}
    @Override public void onHide() {}
}