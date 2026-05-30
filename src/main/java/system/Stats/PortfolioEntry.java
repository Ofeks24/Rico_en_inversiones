package system.Stats;

import tools.MarketService;


/**
 * Representa una posición abierta en la cartera del jugador:
 * cuántas acciones posee de una empresa concreta y a qué precio las compró.
 * <p>
 * El valor actual se obtiene en tiempo real consultando el historial de
 * {@link tools.MarketService}, lo que permite mostrar la ganancia o pérdida
 * respecto al precio de compra original.
 * </p>
 */
public class PortfolioEntry {

    private int    empresaId;
    private String empresaNombre;
    private int    acciones;

    // precio de compra (referencia histórica)
    private double valorCompra;

    // referencia al mercado para precio en tiempo real
    private MarketService market;

    // ── Constructor con mercado (uso normal) ──────────────
    /**
     * Construye una entrada de cartera con referencia al mercado en tiempo real.
     *
     * @param empresaId     identificador único de la empresa
     * @param empresaNombre nombre visible de la empresa
     * @param acciones      número de acciones poseídas
     * @param valorCompra   precio unitario en el momento de la compra
     * @param market        servicio de mercado para consultar el precio actual
     */
    public PortfolioEntry(int empresaId, String empresaNombre,
                          int acciones, double valorCompra,
                          MarketService market) {
        this.empresaId     = empresaId;
        this.empresaNombre = empresaNombre;
        this.acciones      = acciones;
        this.valorCompra   = valorCompra;
        this.market        = market;
    }

    // ── Constructor sin mercado (compatibilidad) ──────────
    /**
     * Constructor de compatibilidad sin referencia al mercado.
     * Si se usa este constructor, {@link #getValorAccion()} devolverá
     * el precio de compra en lugar del precio actual de mercado.
     *
     * @param empresaId     identificador único de la empresa
     * @param empresaNombre nombre visible de la empresa
     * @param acciones      número de acciones poseídas
     * @param valorCompra   precio unitario en el momento de la compra
     */
    public PortfolioEntry(int empresaId, String empresaNombre,
                          int acciones, double valorCompra) {
        this(empresaId, empresaNombre, acciones, valorCompra, null);
    }

    /**
     * Devuelve el identificador único de la empresa.
     *
     * @return id de la empresa
     */
    public int    getEmpresaId()     { return empresaId;     }

    /**
     * Devuelve el nombre visible de la empresa.
     *
     * @return nombre de la empresa
     */
    public String getEmpresaNombre() { return empresaNombre; }

    /**
     * Devuelve el número de acciones poseídas actualmente.
     *
     * @return número de acciones
     */
    public int    getAcciones()      { return acciones;      }

    /**
     * Actualiza el número de acciones de esta posición.
     *
     * @param a nuevo número de acciones
     */
    public void   setAcciones(int a) { acciones = a;         }

    /**
     * Devuelve el precio unitario actual de la acción consultando el mercado.
     * <p>
     * Si no hay un {@link MarketService} disponible o su historial está vacío,
     * devuelve el precio de compra como valor de reserva.
     * </p>
     *
     * @return precio actual por acción
     */
    public double getValorAccion() {
        if (market == null) return valorCompra;
        // El último close del historial es el precio actual
        java.util.List<tools.Candle> hist = market.getHistory(empresaId);
        if (hist == null || hist.isEmpty()) return valorCompra;
        return hist.get(hist.size() - 1).getClose();
    }

    /**
     * Devuelve el valor total de la posición al precio actual de mercado.
     * <p>
     * Equivale a {@code acciones × getValorAccion()}.
     * </p>
     *
     * @return valor total en moneda del juego (₲)
     */
    public double getValorTotal() {
        return acciones * getValorAccion();
    }

    /**
     * Calcula la ganancia o pérdida porcentual respecto al precio de compra.
     * <p>
     * Un resultado positivo indica beneficio; negativo, pérdida.
     * Devuelve {@code 0} si el precio de compra fue cero o negativo.
     * </p>
     *
     * @return porcentaje de ganancia/pérdida (ej. {@code 5.3} = +5,3 %)
     */
    public double getGananciaPct() {
        if (valorCompra <= 0) return 0;
        return (getValorAccion() - valorCompra) / valorCompra * 100.0;
    }
}