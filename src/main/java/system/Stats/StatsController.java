package system.Stats;

import javax.swing.SwingUtilities;
import system.Player;
import tools.MarketService;

import java.util.ArrayList;
import java.util.List;

public class StatsController {

    private final StatsModel        model;
    private final StatsPanel        view;

    // Listeners opcionales para notificar a Investment u otras vistas
    private final List<Runnable>    onChangeListeners = new ArrayList<>();

    public StatsController(StatsModel model, StatsPanel view, MarketService market) {
        this.model = model;
        this.view  = view;

        market.addListener(() ->
            SwingUtilities.invokeLater(this::refresh)
        );

        refresh();
    }

    // =========================================================
    // REFRESH — actualiza Stats y notifica listeners externos
    // =========================================================

    public void refresh() {
        view.updatePortfolio(
            model.getPortfolio(),
            model.getTotalValue(),
            Player.getInstance().getDinero()
        );
        onChangeListeners.forEach(Runnable::run);
    }

    // =========================================================
    // COMPRAR / VENDER  (API unificada)
    // =========================================================

    /**
     * Operación unificada de compra/venta.
     *
     * @param empresaId  Id de la empresa.
     * @param nombre     Nombre de la empresa (solo se usa al comprar).
     * @param cantidad   Número de acciones (positivo = comprar, negativo = vender).
     * @param valor      Precio unitario actual.
     */
    public void trade(int empresaId, String nombre, int cantidad, double valor) {
        if (cantidad > 0) {
            Player.getInstance().gastar(cantidad * valor);
            model.buyShares(empresaId, nombre, cantidad, valor);
        } else if (cantidad < 0) {
            Player.getInstance().ingresar(-cantidad * valor);
            model.sellShares(empresaId, -cantidad);
        }
        refresh();
    }

    // Mantener métodos originales como delegados para no romper código existente
    public void buyShares(int empresaId, String nombre, int cantidad, double valor) {
        trade(empresaId, nombre, cantidad, valor);
    }

    public void sellShares(int empresaId, int cantidad, double valor) {
        trade(empresaId, null, -cantidad, valor);
    }

    // =========================================================
    // RESET  — limpia cartera, reinicia Player y refresca vistas
    // =========================================================

    public void reset() {
        model.clearPortfolio();
        refresh();
    }

    /** Delegado a StatsModel para que InvestmentController pueda sincronizarse. */
    public int getOwnedShares(int empresaId) {
        return model.getOwnedShares(empresaId);
    }

    // =========================================================
    // LISTENERS EXTERNOS
    // =========================================================

    /** Registra un Runnable que se ejecuta tras cada cambio (compra/venta/reset). */
    public void addOnChangeListener(Runnable r) {
        onChangeListeners.add(r);
    }
}