package system.Investment;

import tools.CompanyData;
import tools.Utils;

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
        
        companies.add(
                new CompanyData(
                        "Ultra mega hiper empresa super guay de patas",
                        "Vehículos eléctricos",
                        950000,
                        0,
                        0.03
                )
        );
        
        Utils.conexion();
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
