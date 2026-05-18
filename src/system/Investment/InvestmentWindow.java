package system.Investment;

import tools.CompanyData;

import java.util.ArrayList;
import java.util.List;

public class InvestmentWindow {

    public static InvestmentPanel create() {

        List<CompanyData> companies =
                new ArrayList<>();

        companies.add(
                new CompanyData(
                        "Apple Inc.",
                        "Tecnología",
                        1200000,
                        0,
                        12
                )
        );

        companies.add(
                new CompanyData(
                        "Tesla",
                        "Vehículos eléctricos",
                        950000,
                        0,
                        0.03
                )
        );

        InvestmentModel model =
                new InvestmentModel(companies);

        InvestmentPanel view =
                new InvestmentPanel();

        new InvestmentController(
                model,
                view
        );

        return view;
    }
}
