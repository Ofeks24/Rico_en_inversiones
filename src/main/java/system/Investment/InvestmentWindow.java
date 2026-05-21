package system.Investment;



public class InvestmentWindow {

    public static InvestmentPanel create() {
     
        InvestmentModel model =
                new InvestmentModel();

        InvestmentPanel view =
                new InvestmentPanel();

        new InvestmentController(
                model,
                view
        );

        return view;
    }

}
