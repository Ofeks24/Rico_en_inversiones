package system.Stats;

import database.CompanyRepository;
import tools.MarketService;

import java.util.ArrayList;
import java.util.List;


/**
 * Modelo de datos de la cartera de inversiones del jugador.
 * <p>
 * Mantiene la lista de posiciones abiertas ({@link PortfolioEntry}) y actúa
 * como única fuente de verdad para las operaciones de compra y venta.
 * Cada modificación se persiste automáticamente en la base de datos a través
 * de {@link CompanyRepository}.
 * </p>
 * <p>
 * Sigue el patrón MVC: es la "M" del subsistema de estadísticas, y
 * {@link StatsController} actúa como intermediario entre este modelo
 * y {@link StatsPanel}.
 * </p>
 */
public class StatsModel {

    private final List<PortfolioEntry> portfolio = new ArrayList<>();
    private final CompanyRepository    repo      = new CompanyRepository();
    private final MarketService        market;

    /**
     * Construye el modelo cargando desde la base de datos las posiciones
     * abiertas del jugador (empresas con acciones en propiedad &gt; 0).
     *
     * @param market servicio de mercado para obtener precios actuales en
     *               cada {@link PortfolioEntry}
     */
    public StatsModel(MarketService market) {
        this.market = market;

        repo.getAllCompanies().forEach(c -> {
            if (c.getAccionesPropiedad() > 0) {
                portfolio.add(new PortfolioEntry(
                    c.getId(),
                    c.getNombre(),
                    c.getAccionesPropiedad(),
                    c.getValorAccion(),
                    market
                ));
            }
        });
    }

    /**
     * Devuelve la lista de posiciones abiertas en la cartera del jugador.
     *
     * @return lista de {@link PortfolioEntry}; puede estar vacía pero nunca es {@code null}
     */
    public List<PortfolioEntry> getPortfolio() { return portfolio; }

    /**
     * Registra la compra de acciones de una empresa.
     * <p>
     * Si ya existe una posición abierta para esa empresa, incrementa el
     * número de acciones; en caso contrario crea una nueva entrada.
     * La operación se persiste en la base de datos.
     * </p>
     *
     * @param empresaId   identificador de la empresa
     * @param nombre      nombre de la empresa (usado al crear una nueva entrada)
     * @param cantidad    número de acciones compradas (debe ser &gt; 0)
     * @param valorAccion precio unitario en el momento de la compra
     */
    public void buyShares(int empresaId, String nombre,
                          int cantidad, double valorAccion) {
        PortfolioEntry existing = findEntry(empresaId);
        if (existing != null) {
            existing.setAcciones(existing.getAcciones() + cantidad);
            repo.guardarAcciones(empresaId, existing.getAcciones());
        } else {
            portfolio.add(new PortfolioEntry(
                empresaId, nombre, cantidad, valorAccion, market
            ));
            repo.guardarAcciones(empresaId, cantidad);
        }
    }

    /**
     * Registra la venta de acciones de una empresa.
     * <p>
     * Si tras la venta la posición llega a cero o menos, la entrada se
     * elimina de la cartera y se borra de la base de datos.
     * Si no existe ninguna posición para la empresa indicada, no hace nada.
     * </p>
     *
     * @param empresaId identificador de la empresa
     * @param cantidad  número de acciones vendidas (debe ser &gt; 0)
     */
    public void sellShares(int empresaId, int cantidad) {
        PortfolioEntry entry = findEntry(empresaId);
        if (entry == null) return;
        int restantes = entry.getAcciones() - cantidad;
        if (restantes <= 0) {
            portfolio.remove(entry);
            repo.eliminarAcciones(empresaId);
        } else {
            entry.setAcciones(restantes);
            repo.guardarAcciones(empresaId, restantes);
        }
    }

    /**
     * Calcula el valor total de la cartera a precios actuales de mercado.
     *
     * @return suma de {@link PortfolioEntry#getValorTotal()} de todas las posiciones
     */
    public double getTotalValue() {
        double total = 0;
        for (PortfolioEntry p : portfolio)
            total += p.getValorTotal();
        return total;
    }

    /**
     * Devuelve el número de acciones poseídas de una empresa concreta.
     *
     * @param empresaId identificador de la empresa
     * @return número de acciones en cartera; {@code 0} si la empresa no está en cartera
     */
    public int getOwnedShares(int empresaId) {
        PortfolioEntry e = findEntry(empresaId);
        return (e == null) ? 0 : e.getAcciones();
    }

    /**
     * Busca una entrada de cartera por id de empresa.
     *
     * @param empresaId identificador de la empresa
     * @return la {@link PortfolioEntry} correspondiente, o {@code null} si no existe
     */
    private PortfolioEntry findEntry(int empresaId) {
        for (PortfolioEntry p : portfolio)
            if (p.getEmpresaId() == empresaId) return p;
        return null;
    }

    /**
     * Vacía completamente la cartera del jugador en memoria y restablece a
     * cero las acciones en propiedad de todas las empresas cargadas.
     * <p>
     * <strong>Nota:</strong> no persiste el borrado en base de datos; eso lo
     * gestiona {@link database.CompanyRepository#resetearPartida} que debe
     * llamarse previamente desde {@link system.OptionsWindow}.
     * </p>
     */
    public void clearPortfolio() {
        repo.getAllCompanies().forEach(c -> c.setAccionesPropiedad(0));
        portfolio.clear();
    }
}