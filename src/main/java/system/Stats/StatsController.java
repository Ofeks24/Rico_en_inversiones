package system.Stats;

import javax.swing.SwingUtilities;
import system.Player;
import tools.MarketService;

import java.util.ArrayList;
import java.util.List;


/**
 * Controlador del subsistema de estadísticas de cartera (la "C" del MVC).
 * <p>
 * Actúa como intermediario entre {@link StatsModel} y {@link StatsPanel},
 * y expone la API de compra/venta unificada que usan otros controladores
 * (como {@link system.Investment.InvestmentController}).
 * También notifica a listeners externos cada vez que cambia el estado
 * de la cartera, lo que permite mantener sincronizadas otras vistas.
 * </p>
 */
public class StatsController {

    private final StatsModel        model;
    private final StatsPanel        view;

    // Listeners opcionales para notificar a Investment u otras vistas
    private final List<Runnable>    onChangeListeners = new ArrayList<>();

    /**
     * Construye el controlador, registra un listener de mercado para
     * refrescar automáticamente la vista en cada tick, y realiza el
     * primer refresco inicial.
     *
     * @param model  modelo de datos de la cartera
     * @param view   panel de vista que muestra la cartera
     * @param market servicio de mercado cuya actualización periódica
     *               dispara el refresco de la vista
     */
    public StatsController(StatsModel model, StatsPanel view, MarketService market) {
        this.model = model;
        this.view  = view;

        market.addListener(() ->
            SwingUtilities.invokeLater(this::refresh)
        );

        //refresh();
    }

    // =========================================================
    // REFRESH — actualiza Stats y notifica listeners externos
    // =========================================================

    /**
     * Refresca la vista con el estado actual del modelo y notifica a todos
     * los listeners registrados.
     * <p>
     * Debe ejecutarse en el EDT; si se llama desde un listener de mercado,
     * asegúrate de envolverlo en {@link SwingUtilities#invokeLater}.
     * </p>
     */
    public void refresh() {
        view.updatePortfolio(
            model.getPortfolio(),
            model.getTotalValue(),
            Player.getInstance().getDinero()
        );
        onChangeListeners.forEach(Runnable::run);
    }

    /**
     * Operación unificada de compra o venta de acciones.
     * <p>
     * Dependiendo del signo de {@code cantidad}:
     * <ul>
     *   <li><b>Positivo</b>: descuenta el coste del saldo del jugador
     *       y registra la compra en el modelo.</li>
     *   <li><b>Negativo</b>: ingresa el importe al jugador y registra
     *       la venta en el modelo.</li>
     *   <li><b>Cero</b>: no realiza ninguna operación.</li>
     * </ul>
     * Tras la operación llama a {@link #refresh()} para actualizar la vista
     * y notificar a los listeners.
     * </p>
     *
     * @param empresaId id de la empresa sobre la que se opera
     * @param nombre    nombre de la empresa (sólo necesario al comprar,
     *                  puede ser {@code null} al vender)
     * @param cantidad  número de acciones; positivo = compra, negativo = venta
     * @param valor     precio unitario actual de la acción
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

    /**
     * Delegado de compra sobre {@link #trade} para mantener compatibilidad
     * con código anterior.
     *
     * @param empresaId id de la empresa
     * @param nombre    nombre de la empresa
     * @param cantidad  número de acciones a comprar (positivo)
     * @param valor     precio unitario actual
     */
    public void buyShares(int empresaId, String nombre, int cantidad, double valor) {
        trade(empresaId, nombre, cantidad, valor);
    }

    /**
     * Delegado de venta sobre {@link #trade} para mantener compatibilidad
     * con código anterior.
     *
     * @param empresaId id de la empresa
     * @param cantidad  número de acciones a vender (positivo)
     * @param valor     precio unitario actual
     */
    public void sellShares(int empresaId, int cantidad, double valor) {
        trade(empresaId, null, -cantidad, valor);
    }
    
    /**
     * Resetea la cartera del jugador: vacía el modelo, actualiza la vista
     * y notifica a los listeners.
     * <p>
     * Se invoca desde {@link system.OptionsWindow} cuando el jugador
     * confirma el borrado de datos de partida.
     * </p>
     */
    public void reset() {
        model.clearPortfolio();
        refresh();
    }

    /**
     * Devuelve el número de acciones poseídas de una empresa concreta,
     * delegando en {@link StatsModel#getOwnedShares(int)}.
     * <p>
     * Permite a {@link system.Investment.InvestmentController} sincronizar
     * su estado tras un reset externo sin acceder directamente al modelo.
     * </p>
     *
     * @param empresaId id de la empresa
     * @return número de acciones en cartera; {@code 0} si no hay posición abierta
     */
    public int getOwnedShares(int empresaId) {
        return model.getOwnedShares(empresaId);
    }

    /**
     * Registra un listener que se ejecutará en el EDT tras cada compra,
     * venta o reset.
     * <p>
     * Úsalo para mantener sincronizadas vistas externas como
     * {@link system.Investment.InvestmentPanel}.
     * </p>
     *
     * @param r el {@link Runnable} a ejecutar en cada cambio
     */
    public void addOnChangeListener(Runnable r) {
        onChangeListeners.add(r);
    }
}