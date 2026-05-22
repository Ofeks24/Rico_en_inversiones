package system.Investment;

import system.Stats.StatsController;

public class InvestmentWindow {

    public static InvestmentPanel create(StatsController stats) {
     
        InvestmentModel model =
                new InvestmentModel();

        InvestmentPanel view =
                new InvestmentPanel();

        new InvestmentController(
                model,
                view,
                stats
        );

        return view;
    }

}
