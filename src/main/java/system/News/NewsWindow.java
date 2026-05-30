package system.News;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import tools.CompanyData;
import tools.MarketService;
import tools.NewsEvent;
import tools.NewsGenerator;
import tools.Screen;
import tools.Sector;


/**
 * Ventana principal del periódico "Telégrafo de Montecristo".
 * <p>
 * Simula la portada de un periódico de época con cinco noticias: dos en la
 * fila superior, una destacada en el centro y dos en la fila inferior.
 * Cada cierto tiempo (configurable con {@code timeToUpdate}) se genera una
 * nueva tanda de noticias mediante {@link tools.NewsGenerator} y se aplican
 * sus efectos al mercado con {@link tools.MarketService#applyNews}.
 * </p>
 * <p>
 * Al hacer clic en cualquier titular se navega a una vista de detalle
 * ({@link DetallePanel}) que muestra el artículo completo con imagen.
 * Implementa {@link tools.Screen} para detener el temporizador de
 * actualización cuando la ventana se cierra u oculta.
 * </p>
 */
public class NewsWindow extends JPanel implements Screen {

    private Timer toUpdate;

    // ── Paleta ────────────────────────────────────────────
    static final Color C_BG      = new Color(222, 205, 175);
    static final Color C_SECTION = new Color(235, 212, 175);
    static final Color C_BORDER  = new Color(60,  45,  30);
    static final Color C_TITLE   = new Color(40,  25,  15);
    static final Color C_TEXT    = new Color(60,  45,  30);
    static final Color C_HEADER  = new Color(245, 232, 205);
    static final Color C_LINK    = new Color(100, 50,  10);

    // ── Ruta base de logos ────────────────────────────────
    private static final String LOGOS = "/main/resources/empresas/";

    /**
     * Devuelve la ruta del recurso de imagen que ilustra una noticia
     * según su sector económico.
     * <p>
     * Actualmente todas las rutas apuntan al mismo recurso genérico
     * ({@code DORADO.png}); el método está preparado para asignar
     * imágenes distintas por sector en el futuro.
     * </p>
     *
     * @param s sector de la noticia; si es {@code null} se usa la imagen por defecto
     * @return ruta del recurso de imagen en el classpath
     */
    static String imagenPorSector(Sector s) {
        if (s == null) return LOGOS + "DORADO.png";
        return switch (s) {
            case NAVAL       -> LOGOS + "DORADO(1).png";
            case INDUSTRIA   -> LOGOS + "MECHANIQUE(1).png";
            case BOTANICA    -> LOGOS + "BOTANICA_ATLANTE(1).png";
            case AGRICULTURA -> LOGOS + "MOLINOS(1).png";
            case MANUFACTURA -> LOGOS + "JABONES(1).png";
            case GLOBAL      -> LOGOS + "BlasonMontecristo(3).png";
        };
    }

    // ── Slots de noticia (5 en total) ─────────────────────
    private final NoticiaCard[] cards = new NoticiaCard[5];

    // ── CardLayout para alternar portada ↔ detalle ───────
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel     switcher   = new JPanel(cardLayout);

    // ── Paneles hijos ─────────────────────────────────────
    private final JPanel      root;     // portada
    private final DetallePanel detalle; // vista de artículo completo

    /**
     * Construye la ventana del periódico y arranca el temporizador de
     * actualización automática de noticias.
     * <p>
     * Genera una primera tanda de noticias inmediatamente y programa
     * las siguientes con el intervalo indicado.
     * </p>
     *
     * @param timeToUpdate intervalo en milisegundos entre generaciones de
     *                     nuevas noticias (ej. {@code 10000} = cada 10 s)
     * @param market       servicio de mercado al que se aplican los efectos
     *                     de cada noticia generada
     * @param newsGen      generador de noticias que usa las plantillas JSON
     * @param companies    lista de empresas disponibles para personalizar
     *                     los titulares con nombres y sectores reales
     */
    public NewsWindow(int timeToUpdate,
                      MarketService market,
                      NewsGenerator newsGen,
                      List<CompanyData> companies) {

        setLayout(new BorderLayout());
        setBackground(C_BG);

        root    = new JPanel(null);
        root.setBackground(C_BG);

        detalle = new DetallePanel(() -> cardLayout.show(switcher, "PORTADA"));

        switcher.add(root,    "PORTADA");
        switcher.add(detalle, "DETALLE");
        add(switcher, BorderLayout.CENTER);

        // ── Cabecera del periódico ─────────────────────────
        root.add(buildHeader());   // índice 0

        // ── Cards (solo titular) ──────────────────────────
        for (int i = 0; i < 5; i++) {
            boolean importante = (i == 2);
            cards[i] = new NoticiaCard(importante, ev -> {
                detalle.mostrar(ev);
                cardLayout.show(switcher, "DETALLE");
            });
            root.add(cards[i]);    // índices 1-5
        }

        // ── Layout proporcional ───────────────────────────
        root.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) { layoutPortada(); }
        });

        publicarTanda(market, newsGen, companies);

        toUpdate = new Timer(timeToUpdate,
                e -> publicarTanda(market, newsGen, companies));
        toUpdate.start();
    }

    /**
     * Calcula el layout proporcional de los cinco slots de noticia
     * en función del tamaño actual del panel raíz.
     * <p>
     * Se llama automáticamente cada vez que el componente cambia de tamaño
     * gracias al {@link java.awt.event.ComponentListener} registrado en el
     * constructor.
     * </p>
     */
    private void layoutPortada() {
        int W = root.getWidth();
        int H = root.getHeight();
        if (W == 0 || H == 0) return;

        int pad  = (int)(W * 0.012);
        int gap  = (int)(W * 0.010);
        int cW   = W - pad * 2;

        int y    = pad;

        // cabecera
        int hCab = (int)(H * 0.14);
        root.getComponent(0).setBounds(pad, y, cW, hCab);
        y += hCab + gap;

        // fila superior: cards[0] + cards[1]
        int hTop  = (int)(H * 0.26);
        int halfW = (cW - gap) / 2;
        root.getComponent(1).setBounds(pad,            y, halfW, hTop);
        root.getComponent(2).setBounds(pad + halfW + gap, y, halfW, hTop);
        y += hTop + gap;

        // central: cards[2]
        int hMid = (int)(H * 0.34);
        root.getComponent(3).setBounds(pad, y, cW, hMid);
        y += hMid + gap;

        // fila inferior: cards[3] + cards[4]
        int hBot = (int)(H * 0.26);
        root.getComponent(4).setBounds(pad,             y, halfW, hBot);
        root.getComponent(5).setBounds(pad + halfW + gap, y, halfW, hBot);
    }

    /**
     * Construye y devuelve la cabecera decorativa del periódico,
     * con el nombre "Telégrafo de Montecristo", separadores horizontales
     * y la línea de edición en cursiva.
     *
     * @return panel con la cabecera maquetada
     */
    private JPanel buildHeader() {
        JPanel h = new JPanel(new GridBagLayout());
        h.setBackground(C_HEADER);
        h.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(2, 2, 2, 2, C_BORDER),
            new EmptyBorder(6, 12, 6, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;

        JSeparator sepTop = new JSeparator();
        sepTop.setForeground(C_BORDER);
        gbc.insets = new Insets(0, 0, 4, 0);
        h.add(sepTop, gbc);

        JLabel titulo = new JLabel("Telégrafo de Montecristo", SwingConstants.CENTER);
        titulo.setFont(new Font("Serif", Font.BOLD, 42));
        titulo.setForeground(C_TITLE);
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 2, 0);
        h.add(titulo, gbc);

        JLabel sub = new JLabel(
            "Edición Imperial  •  Año XII  •  Noticias del Imperio",
            SwingConstants.CENTER);
        sub.setFont(new Font("Serif", Font.ITALIC, 15));
        sub.setForeground(C_TEXT);
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 4, 0);
        h.add(sub, gbc);

        JSeparator sepBot = new JSeparator();
        sepBot.setForeground(C_BORDER);
        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 0, 0);
        h.add(sepBot, gbc);

        return h;
    }

    /**
     * Genera una nueva tanda de cinco noticias, aplica sus efectos al
     * mercado y actualiza cada slot de la portada.
     * <p>
     * Se invoca en el constructor y periódicamente por el {@link Timer}
     * interno de la ventana.
     * </p>
     *
     * @param market    servicio de mercado para aplicar los impactos
     * @param newsGen   generador que produce cada {@link tools.NewsEvent}
     * @param companies lista de empresas con las que se personalizan los textos
     */
    private void publicarTanda(MarketService market,
                               NewsGenerator newsGen,
                               List<CompanyData> companies) {
        for (int i = 0; i < 5; i++) {
            NewsEvent ev = newsGen.generate(companies);
            market.applyNews(ev, companies);
            cards[i].setNoticia(ev);
        }
        layoutPortada();
        root.revalidate();
        root.repaint();
    }

    /**
     * Tarjeta clicable de portada que muestra únicamente el titular de una noticia.
     * <p>
     * Al hacer doble clic (o clic simple, según el listener) sobre la tarjeta
     * se invoca el callback {@code onClick} con el {@link tools.NewsEvent}
     * correspondiente para que {@link NewsWindow} navegue al {@link DetallePanel}.
     * La tarjeta central se renderiza en tamaño destacado con separadores y
     * fleuron decorativo.
     * </p>
     */
    private static class NoticiaCard extends JPanel {

        private final JLabel          tituloLabel;
        private final boolean         importante;
        private final Consumer<NewsEvent> onClick;
        private       NewsEvent        eventoActual;

        /**
         * Construye la tarjeta con el estilo visual adecuado.
         *
         * @param importante {@code true} para el slot central (mayor tamaño
         *                   y decoración adicional); {@code false} para los laterales
         * @param onClick    acción a ejecutar cuando el usuario hace clic,
         *                   recibiendo el {@link tools.NewsEvent} actual de la tarjeta
         */
        NoticiaCard(boolean importante, Consumer<NewsEvent> onClick) {
            this.importante = importante;
            this.onClick    = onClick;

            setLayout(new GridBagLayout());
            setBackground(C_SECTION);
            setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(2, 2, 2, 2, C_BORDER),
                new EmptyBorder(
                    importante ? 22 : 14,
                    importante ? 26 : 16,
                    importante ? 22 : 14,
                    importante ? 26 : 16
                )
            ));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            tituloLabel = new JLabel("", SwingConstants.CENTER);
            tituloLabel.setFont(new Font("Serif", Font.BOLD,
                                         importante ? 30 : 20));
            tituloLabel.setForeground(C_LINK);
            // Subrayado sutil estilo enlace de periódico
            tituloLabel.setBorder(BorderFactory.createMatteBorder(
                0, 0, 1, 0,
                new Color(C_LINK.getRed(), C_LINK.getGreen(), C_LINK.getBlue(), 100)
            ));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0; gbc.gridy = 0;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.anchor = GridBagConstraints.CENTER;

            if (importante) {
                JSeparator sep1 = new JSeparator();
                sep1.setForeground(C_BORDER);
                gbc.insets = new Insets(0, 0, 10, 0);
                add(sep1, gbc);
                gbc.gridy++;
            }

            gbc.insets = new Insets(0, 0, 0, 0);
            add(tituloLabel, gbc);

            if (importante) {
                gbc.gridy++;
                JSeparator sep2 = new JSeparator();
                sep2.setForeground(C_BORDER);
                gbc.insets = new Insets(10, 0, 0, 0);
                add(sep2, gbc);

                gbc.gridy++;
                JLabel fleuron = new JLabel("— ✦ —", SwingConstants.CENTER);
                fleuron.setFont(new Font("Serif", Font.ITALIC, 13));
                fleuron.setForeground(
                    new Color(C_BORDER.getRed(), C_BORDER.getGreen(),
                              C_BORDER.getBlue(), 140));
                gbc.insets = new Insets(6, 0, 0, 0);
                add(fleuron, gbc);
            }

            // ── Hover ─────────────────────────────────────
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    setBackground(C_HEADER);
                    tituloLabel.setForeground(C_TITLE);
                    repaint();
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    setBackground(C_SECTION);
                    tituloLabel.setForeground(C_LINK);
                    repaint();
                }
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (eventoActual != null) onClick.accept(eventoActual);
                }
            });
        }

        /**
         * Actualiza la tarjeta con los datos de la noticia indicada.
         * <p>
         * Actualiza el texto del titular y almacena la referencia al evento
         * para pasárselo al callback al hacer clic.
         * </p>
         *
         * @param ev el evento de noticia a mostrar
         */
        void setNoticia(NewsEvent ev) {
            eventoActual = ev;
            tituloLabel.setText(
                "<html><div style='text-align:center;'>"
                + ev.getTitulo() + "</div></html>"
            );
        }
    }

    /**
     * Panel de detalle que ocupa la pantalla completa y muestra un artículo
     * de prensa al completo: nombre del periódico, titular, cuerpo de texto
     * e imagen ilustrativa.
     * <p>
     * Incluye un botón "← Volver" que invoca un callback para regresar
     * a la portada del periódico mediante el {@link CardLayout} padre.
     * </p>
     */
    private static class DetallePanel extends JPanel {

        // Campos actualizables
        private final JLabel    titularLabel;
        private final JTextArea cuerpoArea;
        private final JLabel    imagenLabel;

        // Referencia al viewport para resetear el scroll al mostrar una noticia
        private final JScrollPane mainScroll;

        /**
         * Construye el panel de detalle con su estructura visual completa.
         *
         * @param onVolver acción que se ejecuta al pulsar el botón "Volver",
         *                 normalmente {@code () -> cardLayout.show(switcher, "PORTADA")}
         */
        DetallePanel(Runnable onVolver) {
            setLayout(new BorderLayout());
            setBackground(C_BG);

            // ── NORTE: nombre del periódico + botón + titular ──
            JPanel norte = new JPanel(new BorderLayout(0, 0));
            norte.setBackground(C_HEADER);
            norte.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(2, 2, 2, 2, C_BORDER),
                new EmptyBorder(16, 24, 16, 24)
            ));

            // Fila superior: botón volver (izq) + nombre periódico (centro)
            JPanel filaTop = new JPanel(new BorderLayout(16, 0));
            filaTop.setOpaque(false);

            JButton btnVolver = new JButton("← Volver");
            btnVolver.setFont(new Font("Serif", Font.BOLD, 16));
            btnVolver.setForeground(C_TITLE);
            btnVolver.setBackground(C_HEADER);
            btnVolver.setFocusPainted(false);
            btnVolver.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, C_BORDER),
                new EmptyBorder(6, 14, 6, 14)
            ));
            btnVolver.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btnVolver.addActionListener(e -> onVolver.run());
            btnVolver.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                    btnVolver.setBackground(new Color(230, 215, 185));
                }
                @Override public void mouseExited(java.awt.event.MouseEvent e) {
                    btnVolver.setBackground(C_HEADER);
                }
            });

            JLabel nombrePeriodico = new JLabel(
                "Telégrafo de Montecristo", SwingConstants.CENTER);
            nombrePeriodico.setFont(new Font("Serif", Font.BOLD, 26));
            nombrePeriodico.setForeground(C_TITLE);

            filaTop.add(btnVolver,       BorderLayout.WEST);
            filaTop.add(nombrePeriodico, BorderLayout.CENTER);

            // Separador
            JSeparator sep = new JSeparator();
            sep.setForeground(C_BORDER);
            JPanel sepWrapper = new JPanel(new BorderLayout());
            sepWrapper.setOpaque(false);
            sepWrapper.setBorder(new EmptyBorder(10, 0, 10, 0));
            sepWrapper.add(sep, BorderLayout.CENTER);

            // Titular de la noticia
            titularLabel = new JLabel("", SwingConstants.CENTER);
            titularLabel.setFont(new Font("Serif", Font.BOLD + Font.ITALIC, 34));
            titularLabel.setForeground(C_TITLE);

            norte.add(filaTop,      BorderLayout.NORTH);
            norte.add(sepWrapper,   BorderLayout.CENTER);
            norte.add(titularLabel, BorderLayout.SOUTH);

            add(norte, BorderLayout.NORTH);

            // ── CENTRO: columna única con scroll ──────────────
            // Contenedor scrollable: texto encima, imagen debajo
            JPanel columna = new JPanel();
            columna.setLayout(new BoxLayout(columna, BoxLayout.Y_AXIS));
            columna.setBackground(C_SECTION);
            columna.setBorder(new EmptyBorder(36, 80, 36, 80));
            
            // Imagen grande centrada
            imagenLabel = new JLabel("", SwingConstants.CENTER);
            imagenLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            columna.add(imagenLabel);
            
            // Texto del artículo
            cuerpoArea = new JTextArea();
            cuerpoArea.setFont(new Font("Serif", Font.PLAIN, 22));
            cuerpoArea.setForeground(C_TEXT);
            cuerpoArea.setBackground(C_SECTION);
            cuerpoArea.setWrapStyleWord(true);
            cuerpoArea.setLineWrap(true);
            cuerpoArea.setEditable(false);
            cuerpoArea.setOpaque(false);
            cuerpoArea.setFocusable(false);
            cuerpoArea.setAlignmentX(Component.CENTER_ALIGNMENT);
            columna.add(cuerpoArea);

            // Separador decorativo entre texto e imagen
            columna.add(Box.createVerticalStrut(32));
            //JSeparator sepH = new JSeparator(SwingConstants.HORIZONTAL);
            //sepH.setForeground(C_BORDER);
            //sepH.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
            //sepH.setAlignmentX(Component.CENTER_ALIGNMENT);
            //columna.add(sepH);
            //columna.add(Box.createVerticalStrut(32));

            

            // Fleuron al pie de la imagen
            columna.add(Box.createVerticalStrut(18));
            JLabel fleuron = new JLabel("— ✦ —", SwingConstants.CENTER);
            fleuron.setFont(new Font("Serif", Font.ITALIC, 16));
            fleuron.setForeground(
                new Color(C_BORDER.getRed(), C_BORDER.getGreen(),
                          C_BORDER.getBlue(), 150));
            fleuron.setAlignmentX(Component.CENTER_ALIGNMENT);
            columna.add(fleuron);
            columna.add(Box.createVerticalStrut(16));

            // Wrap en un panel con borde exterior
            JPanel centroWrapper = new JPanel(new BorderLayout());
            centroWrapper.setBackground(C_SECTION);
            centroWrapper.setBorder(new MatteBorder(0, 2, 2, 2, C_BORDER));
            centroWrapper.add(columna, BorderLayout.CENTER);

            mainScroll = new JScrollPane(centroWrapper,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            mainScroll.setBorder(null);
            mainScroll.getVerticalScrollBar().setUnitIncrement(16);

            add(mainScroll, BorderLayout.CENTER);
            revalidate();
            repaint();
        }

        // ── Rellenar con datos del NewsEvent ──────────────
        /**
         * Rellena el panel con los datos del evento de noticia indicado y
         * resetea el scroll al inicio del artículo.
         *
         * @param ev el evento de noticia cuyo artículo se va a mostrar
         */
        void mostrar(NewsEvent ev) {
            // Titular
            titularLabel.setText(
                "<html><div style='text-align:center;'>"
                + ev.getTitulo() + "</div></html>"
            );

            // Cuerpo
            cuerpoArea.setText(ev.getCuerpo());

            // Imagen por sector
            cargarImagen(ev.getSector());

            // Volver al inicio del scroll
            SwingUtilities.invokeLater(() ->
                mainScroll.getVerticalScrollBar().setValue(0)
            );

            revalidate();
            repaint();
        }

        /**
         * Carga y escala la imagen ilustrativa correspondiente al sector de la
         * noticia, mostrándola centrada bajo el cuerpo del artículo.
         * <p>
         * Si el recurso no existe o no puede cargarse, oculta la imagen
         * sin lanzar excepción.
         * </p>
         *
         * @param sector sector de la noticia cuya imagen se va a mostrar
         */
        private void cargarImagen(Sector sector) {
            String ruta = imagenPorSector(sector);
            try {
                java.net.URL url = DetallePanel.class.getResource(ruta);
                if (url != null) {
                    ImageIcon raw  = new ImageIcon(url);
                    int origW = raw.getIconWidth();
                    int origH = raw.getIconHeight();
                    if (origH > 0 && origW > 0) {
                        // Imagen grande: 420 px de alto
                        int newH = 420;
                        int newW = (origW * newH) / origH;
                        Image scaled = raw.getImage()
                            .getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
                        imagenLabel.setIcon(new ImageIcon(scaled));
                        imagenLabel.setText("");
                    } else {
                        imagenLabel.setIcon(null);
                        imagenLabel.setText("");
                    }
                } else {
                    imagenLabel.setIcon(null);
                    imagenLabel.setText("");
                }
            } catch (Exception ex) {
                imagenLabel.setIcon(null);
                imagenLabel.setText("");
            }
        }
    }

    /**
     * No realiza ninguna acción al mostrarse.
     * {@inheritDoc}
     */
    @Override public void onShow() {}
    
    /**
     * Detiene el temporizador de actualización automática para evitar
     * que siga generando noticias y consumiendo CPU en segundo plano.
     * {@inheritDoc}
     */
    @Override public void onHide() { if (toUpdate != null) toUpdate.stop(); }
}