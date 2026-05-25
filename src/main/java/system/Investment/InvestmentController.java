package system.Investment;

import javax.swing.SwingUtilities;

import system.Player;
import system.Stats.StatsController;
import tools.AudioManager;
import tools.CompanyData;
import tools.MarketService;

public class InvestmentController {

    private final InvestmentModel  model;
    private final InvestmentPanel  view;
    private final StatsController  statsController;
    private final MarketService    market;

    private int accionesComprar = 0;
    private int accionesVender  = 0;

    public InvestmentController(
            InvestmentModel model,
            InvestmentPanel view,
            StatsController statsController,
            MarketService market
    ) {
        this.model           = model;
        this.view            = view;
        this.statsController = statsController;
        this.market          = market;
        init();
    }

    // =====================================================
    // INICIALIZACIÓN
    // =====================================================

    private void init() {

        view.setCompanies(model.getCompanies());
        updateView();

        // ── Dropdown ──────────────────────────────────────
        view.getCompanyDropdown().addActionListener(e -> {
            CompanyData sel = (CompanyData) view.getCompanyDropdown().getSelectedItem();
            if (sel == null) return;
            model.setSelectedCompany(sel);
            accionesComprar = 0;
            accionesVender  = 0;
            view.setChartEmpresa(sel.getId());
            updateView();
        });

        // ── Slider COMPRAR ────────────────────────────────
        view.getBuySlider().addChangeListener(e -> {
            accionesComprar = view.getBuySlider().getValue();
            view.getBuyField().setText(String.valueOf(accionesComprar));
            view.setBuyCost(calcBuyCost());
        });

        view.getBuyField().addActionListener(e -> syncBuyField());

        // ── Slider VENDER ─────────────────────────────────
        view.getSellSlider().addChangeListener(e -> {
            accionesVender = view.getSellSlider().getValue();
            view.getSellField().setText(String.valueOf(accionesVender));
            view.setSellIncome(calcSellIncome());
        });

        view.getSellField().addActionListener(e -> syncSellField());

        // ── Botones ───────────────────────────────────────
        view.getComprarButton().addActionListener(e -> buyShares());
        view.getVenderButton() .addActionListener(e -> sellShares());

        // ── Listener de mercado ───────────────────────────
        market.addListener(() -> SwingUtilities.invokeLater(() -> {
            CompanyData c = model.getSelectedCompany();
            if (c == null) return;
            view.updateMarketInfo(c);
            int nuevoMaxBuy = model.getMaxAccionesComprables();
            view.setBuyMax(nuevoMaxBuy);
            if (accionesComprar > nuevoMaxBuy) {
                accionesComprar = nuevoMaxBuy;
                view.setBuyValue(accionesComprar);
            }
            view.setBuyCost(calcBuyCost());
            view.setSellIncome(calcSellIncome());
        }));

        // ── Escuchar cambios de StatsController (incluye reset) ───
        statsController.addOnChangeListener(() ->
            SwingUtilities.invokeLater(this::onExternalChange)
        );
    }

    // =====================================================
    // REACCIÓN A CAMBIOS EXTERNOS (reset, etc.)
    // =====================================================

    private void onExternalChange() {
        // Recargar el estado real de la empresa seleccionada desde el modelo
        CompanyData c = model.getSelectedCompany();
        if (c == null) return;

        // Sincronizar accionesPropiedad desde el portfolio del StatsModel
        // El modelo de inversión mantiene su propia lista; actualizamos el campo
        int owned = statsController.getOwnedShares(c.getId());
        c.setAccionesPropiedad(owned);

        accionesComprar = 0;
        accionesVender  = 0;
        updateView();
    }

    // =====================================================
    // ACTUALIZAR VISTA AL CAMBIAR DE EMPRESA
    // =====================================================

    private void updateView() {
        CompanyData c = model.getSelectedCompany();
        if (c == null) return;

        view.setCompanyInfo(c);

        int maxBuy  = model.getMaxAccionesComprables();
        int maxSell = c.getAccionesPropiedad();

        view.setBuyMax(maxBuy);
        view.setSellMax(maxSell);

        view.setBuyValue(0);
        view.setSellValue(0);
        accionesComprar = 0;
        accionesVender  = 0;

        view.setBuyCost(0);
        view.setSellIncome(0);
    }

    // =====================================================
    // SINCRONIZAR CAMPOS DE TEXTO → SLIDERS
    // =====================================================

    private void syncBuyField() {
        try {
            int v = Integer.parseInt(view.getBuyField().getText().trim());
            v = Math.max(0, Math.min(v, view.getBuySlider().getMaximum()));
            accionesComprar = v;
            view.getBuySlider().setValue(v);
            view.setBuyCost(calcBuyCost());
        } catch (NumberFormatException ex) {
            view.getBuyField().setText(String.valueOf(accionesComprar));
        }
    }

    private void syncSellField() {
        try {
            int v = Integer.parseInt(view.getSellField().getText().trim());
            v = Math.max(0, Math.min(v, view.getSellSlider().getMaximum()));
            accionesVender = v;
            view.getSellSlider().setValue(v);
            view.setSellIncome(calcSellIncome());
        } catch (NumberFormatException ex) {
            view.getSellField().setText(String.valueOf(accionesVender));
        }
    }

    // =====================================================
    // CÁLCULOS
    // =====================================================

    private double calcBuyCost() {
        CompanyData c = model.getSelectedCompany();
        return (c == null) ? 0 : accionesComprar * c.getValorAccion();
    }

    private double calcSellIncome() {
        CompanyData c = model.getSelectedCompany();
        return (c == null) ? 0 : accionesVender * c.getValorAccion();
    }

    // =====================================================
    // COMPRAR  — usa trade() unificado
    // =====================================================

    private void buyShares() {
        CompanyData c = model.getSelectedCompany();
        if (c == null || accionesComprar <= 0) return;

        // trade() se encarga de descontar dinero + actualizar portfolio + refresh
        statsController.trade(
            c.getId(), c.getNombre(),
            accionesComprar,          // positivo → compra
            c.getValorAccion()
        );

        c.setAccionesPropiedad(c.getAccionesPropiedad() + accionesComprar);
        view.refreshOwnership(c);
        view.setSellMax(c.getAccionesPropiedad());

        accionesComprar = 0;
        view.setBuyMax(model.getMaxAccionesComprables());
        view.setBuyValue(0);
        view.setBuyCost(0);

        AudioManager.getInstance().playSfx(
            "/main/resources/audio/sfx/cash-register-sound-efect.wav");
    }

    // =====================================================
    // VENDER  — usa trade() unificado
    // =====================================================

    private void sellShares() {
        CompanyData c = model.getSelectedCompany();
        if (c == null || accionesVender <= 0) {
            AudioManager.getInstance().playSfx(
                "/main/resources/audio/sfx/wrong-beep.wav");
            return;
        }

        // trade() con cantidad negativa → venta
        statsController.trade(
            c.getId(), null,
            -accionesVender,
            c.getValorAccion()
        );

        c.setAccionesPropiedad(
            Math.max(0, c.getAccionesPropiedad() - accionesVender));
        view.refreshOwnership(c);

        view.setSellMax(c.getAccionesPropiedad());
        view.setBuyMax(model.getMaxAccionesComprables());

        accionesVender = 0;
        view.setSellValue(0);
        view.setSellIncome(0);

        AudioManager.getInstance().playSfx(
            "/main/resources/audio/sfx/cash-register-sound-efect.wav");
    }
}