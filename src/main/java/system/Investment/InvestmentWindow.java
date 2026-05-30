package system.Investment;

import system.Stats.StatsController;
import tools.MarketService;


/**
 * Factoría estática que ensambla la ventana de inversión (Robbin Hub).
 *
 * <p>Crea el modelo ({@link InvestmentModel}), la vista
 * ({@link InvestmentPanel}) y el controlador ({@link InvestmentController}),
 * los conecta entre sí y devuelve el panel listo para ser incrustado en un
 * {@link tools.OpenAppWindow}.</p>
 *
 * <p>Uso típico:</p>
 * <pre>{@code
 * InvestmentPanel panel = InvestmentWindow.create(statsController, market);
 * }</pre>
 */
public class InvestmentWindow {

    /**
     * Construye y devuelve el panel de inversión completamente inicializado.
     *
     * <p>Instancia el modelo (cargando empresas desde la BD), crea la vista
     * con la empresa inicial preseleccionada y conecta el controlador.
     * Si la lista de empresas está vacía se usa {@code -1} como id inicial.</p>
     *
     * @param stats  controlador de estadísticas necesario para ejecutar
     *               operaciones de compra/venta y escuchar resets.
     * @param market servicio de mercado para actualizaciones de precio en
     *               tiempo real y el gráfico de velas.
     * @return {@link InvestmentPanel} listo para mostrarse en pantalla.
     */
	public static InvestmentPanel create(StatsController stats, MarketService market) {
		
		InvestmentModel model = new InvestmentModel();
        
        int idInicial = model.getCompanies().isEmpty() ? -1 : model.getCompanies().get(0).getId();

        InvestmentPanel view = new InvestmentPanel(market, idInicial);

        new InvestmentController(
                model,
                view,
                stats,
                market
        );

        return view;
    }

}
