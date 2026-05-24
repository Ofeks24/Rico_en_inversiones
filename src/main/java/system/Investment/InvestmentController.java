package system.Investment;

import javax.swing.SwingUtilities;

import system.Stats.StatsController;
import tools.AudioManager;
import tools.CompanyData;
import tools.MarketService;

public class InvestmentController {

    private final InvestmentModel model;

    private final InvestmentPanel view;
    
    private final StatsController statsController;
    
    private final MarketService market;

    public InvestmentController(
            InvestmentModel model,
            InvestmentPanel view,
            StatsController statsController,
            MarketService market          // ← nuevo parámetro
    ) {
        this.model = model;
        this.view = view;
        this.statsController = statsController;
        this.market = market;
        init();
    }

    private void init() {

        // empresa inicial
        view.setCompanies(
                model.getCompanies()
        );

        updateView();

        // dropdown
        view.getCompanyDropdown().addActionListener(e -> {
            CompanyData selected = (CompanyData) view.getCompanyDropdown().getSelectedItem();
            model.setSelectedCompany(selected);
            model.setAccionesSeleccionadas(0);
            view.setChartEmpresa(selected.getId()); // ← añadir esta línea
            updateView();
        });

        // slider
        view.getSlider().addChangeListener(e -> {

            model.setAccionesSeleccionadas(view.getSlider().getValue());

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
        view.getVenderButton().addActionListener(e -> sellShares());
        
        market.addListener(() -> SwingUtilities.invokeLater(() -> {
            CompanyData c = model.getSelectedCompany();
            if (c == null) return;
            view.updateMarketInfo(c);
            // Recalcular máximo del slider con precio actualizado
            int nuevoMax = model.getMaxAccionesComprables();
            int valorActual = view.getSlider().getValue();
            view.getSlider().setMaximum(nuevoMax);
            // Si el valor actual supera el nuevo máximo, ajustarlo
            if (valorActual > nuevoMax) {
                view.getSlider().setValue(nuevoMax);
                model.setAccionesSeleccionadas(nuevoMax);
                updateCost();
            }
        }));
        
    }

    private void updateView() {

        CompanyData c =
                model.getSelectedCompany();

        view.setCompanyInfo(c);

        view.getSlider().setMaximum(model.getMaxAccionesComprables());

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

        CompanyData company = model.getSelectedCompany();
        if (company == null) return;

        int cantidad = model.getAccionesSeleccionadas();
        if (cantidad <= 0) return;

        statsController.buyShares(
            company.getId(),
            company.getNombre(),
            cantidad,
            company.getValorAccion()
        );

        // ── Actualizar accionesPropiedad en el CompanyData ──
        company.setAccionesPropiedad(
            company.getAccionesPropiedad() + cantidad
        );

        // ── Refrescar label de propiedad y disponibles ──────
        view.refreshOwnership(company);

        // ── Resetear slider ─────────────────────────────────
        model.setAccionesSeleccionadas(0);
        view.getSlider().setMaximum(model.getMaxAccionesComprables());
        view.getSlider().setValue(0);
        updateCost();
        AudioManager.getInstance().playSfx("/audio/sfx/cash-register-sound-efect.mp3");
    }
    
    private void sellShares() {
        CompanyData company = model.getSelectedCompany();
        if (company == null) return;
        int cantidad = model.getAccionesSeleccionadas();
        if (cantidad <= 0) { 
        	AudioManager.getInstance().playSfx("/audio/sfx/wrong-beep.mp3");
        	return;
        }

        statsController.sellShares(
            company.getId(),
            cantidad,
            company.getValorAccion()   // precio actual de mercado
        );

        company.setAccionesPropiedad(
            Math.max(0, company.getAccionesPropiedad() - cantidad)
        );

        view.refreshOwnership(company);
        model.setAccionesSeleccionadas(0);
        view.getSlider().setMaximum(model.getMaxAccionesComprables());
        view.getSlider().setValue(0);
        updateCost();
        AudioManager.getInstance().playSfx("/audio/sfx/cash-register-sound-efect.mp3");
    }
}