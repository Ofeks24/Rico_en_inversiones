package tools;

import java.util.*;
import javax.swing.Timer;


/**
 * Servicio central que gestiona el mercado de acciones del juego.
 * <p>
 * Mantiene el historial de velas ({@link tools.Candle}) de cada empresa,
 * los precios de apertura diaria y la lógica de variación de precios.
 * Cualquier componente que necesite reaccionar a cambios del mercado puede
 * registrarse mediante {@link #addListener(Runnable)}.
 * </p>
 *
 * <p>Responsabilidades principales:</p>
 * <ul>
 *   <li>Inicializar el historial sintético de cada empresa.</li>
 *   <li>Aplicar el tick periódico de variación aleatoria de precios mediante
 *       su propio temporizador interno, independiente del {@link tools.Clock}.</li>
 *   <li>Aplicar el impacto de un {@link tools.NewsEvent} sobre las empresas afectadas.</li>
 *   <li>Resetear los precios de apertura al inicio de cada nuevo día.</li>
 * </ul>
 */
public class MarketService {

    private final Map<Integer, List<Candle>> histories = new HashMap<>();
    private final Map<Integer, Double>       openDay   = new HashMap<>();
    private final List<Runnable>             listeners = new ArrayList<>();
    private final Random rng = new Random();

    /**
     * Controla el modo de disparo de los ticks del mercado.
     * <p>
     * {@code true}  → el mercado usa su propio {@link Timer} interno con el
     *                 intervalo configurado en {@link #tickIntervalMs}.<br>
     * {@code false} → el ticker interno permanece desactivado; los ticks
     *                 deben dispararse externamente (p. ej. desde el listener
     *                 del {@link tools.Clock} registrado en
     *                 {@link system.GameWindow}).
     * </p>
     */
    public static final boolean USE_INTERNAL_TICKER = false;

    /** Lista de empresas sobre la que opera el ticker interno. */
    private List<CompanyData> trackedCompanies;

    /** Temporizador interno independiente del reloj del juego. */
    private Timer ticker;

    /** Intervalo actual del ticker en milisegundos. */
    private int tickIntervalMs = 2000;

    /**
     * Inicializa el historial de velas y el precio de apertura de una empresa.
     * <p>
     * Genera 60 velas sintéticas con variaciones aleatorias de ±2 % por período,
     * partiendo del precio base indicado. El resultado se almacena en el mapa
     * interno de historiales indexado por el identificador de la empresa.
     * </p>
     *
     * @param id         identificador único de la empresa
     * @param precioBase precio inicial a partir del cual se construye el historial
     */
    public void initCompany(int id, double precioBase) {
        List<Candle> hist = new ArrayList<>();
        double price = precioBase;
        for (int i = 0; i < 60; i++) {
            double drift = (rng.nextDouble() * 0.04) - 0.02;
            double close = Math.max(1, price * (1 + drift));
            double body  = Math.abs(close - price);
            double high  = Math.max(price, close) + rng.nextDouble() * body * 0.5;
            double low   = Math.min(price, close) - rng.nextDouble() * body * 0.5;
            hist.add(new Candle(price, high, low, close));
            price = close;
        }
        histories.put(id, hist);
        openDay.put(id, precioBase);
    }

    // ── Control del ticker interno ────────────────────────────

    /**
     * Arranca el temporizador interno del mercado usando el intervalo
     * configurado ({@link #tickIntervalMs}).
     * <p>
     * Debe llamarse después de inicializar las empresas con
     * {@link #initCompany}. Si el ticker ya está en marcha, no hace nada.
     * </p>
     *
     * @param companies lista de empresas sobre la que operará el ticker
     */
    public void startTicker(List<CompanyData> companies) {
        this.trackedCompanies = companies;
        if (!USE_INTERNAL_TICKER) return;          // modo reloj: sin Timer propio
        if (ticker != null && ticker.isRunning()) return;
        ticker = new Timer(tickIntervalMs, e -> tick(trackedCompanies));
        ticker.start();
    }

    /**
     * Detiene el temporizador interno del mercado sin perder el estado
     * de precios ni el historial de velas.
     * <p>
     * No hace nada cuando {@link #USE_INTERNAL_TICKER} es {@code false},
     * ya que en ese modo no existe Timer interno que detener.
     * </p>
     */
    public void stopTicker() {
        if (!USE_INTERNAL_TICKER) return;
        if (ticker != null) ticker.stop();
    }

    /**
     * Cambia el intervalo entre ticks del mercado.
     * <p>
     * Si el ticker está en marcha, se reinicia inmediatamente con el
     * nuevo intervalo sin perder la referencia a las empresas.
     * </p>
     *
     * @param ms intervalo en milisegundos (mínimo 100 ms)
     */
    public void setTickInterval(int ms) {
        this.tickIntervalMs = Math.max(100, ms);
        if (ticker != null && ticker.isRunning()) {
            ticker.stop();
            ticker.setDelay(tickIntervalMs);
            ticker.setInitialDelay(tickIntervalMs);
            ticker.start();
        } else if (ticker != null) {
            ticker.setDelay(tickIntervalMs);
        }
    }

    /**
     * Devuelve el intervalo actual del ticker en milisegundos.
     *
     * @return intervalo en ms
     */
    public int getTickInterval() { return tickIntervalMs; }

    // ── Tick manual (sigue disponible para uso externo) ───────

    /**
     * Aplica un tick de variación aleatoria de precios a todas las empresas.
     * <p>
     * Cada empresa experimenta una variación aleatoria de entre −1,5 % y +1,5 %.
     * El precio resultante nunca baja de 1. Tras actualizar todos los precios,
     * se notifica a los listeners registrados.
     * </p>
     * <p>
     * Puede invocarse manualmente o es llamado automáticamente por el
     * ticker interno iniciado con {@link #startTicker}.
     * </p>
     *
     * @param companies lista de empresas cuyos precios deben actualizarse
     */
    public void tick(List<CompanyData> companies) {
        for (CompanyData c : companies) {
            double old   = c.getValorAccion();
            double drift = (rng.nextDouble() * 0.03) - 0.015;
            double nuevo = Math.max(1, old * (1 + drift));
            c.setValorAccion(nuevo);
            pushCandle(c.getId(), old, nuevo);
        }
        notify_();
    }

    /**
     * Aplica el impacto de un evento de noticias sobre las empresas afectadas.
     * <p>
     * Una empresa se ve afectada si el evento es global ({@code empresaId == -1}),
     * coincide exactamente con su identificador, o pertenece al mismo sector.
     * Las noticias sectoriales que no corresponden a la empresa exacta aplican
     * un factor de impacto reducido al 40 %.
     * </p>
     * <p>Tras actualizar los precios, se notifica a los listeners registrados.</p>
     *
     * @param news      evento de noticias con el impacto a aplicar
     * @param companies lista de empresas sobre las que evaluar el efecto
     */
    public void applyNews(NewsEvent news,
                          List<CompanyData> companies) {
        for (CompanyData c : companies) {
            boolean afecta = news.getEmpresaId() == -1          // global
                    || news.getEmpresaId() == c.getId()          // empresa exacta
                    || c.getSector() == news.getSector();        // mismo sector

            if (!afecta) continue;

            // Las noticias sectoriales que no son de la empresa exacta
            // tienen impacto reducido al 40%
            double factor = (news.getEmpresaId() == c.getId()
                             || news.getEmpresaId() == -1)
                            ? 1.0 : 0.4;

            double old   = c.getValorAccion();
            double nuevo = Math.max(1,
                    old * (1 + news.getImpacto() * factor));
            c.setValorAccion(nuevo);
            pushCandle(c.getId(), old, nuevo);
        }
        notify_();
    }

    /**
     * Registra el precio actual de cada empresa como precio de apertura del día.
     * <p>
     * Debe llamarse al inicio de cada nuevo día de juego para que los cálculos
     * de variación diaria reflejen correctamente el rendimiento de la jornada.
     * </p>
     *
     * @param companies lista de empresas cuyo precio de apertura se actualiza
     */
    public void newDay(List<CompanyData> companies) {
        for (CompanyData c : companies)
            openDay.put(c.getId(), c.getValorAccion());
    }

    /**
     * Devuelve el historial de velas de una empresa.
     *
     * @param id identificador de la empresa
     * @return lista de {@link Candle} ordenada cronológicamente;
     *         lista vacía si la empresa no ha sido inicializada
     */
    public List<Candle> getHistory(int id) {
        return histories.getOrDefault(id, Collections.emptyList());
    }

    /**
     * Devuelve el precio de apertura del día actual para una empresa.
     *
     * @param id identificador de la empresa
     * @return precio de apertura; {@code 1.0} si la empresa no se encuentra
     */
    public double getOpenDay(int id) {
        return openDay.getOrDefault(id, 1.0);
    }

    /**
     * Registra un listener que será notificado cada vez que el mercado cambie.
     * <p>
     * Los cambios que disparan la notificación son: un tick periódico
     * ({@link #tick}) y la aplicación de una noticia ({@link #applyNews}).
     * </p>
     *
     * @param r tarea a ejecutar cuando se produzca un cambio de mercado
     */
    public void addListener(Runnable r) { listeners.add(r); }

    // ── Interno ───────────────────────────────────────────────
    private void pushCandle(int id, double open, double close) {
        List<Candle> hist =
                histories.computeIfAbsent(id, k -> new ArrayList<>());
        double body = Math.abs(close - open);
        double high = Math.max(open, close)
                      + rng.nextDouble() * body * 0.5;
        double low  = Math.min(open, close)
                      - rng.nextDouble() * body * 0.5;
        hist.add(new Candle(open, high, low, close));
        if (hist.size() > 120) hist.remove(0);
    }

    private void notify_() { listeners.forEach(Runnable::run); }
}