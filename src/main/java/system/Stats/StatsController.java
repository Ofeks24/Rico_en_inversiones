package system.Stats;

public class StatsController {

    private final StatsModel model;

    private final StatsPanel view;

    public StatsController(

            StatsModel model,
            StatsPanel view
    ) {

        this.model = model;
        this.view = view;

        refresh();
    }

    // =====================================================
    // REFRESH
    // =====================================================

    public void refresh() {

        view.updatePortfolio(

                model.getPortfolio(),

                model.getTotalValue()
        );
    }

    // =====================================================
    // COMPRAR
    // =====================================================

    public void buyShares(

            int empresaId,
            String nombre,
            int cantidad,
            double valor
    ) {

        model.buyShares(

                empresaId,
                nombre,
                cantidad,
                valor
        );

        refresh();
    }

    // =====================================================
    // VENDER
    // =====================================================

    public void sellShares(

            int empresaId,
            int cantidad
    ) {

        model.sellShares(
                empresaId,
                cantidad
        );

        refresh();
    }
}