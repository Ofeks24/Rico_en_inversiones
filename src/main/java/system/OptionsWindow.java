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
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSeparator;

import tools.AudioManager;
import tools.RoundedPanel;
import tools.Screen;

public class OptionsWindow extends JPanel implements Screen {

    private Runnable onBack;

    // Fuentes y colores
    private static final Font  TITLE_FONT   = new Font("Segoe UI", Font.BOLD,  28);
    private static final Font  SECTION_FONT = new Font("Segoe UI", Font.BOLD,  18);
    private static final Font  NORMAL_FONT  = new Font("Segoe UI", Font.PLAIN, 17);
    private static final Color TEXT_COLOR   = new Color(220, 220, 220);
    private static final Color ACCENT       = new Color(52, 120, 246);

    public OptionsWindow(Runnable onBack) {
        setOpaque(false);
        this.onBack = onBack;
        setLayout(new GridBagLayout());

        RoundedPanel overlay = new RoundedPanel(30);
        overlay.setLayout(new BorderLayout());
        
        overlay.setPreferredSize(calcularTamanoOverlay());

	     // Redimensionar cuando cambie la ventana
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

        // Checkbox activar música
        JCheckBox musicCheck = crearCheckbox("Música activada",
                AudioManager.getInstance().isMusicEnabled());
        centro.add(musicCheck);
        centro.add(Box.createVerticalStrut(8));

        // Slider volumen música
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

        // Checkbox activar efectos
        JCheckBox sfxCheck = crearCheckbox("Efectos activados",
                AudioManager.getInstance().isSfxEnabled());
        centro.add(sfxCheck);
        centro.add(Box.createVerticalStrut(8));

        // Slider volumen efectos
        JPanel sfxVolPanel = crearSliderPanel(
                "Volumen efectos",
                (int)(AudioManager.getInstance().getSfxVolume() * 100)
        );
        JSlider sfxSlider = (JSlider) ((JPanel) sfxVolPanel
                .getComponent(1)).getComponent(0);
        centro.add(sfxVolPanel);

        overlay.add(centro, BorderLayout.CENTER);

        // ── Listeners ─────────────────────────────────────
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

        // Estado inicial de los sliders
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

    // =========================================================
    // HELPERS
    // =========================================================

    /** Línea separadora con etiqueta de sección */
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

    /** Checkbox con estilo */
    private JCheckBox crearCheckbox(String texto, boolean selected) {
        JCheckBox cb = new JCheckBox(texto, selected);
        cb.setFont(NORMAL_FONT);
        cb.setForeground(TEXT_COLOR);
        cb.setOpaque(false);
        cb.setAlignmentX(Component.LEFT_ALIGNMENT);
        return cb;
    }

    /**
     * Panel fila: label a la izquierda, slider a la derecha.
     * El slider está envuelto en un JPanel para poder extraerlo
     * con getComponent(1).getComponent(0).
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

        // Envolver en panel para acceso por índice
        JPanel sliderWrapper = new JPanel(new BorderLayout());
        sliderWrapper.setOpaque(false);
        sliderWrapper.add(slider, BorderLayout.CENTER);

        // Label de porcentaje
        JLabel pctLabel = new JLabel(valorInicial + "%");
        pctLabel.setFont(NORMAL_FONT);
        pctLabel.setForeground(TEXT_COLOR);
        pctLabel.setPreferredSize(new Dimension(36, 20));
        pctLabel.setHorizontalAlignment(JLabel.RIGHT);

        slider.addChangeListener(e ->
            pctLabel.setText(slider.getValue() + "%")
        );

        fila.add(lbl,          BorderLayout.WEST);
        fila.add(sliderWrapper, BorderLayout.CENTER);
        fila.add(pctLabel,     BorderLayout.EAST);

        return fila;
    }
    
    private Dimension calcularTamanoOverlay() {

        int ancho = (int) (getWidth() * 0.55);
        int alto  = (int) (getHeight() * 0.75);

        // Tamaños mínimos
        ancho = Math.max(ancho, 420);
        alto  = Math.max(alto, 420);

        // Tamaños máximos opcionales
        ancho = Math.min(ancho, 800);
        alto  = Math.min(alto, 700);

        return new Dimension(ancho, alto);
    }

    @Override public void onShow() {}
    @Override public void onHide() {}
}