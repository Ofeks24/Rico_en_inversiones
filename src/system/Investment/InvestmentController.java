package system.Investment;

import tools.CompanyData;

public class InvestmentController {

    private final InvestmentModel model;

    private final InvestmentPanel view;

    public InvestmentController(
            InvestmentModel model,
            InvestmentPanel view
    ) {

        this.model = model;
        this.view = view;

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
}