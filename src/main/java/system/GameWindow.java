package system;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import database.CompanyRepository;
import system.Investment.InvestmentWindow;
import system.News.NewsWindow;
import system.Stats.StatsController;
import system.Stats.StatsModel;
import system.Stats.StatsPanel;
import tools.AudioManager;
import tools.Clock;
import tools.CompanyData;
import tools.DesktopGridLayout;
import tools.MarketService;
import tools.NewsGenerator;
import tools.OpenAppWindow;
import tools.Screen;
import tools.TaskBarManager;
import tools.Utils;
import tools.WindowManager;


/**
 * Pantalla principal del juego. Simula un escritorio de sistema operativo
 * retro con iconos de aplicaciones, una barra de tareas y un reloj en
 * tiempo real sincronizado con el {@link Clock} del juego.
 *
 * <p>Gestiona la capa de escritorio (iconos) y la capa de ventanas flotantes
 * ({@link JDesktopPane}), e inicializa el mercado ({@link MarketService}),
 * el generador de noticias ({@link NewsGenerator}) y el subsistema de
 * estadísticas ({@link StatsController}).</p>
 *
 * <p>Implementa {@link Screen} para recibir las llamadas del ciclo de vida
 * {@code onShow}/{@code onHide} gestionadas por {@link tools.ScreenManager}.</p>
 */
public class GameWindow extends JPanel implements Screen {

    // ── Tamaños relativos a la pantalla ──────────────────
    private static final Dimension SCREEN =
        Toolkit.getDefaultToolkit().getScreenSize();
    private static final int TASKBAR_H  = scale(50,  SCREEN.height, 1080);
    private static final int WIN_W      = 800;//scale(900, SCREEN.width,  1920);
    private static final int WIN_H      = 600;//scale(500, SCREEN.height, 1080);
    private static final int ICON_SIZE  = scale(75,  SCREEN.width,  1920);
    private static final int BTN_W      = scale(110, SCREEN.width,  1920);
    private static final int BTN_H      = scale(32,  SCREEN.height, 1080);

    private JPanel        iconoSeleccionado = null;
    private Clock         time;
    private JDesktopPane  appsLayer         = new JDesktopPane();
    private WindowManager windowManager;
    private TaskBarManager taskBarManager;

    private final String ruta  = "/main/resources/logos/";
    private final MarketService  market   = new MarketService();
    private final NewsGenerator  newsGen  = new NewsGenerator();
    private       List<CompanyData> companies;
    private final Player         player   = Player.getInstance();

    StatsPanel      statsPanel;
    StatsController statsController;

    /**
     * Construye la ventana de juego completa: escritorio, iconos, barra de
     * tareas y reloj.
     *
     * <p>Carga todas las empresas de la base de datos, inicializa el mercado
     * con sus precios de apertura y registra un listener en el reloj para que
     * el mercado avance en cada tick.</p>
     *
     * @param exit {@link Runnable} que se ejecuta al pulsar el botón de inicio
     *             de la barra de tareas (vuelve al menú principal).
     * @param time Reloj del juego que controla el avance del tiempo y del mercado.
     */
    public GameWindow(Runnable exit, Clock time) {

        this.time = time;
        JPanel root = new JPanel(new GridBagLayout());
        setLayout(new BorderLayout());
        add(root, BorderLayout.CENTER);

        companies = new CompanyRepository().getAllCompanies();
        for (CompanyData c : companies)
            market.initCompany(c.getId(), c.getValorAccion());

        time.addListener(() -> market.tick(companies));

        GridBagConstraints gbc = new GridBagConstraints();

        StatsModel statsModel = new StatsModel(market);
        statsPanel     = new StatsPanel();
        statsController = new StatsController(statsModel, statsPanel, market);

        // =================================================
        // ESCRITORIO
        // =================================================

        JLayeredPane desktopContainer = new JLayeredPane();
        desktopContainer.setOpaque(true);
        desktopContainer.setBackground(new Color(85, 171, 170));

        JPanel desktopIconsLayer = new JPanel();
        desktopIconsLayer.setLayout(new DesktopGridLayout());
        desktopIconsLayer.setOpaque(false);

        appsLayer.setOpaque(false);
        appsLayer.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);

        desktopIconsLayer.add(crearIcono("Robbin Hub",
            Utils.escalarIcono(ruta + "RobbinHub(icono).png", ICON_SIZE)));
        desktopIconsLayer.add(crearIcono("Telégrafo de Montecristo",
            Utils.escalarIcono(ruta + "TelegrafoDeMontecristo(icono)(1).png", ICON_SIZE)));
        desktopIconsLayer.add(crearIcono("Stats.U",
            Utils.escalarIcono(ruta + "Stats_U(icono).png", ICON_SIZE)));
        desktopIconsLayer.add(crearIcono("Tienda",
            Utils.escalarIcono(ruta + "Doors(Closed).png", ICON_SIZE * 2 / 3)));
        desktopIconsLayer.add(crearIcono("BreakRoom",
            Utils.escalarIcono(ruta + "Doors(Closed).png", ICON_SIZE * 2 / 3)));

        desktopContainer.add(desktopIconsLayer, JLayeredPane.DEFAULT_LAYER);
        desktopContainer.add(appsLayer,         JLayeredPane.PALETTE_LAYER);

        desktopContainer.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = desktopContainer.getWidth();
                int h = desktopContainer.getHeight();
                desktopIconsLayer.setBounds(0, 0, w, h);
                appsLayer.setBounds(0, 0, w, h);
            }
        });

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 1; gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        root.add(desktopContainer, gbc);

        // =================================================
        // BARRA DE TAREAS
        // =================================================

        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(new Color(195, 199, 200));
        barra.setPreferredSize(new Dimension(0, TASKBAR_H));

        int startIconSize = scale(25, SCREEN.width, 1920);
        JButton inicio = new JButton(Utils.escalarIcono(ruta + "Doors(Closed).png", startIconSize));
        inicio.setRolloverIcon(Utils.escalarIcono(ruta + "Doors(Open).png", startIconSize));
        inicio.setPressedIcon (Utils.escalarIcono(ruta + "Doors(Open).png", startIconSize));
        inicio.addActionListener(e -> { if (exit != null) exit.run(); });
        inicio.setFocusPainted(false);
        inicio.setBorderPainted(false);
        inicio.setContentAreaFilled(false);
        inicio.setOpaque(false);
        barra.add(inicio, BorderLayout.WEST);

        JPanel centroBarra = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        centroBarra.setOpaque(false);
        centroBarra.add(crearBotonBarra("Explorador"));
        centroBarra.add(crearBotonBarra("Chrome"));
        centroBarra.add(crearBotonBarra("Editor"));

        taskBarManager = new TaskBarManager(centroBarra);
        windowManager  = new WindowManager(appsLayer, taskBarManager);

        barra.add(centroBarra, BorderLayout.CENTER);
        barra.add(crearReloj(), BorderLayout.EAST);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 1; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        root.add(barra, gbc);

        add(root);
    }

    /**
     * Devuelve el controlador de estadísticas de cartera.
     *
     * <p>Expuesto para que {@link system.MainFrame} pueda pasárselo a otras
     * pantallas (p. ej. {@link system.OptionsWindow}) sin crear dependencias
     * directas con el modelo.</p>
     *
     * @return el {@link StatsController} activo en esta partida.
     */
    public StatsController getStatsController() { return statsController; }

    /**
     * Crea un icono de escritorio clicable con imagen y etiqueta de texto.
     *
     * <p>El icono responde a los eventos de ratón:</p>
     * <ul>
     *   <li>{@code mouseEntered} — resalta el fondo en azul semitransparente.</li>
     *   <li>{@code mouseExited}  — restaura el fondo transparente (salvo si
     *       está seleccionado).</li>
     *   <li>{@code mousePressed} — marca el icono como seleccionado.</li>
     *   <li>{@code mouseClicked} (doble clic) — abre la aplicación correspondiente
     *       según el nombre del icono.</li>
     * </ul>
     *
     * @param texto  Nombre visible bajo el icono y clave que determina qué
     *               aplicación se abre al hacer doble clic.
     * @param imagen Icono gráfico escalado que se muestra en el escritorio.
     * @return       Panel compuesto (imagen + etiqueta) listo para añadir al
     *               escritorio.
     */
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
            + texto + "</div></html>");
        nombre.setForeground(Color.WHITE);
        nombre.setAlignmentX(Component.CENTER_ALIGNMENT);
        nombre.setHorizontalAlignment(SwingConstants.CENTER);

        p.add(icono);
        p.add(nombre);

        Color hover       = new Color(0, 120, 255, 80);
        Color seleccionado = new Color(0, 120, 255, 140);
        Color transparente = new Color(0, 0, 0, 0);

        p.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (iconoSeleccionado != p) p.setBackground(hover);
                repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (iconoSeleccionado != p) p.setBackground(transparente);
                repaint();
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (iconoSeleccionado != null && iconoSeleccionado != p)
                    iconoSeleccionado.setBackground(transparente);
                iconoSeleccionado = p;
                p.setBackground(seleccionado);
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() != 2) return;

                switch (texto) {
                    case "Robbin Hub" ->
                        abrirWindow(texto,
                            Utils.escalarIcono(ruta + "RobbinHub(icono).png", 25),
                            InvestmentWindow.create(statsController, market));

                    case "Telégrafo de Montecristo" ->
                        abrirWindow(texto,
                            Utils.escalarIcono(ruta + "TelegrafoDeMontecristo(icono)(1).png", 25),
                            new NewsWindow(10000, market, newsGen, companies));

                    case "Stats.U" ->
                        abrirWindow(texto,
                            Utils.escalarIcono(ruta + "Stats_U(icono).png", 25),
                            statsPanel);

                    case "Tienda" ->
                        abrirWindow(texto,
                            Utils.escalarIcono(ruta + "Doors(Closed).png", 25),
                            new JPanel());

                    case "BreakRoom" ->
                        abrirWindow(texto,
                            Utils.escalarIcono(ruta + "Doors(Closed).png", 25),
                            new JPanel());
                }
            }
        });

        return p;
    }

    /**
     * Abre una ventana flotante ({@link OpenAppWindow}) en el escritorio,
     * delegando en {@link WindowManager} para evitar duplicados y gestionar
     * el foco.
     *
     * <p>Si ya existe una ventana con el mismo identificador, se limita a
     * traerla al frente en lugar de crear una nueva instancia.</p>
     *
     * @param nombre Título de la ventana y base para generar su identificador único.
     * @param icono  Icono pequeño que se muestra en el marco de la ventana y
     *               en el botón de la barra de tareas.
     * @param window Panel de contenido que se incrusta dentro de la ventana.
     */
    private void abrirWindow(String nombre, ImageIcon icono, JPanel window) {
        windowManager.openWindow(
            Utils.convertirEnId(nombre),
            icono,
            () -> {
                OpenAppWindow win = new OpenAppWindow(
                    nombre, window, WIN_W, WIN_H, icono);
                win.setLocation(scale(200, SCREEN.width, 1920),
                                scale(100, SCREEN.height, 1080));
                return win;
            }
        );
    }

    /**
     * Crea un botón estándar para la zona central de la barra de tareas.
     *
     * @param txt Etiqueta de texto del botón.
     * @return    Botón configurado con el tamaño proporcional a la resolución.
     */
    private JButton crearBotonBarra(String txt) {
        JButton b = new JButton(txt);
        b.setFocusPainted(false);
        b.setPreferredSize(new Dimension(BTN_W, BTN_H));
        return b;
    }

    /**
     * Construye y devuelve el widget de reloj/fecha de la barra de tareas.
     *
     * <p>Muestra la hora ({@code HH:mm}) y la fecha ({@code dd/MM/yyyy}) del
     * juego, actualizándose automáticamente en cada tick del {@link Clock}
     * mediante un listener.</p>
     *
     * @return Componente {@link JPanel} con las etiquetas de hora y fecha.
     */
    private Component crearReloj() {
        JLabel hora  = new JLabel();
        JLabel fecha = new JLabel();

        int fs = scale(14, SCREEN.height, 1080);
        Font font = new Font("Dialog", Font.PLAIN, fs);
        hora.setFont(font);
        fecha.setFont(font);
        hora.setForeground(Color.BLACK);
        fecha.setForeground(Color.BLACK);
        hora.setHorizontalAlignment(SwingConstants.RIGHT);
        fecha.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel panelHora = new JPanel();
        panelHora.setLayout(new BoxLayout(panelHora, BoxLayout.Y_AXIS));
        panelHora.setBackground(new Color(195, 199, 200));
        panelHora.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createBevelBorder(1),
            new EmptyBorder(2, 6, 2, 6)
        ));
        panelHora.add(hora);
        panelHora.add(fecha);

        Runnable updateClock = () -> {
            hora.setText(String.format("%02d:%02d",
                time.getHour(), time.getMinute()));
            fecha.setText(String.format("%02d/%02d/%04d",
                time.getDate().getDay(),
                time.getDate().getMonth(),
                time.getDate().getYear()));
        };
        updateClock.run();
        time.addListener(updateClock);
        return panelHora;
    }

    /**
     * Llamado por {@link tools.ScreenManager} cuando esta pantalla pasa a ser
     * visible. Inicia la música de fondo y arranca el reloj del juego con un
     * intervalo de 2 segundos por tick.
     */
    @Override
    public void onShow() {
        AudioManager.getInstance()
            .playMusic("/main/resources/audio/music/Paso-de-Papel.wav");
        time.initClock(2000);
    }

    /**
     * Llamado por {@link tools.ScreenManager} cuando esta pantalla deja de ser
     * visible. Detiene el reloj del juego para pausar el avance del tiempo y
     * del mercado.
     */
    @Override
    public void onHide() { time.detener(); }

    /**
     * Escala un valor de referencia diseñado para una resolución base a la
     * resolución real de la pantalla.
     *
     * <p>Ejemplo: {@code scale(50, 1440, 1080)} devuelve {@code 67}, que es
     * el equivalente proporcional de 50 px en una pantalla de 1440 px de alto.</p>
     *
     * @param ref    Valor de referencia en píxeles (diseñado para {@code base}).
     * @param actual Dimensión real de la pantalla en la misma dirección.
     * @param base   Resolución de referencia para la que fue diseñado {@code ref}.
     * @return       Valor escalado redondeado al entero más próximo.
     */
    private static int scale(int ref, int actual, int base) {
        return Math.round(ref * (actual / (float) base));
    }
}