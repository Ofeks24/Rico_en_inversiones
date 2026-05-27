package system;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;

import database.CompanyRepository;
import tools.AudioManager;
import tools.Clock;
import tools.RoundedPanel;
import tools.Screen;


/**
 * Pantalla de opciones del juego. Permite al jugador ajustar el audio
 * (música y efectos de sonido), guardar manualmente la partida y
 * resetear todos los datos de progreso.
 *
 * <p>Se presenta como un panel superpuesto centrado ({@link RoundedPanel})
 * sobre el fondo animado del juego. Los controles de volumen están
 * vinculados en tiempo real al {@link AudioManager} singleton.</p>
 *
 * <p>Implementa {@link Screen} para el ciclo de vida de
 * {@link tools.ScreenManager}, aunque sus callbacks están vacíos.</p>
 */
public class OptionsWindow extends JPanel implements Screen {

    private Runnable onBack;

    // Valores de partida nueva
    private static final double DINERO_INICIAL  = 100.0;
    private static final int    ANYO_INICIAL    = 1996;
    private static final int    MES_INICIAL     = 6;
    private static final int    DIA_INICIAL     = 1;
    private static final int    HORA_INICIAL    = 8;
    private static final int    MINUTO_INICIAL  = 0;

    // Fuentes y colores
    private static final Font  TITLE_FONT   = new Font("Segoe UI", Font.BOLD,  28);
    private static final Font  SECTION_FONT = new Font("Segoe UI", Font.BOLD,  18);
    private static final Font  NORMAL_FONT  = new Font("Segoe UI", Font.PLAIN, 17);
    private static final Color TEXT_COLOR   = new Color(220, 220, 220);
    private static final Color ACCENT       = new Color(52, 120, 246);
    private static final Color DANGER       = new Color(200, 50, 50);

    /**
     * Construye la pantalla de opciones con todos sus controles.
     *
     * <p>Secciones incluidas:</p>
     * <ul>
     *   <li><b>Música</b>: checkbox de activación y slider de volumen.</li>
     *   <li><b>Efectos de sonido</b>: checkbox de activación y slider de volumen.</li>
     *   <li><b>Datos de partida</b>: botón de guardado rápido y botón de reset.</li>
     * </ul>
     *
     * @param onBack  {@link Runnable} ejecutado al pulsar "Volver"; normalmente
     *                navega al menú principal.
     * @param onReset {@link Runnable} adicional de reset (actualmente delega en
     *                {@link system.Stats.StatsController#reset()}).
     * @param clock   Reloj del juego; necesario para guardar la fecha/hora
     *                actual y para restaurarla al resetear la partida.
     */
    public OptionsWindow(Runnable onBack, Runnable onReset, Clock clock) {
        setOpaque(false);
        this.onBack = onBack;
        setLayout(new GridBagLayout());

        RoundedPanel overlay = new RoundedPanel(30);
        overlay.setLayout(new BorderLayout());
        overlay.setPreferredSize(calcularTamanoOverlay());

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                overlay.setPreferredSize(calcularTamanoOverlay());
                revalidate();
            }
        });

        // ── Título ────────────────────────────────────────
        JLabel titulo = new JLabel("Opciones", JLabel.CENTER);
        titulo.setFont(TITLE_FONT);
        titulo.setForeground(TEXT_COLOR);
        titulo.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        overlay.add(titulo, BorderLayout.NORTH);

        // ── Contenido central ─────────────────────────────
        JPanel centro = new JPanel();
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));
        centro.setOpaque(false);
        centro.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        // ── Sección MÚSICA ────────────────────────────────
        centro.add(crearSeparador("MÚSICA"));
        centro.add(Box.createVerticalStrut(10));

        JCheckBox musicCheck = crearCheckbox("Música activada",
                AudioManager.getInstance().isMusicEnabled());
        centro.add(musicCheck);
        centro.add(Box.createVerticalStrut(8));

        JPanel musicVolPanel = crearSliderPanel(
                "Volumen música",
                (int)(AudioManager.getInstance().getMusicVolume() * 100)
        );
        JSlider musicSlider = (JSlider) ((JPanel) musicVolPanel
                .getComponent(1)).getComponent(0);
        centro.add(musicVolPanel);
        centro.add(Box.createVerticalStrut(16));

        // ── Sección EFECTOS ───────────────────────────────
        centro.add(crearSeparador("EFECTOS DE SONIDO"));
        centro.add(Box.createVerticalStrut(10));

        JCheckBox sfxCheck = crearCheckbox("Efectos activados",
                AudioManager.getInstance().isSfxEnabled());
        centro.add(sfxCheck);
        centro.add(Box.createVerticalStrut(8));

        JPanel sfxVolPanel = crearSliderPanel(
                "Volumen efectos",
                (int)(AudioManager.getInstance().getSfxVolume() * 100)
        );
        JSlider sfxSlider = (JSlider) ((JPanel) sfxVolPanel
                .getComponent(1)).getComponent(0);
        centro.add(sfxVolPanel);
        centro.add(Box.createVerticalStrut(16));

        // ── Sección DATOS ─────────────────────────────────
        centro.add(crearSeparador("DATOS DE PARTIDA"));
        centro.add(Box.createVerticalStrut(10));

        JButton guardarBtn = crearBotonAccion(
                "Guardar partida ahora", ACCENT);
        guardarBtn.addActionListener(e ->
            guardarPartidaActual(clock)
        );
        centro.add(guardarBtn);
        centro.add(Box.createVerticalStrut(10));

        JButton resetBtn = crearBotonAccion(
                "Borrar todos los datos", DANGER);
        resetBtn.addActionListener(e ->
            confirmarYResetear(clock)
        );
        centro.add(resetBtn);

        overlay.add(centro, BorderLayout.CENTER);

        // ── Listeners audio ───────────────────────────────
        musicCheck.addActionListener(e -> {
            AudioManager.getInstance()
                    .setMusicEnabled(musicCheck.isSelected());
            musicSlider.setEnabled(musicCheck.isSelected());
        });

        musicSlider.addChangeListener(e ->
            AudioManager.getInstance()
                    .setMusicVolume(musicSlider.getValue() / 100f)
        );

        sfxCheck.addActionListener(e -> {
            AudioManager.getInstance()
                    .setSfxEnabled(sfxCheck.isSelected());
            sfxSlider.setEnabled(sfxCheck.isSelected());
        });

        sfxSlider.addChangeListener(e ->
            AudioManager.getInstance()
                    .setSfxVolume(sfxSlider.getValue() / 100f)
        );

        musicSlider.setEnabled(AudioManager.getInstance().isMusicEnabled());
        sfxSlider.setEnabled(AudioManager.getInstance().isSfxEnabled());

        // ── Botón volver ──────────────────────────────────
        JButton volver = new JButton("Volver");
        volver.setFont(NORMAL_FONT);
        volver.setFocusPainted(false);
        volver.setBackground(ACCENT);
        volver.setForeground(Color.WHITE);
        volver.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        volver.addActionListener(e -> {
            if (this.onBack != null) this.onBack.run();
        });

        JPanel surPanel = new JPanel(new BorderLayout());
        surPanel.setOpaque(false);
        surPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 20, 30));
        surPanel.add(volver, BorderLayout.CENTER);

        overlay.add(surPanel, BorderLayout.SOUTH);

        add(overlay);
    }

    /**
     * Persiste el estado actual del jugador (dinero, fecha y hora del juego)
     * en la base de datos llamando a
     * {@link database.CompanyRepository#guardarPartida(double, Clock)}.
     * Muestra un diálogo informativo al completarse.
     *
     * @param clock Reloj del juego del que se obtienen la fecha y la hora
     *              actuales para guardarlas.
     */
    private void guardarPartidaActual(Clock clock) {
        double dinero = Player.getInstance().getDinero();
        new CompanyRepository().guardarPartida(dinero, clock);

        JOptionPane.showMessageDialog(
            SwingUtilities.getWindowAncestor(this),
            "Partida guardada correctamente.",
            "Guardado",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Muestra un diálogo de confirmación y, si el jugador acepta, ejecuta
     * un reset completo de la partida:
     * <ol>
     *   <li>Borra todas las posiciones de la cartera en BD
     *       ({@link database.CompanyRepository#resetearPartida}).</li>
     *   <li>Reinicia el singleton {@link Player} con el dinero inicial.</li>
     *   <li>Restaura la fecha y hora del reloj a los valores de inicio.</li>
     * </ol>
     * Muestra un diálogo informativo al finalizar.
     *
     * @param clock Reloj del juego cuya fecha y hora se restauran al estado
     *              inicial tras el reset.
     */
    private void confirmarYResetear(Clock clock) {
        int respuesta = JOptionPane.showConfirmDialog(
            SwingUtilities.getWindowAncestor(this),
            "¿Seguro que quieres borrar todos los datos?\n"
                + "Se perderán todas las acciones y el progreso.",
            "Borrar datos",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (respuesta != JOptionPane.YES_OPTION) return;

        // 1. Persistir el reset en BD
        new CompanyRepository().resetearPartida(
            DINERO_INICIAL,
            ANYO_INICIAL, MES_INICIAL, DIA_INICIAL,
            HORA_INICIAL, MINUTO_INICIAL
        );
        

        // 2. Reiniciar el singleton de Player para que lea el nuevo dinero
        Player.reset(DINERO_INICIAL);

        // 3. Restaurar el reloj en memoria
        clock.setHour(HORA_INICIAL);
        clock.setMinute(MINUTO_INICIAL);
        clock.setDate(new tools.Date(DIA_INICIAL, MES_INICIAL, ANYO_INICIAL));

        JOptionPane.showMessageDialog(
            SwingUtilities.getWindowAncestor(this),
            "Datos borrados. La partida ha vuelto al estado inicial.",
            "Datos borrados",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Crea un separador visual de sección con una etiqueta de texto en color
     * de acento y una línea horizontal.
     *
     * @param texto Título de la sección que aparece a la izquierda del separador.
     * @return      Panel compuesto (etiqueta + línea) listo para añadir al layout.
     */
    private JPanel crearSeparador(String texto) {
        JPanel p = new JPanel(new BorderLayout(8, 0));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        JLabel lbl = new JLabel(texto);
        lbl.setFont(SECTION_FONT);
        lbl.setForeground(ACCENT);

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(80, 80, 80));

        p.add(lbl, BorderLayout.WEST);
        p.add(sep, BorderLayout.CENTER);
        return p;
    }

    /**
     * Crea un {@link JCheckBox} con el estilo visual del panel de opciones.
     *
     * @param texto    Etiqueta descriptiva del checkbox.
     * @param selected Estado inicial (marcado/desmarcado).
     * @return         Checkbox configurado con fuente y color corporativos.
     */
    private JCheckBox crearCheckbox(String texto, boolean selected) {
        JCheckBox cb = new JCheckBox(texto, selected);
        cb.setFont(NORMAL_FONT);
        cb.setForeground(TEXT_COLOR);
        cb.setOpaque(false);
        cb.setAlignmentX(Component.LEFT_ALIGNMENT);
        return cb;
    }
    
    /**
     * Crea una fila de control de volumen formada por una etiqueta, un
     * {@link JSlider} y una etiqueta de porcentaje que se actualiza en
     * tiempo real al mover el slider.
     *
     * @param etiqueta     Texto descriptivo que aparece a la izquierda del slider.
     * @param valorInicial Valor inicial del slider (0–100).
     * @return             Panel con la fila de control lista para añadir al layout.
     */
    private JPanel crearSliderPanel(String etiqueta, int valorInicial) {
        JPanel fila = new JPanel(new BorderLayout(10, 0));
        fila.setOpaque(false);
        fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        fila.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(NORMAL_FONT);
        lbl.setForeground(TEXT_COLOR);
        lbl.setPreferredSize(new Dimension(160, 24));

        JSlider slider = new JSlider(0, 100, valorInicial);
        slider.setOpaque(false);
        slider.setPaintTicks(false);
        slider.setPaintLabels(false);
        slider.setForeground(ACCENT);

        JPanel sliderWrapper = new JPanel(new BorderLayout());
        sliderWrapper.setOpaque(false);
        sliderWrapper.add(slider, BorderLayout.CENTER);

        JLabel pctLabel = new JLabel(valorInicial + "%");
        pctLabel.setFont(NORMAL_FONT);
        pctLabel.setForeground(TEXT_COLOR);
        pctLabel.setPreferredSize(new Dimension(36, 20));
        pctLabel.setHorizontalAlignment(JLabel.RIGHT);

        slider.addChangeListener(e ->
            pctLabel.setText(slider.getValue() + "%")
        );

        fila.add(lbl,           BorderLayout.WEST);
        fila.add(sliderWrapper, BorderLayout.CENTER);
        fila.add(pctLabel,      BorderLayout.EAST);

        return fila;
    }

    /**
     * Crea un botón de acción genérico con un color de fondo personalizado,
     * texto blanco y ancho máximo ilimitado (para que ocupe todo el ancho
     * disponible en un {@link BoxLayout}).
     *
     * @param texto Etiqueta visible del botón.
     * @param color Color de fondo del botón (p. ej. azul de acento o rojo de peligro).
     * @return      {@link JButton} configurado y listo para añadir.
     */
    private JButton crearBotonAccion(String texto, Color color) {
        JButton b = new JButton(texto);
        b.setFont(NORMAL_FONT);
        b.setFocusPainted(false);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        return b;
    }

    /**
     * Calcula el tamaño preferido del overlay central en función del tamaño
     * actual del panel contenedor, manteniéndolo entre unos valores mínimos
     * y máximos definidos.
     *
     * @return {@link Dimension} con el ancho y alto preferidos del overlay.
     */
    private Dimension calcularTamanoOverlay() {
        int ancho = (int) (getWidth()  * 0.55);
        int alto  = (int) (getHeight() * 0.75);

        ancho = Math.max(ancho, 420);
        alto  = Math.max(alto,  480);   // un poco más alto por la nueva sección
        ancho = Math.min(ancho, 800);
        alto  = Math.min(alto,  780);

        return new Dimension(ancho, alto);
    }

    /** No realiza ninguna acción al mostrarse esta pantalla. */
    @Override public void onShow() {}
    /** No realiza ninguna acción al mostrarse esta pantalla. */
    @Override public void onHide() {}
}