package system.Stats;

import tools.MarketService;

public class PortfolioEntry {

    private int    empresaId;
    private String empresaNombre;
    private int    acciones;

    // precio de compra (referencia histórica)
    private double valorCompra;

    // referencia al mercado para precio en tiempo real
    private MarketService market;

    // ── Constructor con mercado (uso normal) ──────────────
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
    public PortfolioEntry(int empresaId, String empresaNombre,
                          int acciones, double valorCompra) {
        this(empresaId, empresaNombre, acciones, valorCompra, null);
    }

    // ── Getters ───────────────────────────────────────────
    public int    getEmpresaId()     { return empresaId;     }
    public String getEmpresaNombre() { return empresaNombre; }
    public int    getAcciones()      { return acciones;      }
    public void   setAcciones(int a) { acciones = a;         }

    /** Precio actual de mercado (o precio de compra si no hay market) */
    public double getValorAccion() {
        if (market == null) return valorCompra;
        // El último close del historial es el precio actual
        java.util.List<tools.Candle> hist = market.getHistory(empresaId);
        if (hist == null || hist.isEmpty()) return valorCompra;
        return hist.get(hist.size() - 1).getClose();
    }

    /** Valor total a precio de mercado */
    public double getValorTotal() {
        return acciones * getValorAccion();
    }

    /** Ganancia/pérdida respecto al precio de compra */
    public double getGananciaPct() {
        if (valorCompra <= 0) return 0;
        return (getValorAccion() - valorCompra) / valorCompra * 100.0;
    }
}