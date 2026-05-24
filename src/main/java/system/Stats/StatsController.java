package system.Stats;

import system.Player;

public class StatsController {

    private final StatsModel model;
    private final StatsPanel view;
    private final Player player;

    public StatsController(StatsModel model, StatsPanel view,
                           Player player) {
        this.model  = model;
        this.view   = view;
        this.player = player;
        refresh();
    }

    public void refresh() {
        view.updatePortfolio(
            model.getPortfolio(),
            model.getTotalValue(),
            player.getDinero()      // ← dinero en tiempo real
        );
    }

    public void buyShares(int empresaId, String nombre,
                          int cantidad, double valor) {
        double coste = cantidad * valor;
        player.gastar(coste);           // ← descuenta y persiste
        model.buyShares(empresaId, nombre, cantidad, valor);
        refresh();
    }

    public void sellShares(int empresaId, int cantidad, double valor) {
        double ingreso = cantidad * valor;
        player.ingresar(ingreso);       // ← suma y persiste
        model.sellShares(empresaId, cantidad);
        refresh();
    }
}