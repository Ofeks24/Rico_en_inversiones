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

public class NewsWindow extends JPanel implements Screen {

    private Timer toUpdate;

    // ── Paleta ────────────────────────────────────────────
    static final Color C_BG      = new Color(235, 220, 190);
    static final Color C_SECTION = new Color(222, 205, 175);
    static final Color C_BORDER  = new Color(60,  45,  30);
    static final Color C_TITLE   = new Color(40,  25,  15);
    static final Color C_TEXT    = new Color(60,  45,  30);
    static final Color C_HEADER  = new Color(245, 232, 205);
    static final Color C_LINK    = new Color(100, 50,  10);

    // ── Ruta base de logos ────────────────────────────────
    private static final String LOGOS = "/main/resources/empresas/";

    /**
     * Devuelve la ruta de la imagen a mostrar al pie de cada noticia,
     * según el sector al que pertenece.
     * Los nombres de archivo deben existir en src/main/resources/logos/.
     */
    static String imagenPorSector(Sector s) {
        if (s == null) return LOGOS + "DORADO.png";
        return switch (s) {
            case NAVAL       -> LOGOS + "DORADO.png";
            case INDUSTRIA   -> LOGOS + "DORADO.png";
            case BOTANICA    -> LOGOS + "DORADO.png";
            case AGRICULTURA -> LOGOS + "DORADO.png";
            case MANUFACTURA -> LOGOS + "DORADO.png";
            case GLOBAL      -> LOGOS + "DORADO.png";
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

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

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

    // =====================================================
    // LAYOUT PROPORCIONAL  (portada)
    // =====================================================

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

    // =====================================================
    // CABECERA DEL PERIÓDICO
    // =====================================================

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

    // =====================================================
    // PUBLICAR TANDA
    // =====================================================

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

    // =====================================================
    // CARD DE PORTADA  — solo titular, clicable
    // =====================================================

    private static class NoticiaCard extends JPanel {

        private final JLabel          tituloLabel;
        private final boolean         importante;
        private final Consumer<NewsEvent> onClick;
        private       NewsEvent        eventoActual;

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

        void setNoticia(NewsEvent ev) {
            eventoActual = ev;
            tituloLabel.setText(
                "<html><div style='text-align:center;'>"
                + ev.getTitulo() + "</div></html>"
            );
        }
    }

    // =====================================================
    // PANEL DE DETALLE — artículo completo a pantalla llena
    // =====================================================

    private static class DetallePanel extends JPanel {

        // Campos actualizables
        private final JLabel    titularLabel;
        private final JTextArea cuerpoArea;
        private final JLabel    imagenLabel;

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

            norte.add(filaTop,    BorderLayout.NORTH);
            norte.add(sepWrapper, BorderLayout.CENTER);
            norte.add(titularLabel, BorderLayout.SOUTH);

            add(norte, BorderLayout.NORTH);

            // ── CENTRO: cuerpo de la noticia ──────────────
            JPanel centro = new JPanel(new BorderLayout());
            centro.setBackground(C_SECTION);
            centro.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 2, 0, 2, C_BORDER),
                new EmptyBorder(36, 80, 36, 80)
            ));

            cuerpoArea = new JTextArea();
            cuerpoArea.setFont(new Font("Serif", Font.PLAIN, 22));
            cuerpoArea.setForeground(C_TEXT);
            cuerpoArea.setBackground(C_SECTION);
            cuerpoArea.setWrapStyleWord(true);
            cuerpoArea.setLineWrap(true);
            cuerpoArea.setEditable(false);
            cuerpoArea.setOpaque(false);
            cuerpoArea.setFocusable(false);

            JScrollPane scroll = new JScrollPane(cuerpoArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scroll.setBorder(null);
            scroll.setOpaque(false);
            scroll.getViewport().setOpaque(false);

            centro.add(scroll, BorderLayout.CENTER);
            add(centro, BorderLayout.CENTER);

            // ── SUR: imagen de empresa/sector ─────────────
            JPanel sur = new JPanel(new BorderLayout(0, 8));
            sur.setBackground(C_HEADER);
            sur.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(2, 2, 2, 2, C_BORDER),
                new EmptyBorder(16, 24, 16, 24)
            ));

            imagenLabel = new JLabel("", SwingConstants.CENTER);
            imagenLabel.setPreferredSize(new Dimension(0, 90));

            JLabel fleuron = new JLabel("— ✦ —", SwingConstants.CENTER);
            fleuron.setFont(new Font("Serif", Font.ITALIC, 14));
            fleuron.setForeground(
                new Color(C_BORDER.getRed(), C_BORDER.getGreen(),
                          C_BORDER.getBlue(), 150));

            sur.add(imagenLabel, BorderLayout.CENTER);
            sur.add(fleuron,     BorderLayout.SOUTH);

            add(sur, BorderLayout.SOUTH);
        }

        // ── Rellenar con datos del NewsEvent ──────────────
        void mostrar(NewsEvent ev) {
            // Titular
            titularLabel.setText(
                "<html><div style='text-align:center;'>"
                + ev.getTitulo() + "</div></html>"
            );

            // Cuerpo
            cuerpoArea.setText(ev.getCuerpo());
            cuerpoArea.setCaretPosition(0);

            // Imagen por sector
            cargarImagen(ev.getSector());

            revalidate();
            repaint();
        }

        private void cargarImagen(Sector sector) {
            String ruta = imagenPorSector(sector);
            try {
                java.net.URL url = DetallePanel.class.getResource(ruta);
                if (url != null) {
                    ImageIcon raw  = new ImageIcon(url);
                    int origW = raw.getIconWidth();
                    int origH = raw.getIconHeight();
                    if (origH > 0 && origW > 0) {
                        int newH = 120;
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
                    // Recurso no encontrado — dejamos el label vacío
                    imagenLabel.setIcon(null);
                    imagenLabel.setText("");
                }
            } catch (Exception ex) {
                imagenLabel.setIcon(null);
                imagenLabel.setText("");
            }
        }
    }

    // =====================================================
    // Screen interface
    // =====================================================

    @Override public void onShow() {}
    @Override public void onHide() { if (toUpdate != null) toUpdate.stop(); }
}