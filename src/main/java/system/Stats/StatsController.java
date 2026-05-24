package system.Stats;

import javax.swing.SwingUtilities;
import system.Player;
import tools.MarketService;

public class StatsController {

    private final StatsModel   model;
    private final StatsPanel   view;
    private final Player       player;

    public StatsController(StatsModel model, StatsPanel view,
                           Player player, MarketService market) {
        this.model  = model;
        this.view   = view;
        this.player = player;

        // Refrescar el panel en cada tick de mercado (precios en tiempo real)
        market.addListener(() ->
            SwingUtilities.invokeLater(this::refresh)
        );

        refresh();
    }

    public void refresh() {
        view.updatePortfolio(
            model.getPortfolio(),
            model.getTotalValue(),   // calculado con precios de mercado
            player.getDinero()
        );
    }

    public void buyShares(int empresaId, String nombre,
                          int cantidad, double valor) {
        double coste = cantidad * valor;
        player.gastar(coste);
        model.buyShares(empresaId, nombre, cantidad, valor);
        refresh();
    }

    public void sellShares(int empresaId, int cantidad, double valor) {
        double ingreso = cantidad * valor;
        player.ingresar(ingreso);
        model.sellShares(empresaId, cantidad);
        refresh();
    }
}