
package system.Investment;

import javax.swing.SwingUtilities;

import system.Stats.StatsController;
import tools.AudioManager;
import tools.CompanyData;
import tools.MarketService;

public class InvestmentController {

    private final InvestmentModel  model;
    private final InvestmentPanel  view;
    private final StatsController  statsController;
    private final MarketService    market;

    // Cantidad seleccionada en cada slider (independientes)
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

        // ── Listener de mercado (precio en tiempo real) ───
        market.addListener(() -> SwingUtilities.invokeLater(() -> {
            CompanyData c = model.getSelectedCompany();
            if (c == null) return;

            // Actualizar labels de precio y disponibles
            view.updateMarketInfo(c);

            // Recalcular máximo de compra (precio cambió → dinero alcanza diferente cantidad)
            int nuevoMaxBuy = model.getMaxAccionesComprables();
            view.setBuyMax(nuevoMaxBuy);
            if (accionesComprar > nuevoMaxBuy) {
                accionesComprar = nuevoMaxBuy;
                view.setBuyValue(accionesComprar);
            }
            view.setBuyCost(calcBuyCost());

            // El máximo de venta no cambia (depende de acciones propias, no del precio)
            view.setSellIncome(calcSellIncome());
        }));
    }

    // =====================================================
    // ACTUALIZAR VISTA AL CAMBIAR DE EMPRESA
    // =====================================================

    private void updateView() {
        CompanyData c = model.getSelectedCompany();
        if (c == null) return;

        view.setCompanyInfo(c);

        // Máximos de cada slider
        int maxBuy  = model.getMaxAccionesComprables();
        int maxSell = c.getAccionesPropiedad();

        view.setBuyMax(maxBuy);
        view.setSellMax(maxSell);

        // Resetear valores
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
        if (c == null) return 0;
        return accionesComprar * c.getValorAccion();
    }

    private double calcSellIncome() {
        CompanyData c = model.getSelectedCompany();
        if (c == null) return 0;
        return accionesVender * c.getValorAccion();
    }

    // =====================================================
    // COMPRAR
    // =====================================================

    private void buyShares() {
        CompanyData c = model.getSelectedCompany();
        if (c == null || accionesComprar <= 0) return;

        statsController.buyShares(
            c.getId(), c.getNombre(),
            accionesComprar, c.getValorAccion()
        );

        // Actualizar estado local de la empresa
        c.setAccionesPropiedad(c.getAccionesPropiedad() + accionesComprar);
        view.refreshOwnership(c);

        // El máximo de venta crece
        view.setSellMax(c.getAccionesPropiedad());

        // Resetear slider de compra
        accionesComprar = 0;
        view.setBuyMax(model.getMaxAccionesComprables());
        view.setBuyValue(0);
        view.setBuyCost(0);

        AudioManager.getInstance().playSfx("/main/resources/audio/sfx/cash-register-sound-efect.wav");
    }

    // =====================================================
    // VENDER
    // =====================================================

    private void sellShares() {
        CompanyData c = model.getSelectedCompany();
        if (c == null || accionesVender <= 0) {
            AudioManager.getInstance().playSfx("/main/resources/audio/sfx/wrong-beep.wav");
            return;
        }

        statsController.sellShares(
            c.getId(), accionesVender, c.getValorAccion()
        );

        // Actualizar estado local de la empresa
        c.setAccionesPropiedad(
            Math.max(0, c.getAccionesPropiedad() - accionesVender)
        );
        view.refreshOwnership(c);

        // El máximo de venta decrece; el de compra puede subir (más dinero)
        view.setSellMax(c.getAccionesPropiedad());
        view.setBuyMax(model.getMaxAccionesComprables());

        // Resetear slider de venta
        accionesVender = 0;
        view.setSellValue(0);
        view.setSellIncome(0);

        AudioManager.getInstance().playSfx("/main/resources/audio/sfx/cash-register-sound-efect.wav");
    }
}