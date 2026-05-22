package system.Investment;

import system.Stats.StatsController;
import tools.CompanyData;

public class InvestmentController {

    private final InvestmentModel model;

    private final InvestmentPanel view;
    
    private final StatsController statsController;

    public InvestmentController(
            InvestmentModel model,
            InvestmentPanel view,
            StatsController statsController
    ) {

        this.model = model;
        this.view = view;
		this.statsController = null;

        init();
    }

    private void init() {

        // empresa inicial
        view.setCompanies(
                model.getCompanies()
        );

        updateView();

        // dropdown
        view.getCompanyDropdown()
                .addActionListener(e -> {

            CompanyData selected =
                    (CompanyData)
                    view.getCompanyDropdown()
                    .getSelectedItem();

            model.setSelectedCompany(selected);

            model.setAccionesSeleccionadas(0);

            updateView();
        });

        // slider
        view.getSlider()
                .addChangeListener(e -> {

            model.setAccionesSeleccionadas(
                    view.getSlider().getValue()
            );

            updateCost();
        });

        // textbox
        view.getAccionesField()
                .addActionListener(e -> {

            syncTextToSlider();
        });
        
        view.getComprarButton().addActionListener(e -> {
		    buyShares();
		});
        
    }

    private void updateView() {

        CompanyData c =
                model.getSelectedCompany();

        view.setCompanyInfo(c);

        view.getSlider().setMaximum(
                c.getAccionesMercado()
        );

        view.getSlider().setValue(0);

        updateCost();
    }

    private void updateCost() {

        view.setSelectedActions(
                model.getAccionesSeleccionadas()
        );

        view.setCost(
                model.getCosteTotal()
        );
    }

    private void syncTextToSlider() {

        try {

            int value = Integer.parseInt(
                    view.getAccionesField().getText()
            );

            value = Math.max(0, value);

            value = Math.min(
                    view.getSlider().getMaximum(),
                    value
            );

            view.getSlider().setValue(value);

        } catch (NumberFormatException ex) {

            view.setSelectedActions(
                    model.getAccionesSeleccionadas()
            );
        }
    }
    
    private void buyShares() {

        CompanyData company =
                model.getSelectedCompany();

        if (company == null) return;

        int cantidad =
                model.getAccionesSeleccionadas();

        if (cantidad <= 0) return;

        // =========================
        // ENVIAR A STATS
        // =========================

        statsController.buyShares(

                company.getId(),

                company.getNombre(),

                cantidad,

                company.getValorAccion()
        );
    }
}