package system.Investment;

import javax.swing.SwingUtilities;

import system.Player;
import system.Stats.StatsController;
import tools.AudioManager;
import tools.CompanyData;
import tools.MarketService;


/**
 * Controlador MVC de la ventana de inversión (Robbin Hub).
 *
 * <p>Coordina la comunicación entre {@link InvestmentModel} e
 * {@link InvestmentPanel}, gestionando las acciones del usuario
 * (cambio de empresa, movimiento de sliders, compra y venta de acciones)
 * y manteniéndose sincronizado con el {@link MarketService} y el
 * {@link StatsController} para reflejar cambios externos como variaciones
 * de precio de mercado o resets de partida.</p>
 */
public class InvestmentController {

    private final InvestmentModel  model;
    private final InvestmentPanel  view;
    private final StatsController  statsController;
    private final MarketService    market;

    private int accionesComprar = 0;
    private int accionesVender  = 0;

    /**
     * Crea el controlador y conecta todos los listeners de la vista.
     *
     * @param model           modelo de datos con la lista de empresas y la
     *                        empresa seleccionada actualmente.
     * @param view            panel de la UI al que este controlador enviará
     *                        actualizaciones.
     * @param statsController controlador de estadísticas usado para ejecutar
     *                        operaciones de compra/venta y recibir
     *                        notificaciones de cambio externo.
     * @param market          servicio de mercado que emite ticks de precio.
     */
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

    /**
     * Inicializa todos los listeners de la vista: dropdown de empresa,
     * sliders de compra/venta, campos de texto y botones de acción.
     * También registra los listeners de mercado y de cambio externo.
     */
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

    /**
     * Reacciona a cambios externos en el portfolio (por ejemplo, un reset
     * de partida). Sincroniza las acciones en propiedad con el estado real
     * del {@link StatsController} y resetea los controles de compra/venta.
     *
     * <p>Debe ejecutarse siempre en el hilo de Swing (EDT).</p>
     */
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

    /**
     * Actualiza todos los elementos de la vista al seleccionar una empresa
     * diferente en el dropdown: información de la empresa, máximos de los
     * sliders y costes mostrados.
     */
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

    /**
     * Lee el valor del campo de texto de compra, lo valida, lo acota al
     * máximo permitido y sincroniza el slider correspondiente.
     */
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

    /**
     * Lee el valor del campo de texto de venta, lo valida, lo acota al
     * máximo permitido y sincroniza el slider correspondiente.
     */
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

    /**
     * Calcula el coste total de comprar las acciones seleccionadas al
     * precio de mercado actual.
     *
     * @return coste en la moneda del juego, o {@code 0} si no hay empresa
     *         seleccionada.
     */
    private double calcBuyCost() {
        CompanyData c = model.getSelectedCompany();
        return (c == null) ? 0 : accionesComprar * c.getValorAccion();
    }

    /**
     * Calcula el ingreso total de vender las acciones seleccionadas al
     * precio de mercado actual.
     *
     * @return ingreso en la moneda del juego, o {@code 0} si no hay empresa
     *         seleccionada.
     */
    private double calcSellIncome() {
        CompanyData c = model.getSelectedCompany();
        return (c == null) ? 0 : accionesVender * c.getValorAccion();
    }

    /**
     * Ejecuta la operación de compra: delega en
     * {@link StatsController#trade} para descontar dinero y actualizar el
     * portfolio, actualiza la vista y reproduce el efecto de sonido de caja
     * registradora.
     *
     * <p>No hace nada si no hay empresa seleccionada o si la cantidad a
     * comprar es cero.</p>
     */
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

    /**
     * Ejecuta la operación de venta: delega en
     * {@link StatsController#trade} con cantidad negativa para ingresar
     * dinero y actualizar el portfolio, actualiza la vista y reproduce el
     * efecto de sonido correspondiente.
     *
     * <p>Reproduce un pitido de error y aborta si no hay empresa
     * seleccionada o si la cantidad a vender es cero.</p>
     */
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