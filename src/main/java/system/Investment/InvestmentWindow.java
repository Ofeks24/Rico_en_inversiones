package system.Investment;

import system.Player;
import system.Stats.StatsController;
import tools.MarketService;

public class InvestmentWindow {

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
