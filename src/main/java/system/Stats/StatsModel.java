package system.Stats;

import database.CompanyRepository;
import tools.MarketService;

import java.util.ArrayList;
import java.util.List;

public class StatsModel {

    private final List<PortfolioEntry> portfolio = new ArrayList<>();
    private final CompanyRepository    repo      = new CompanyRepository();
    private final MarketService        market;

    // ── Constructor ───────────────────────────────────────
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

    public List<PortfolioEntry> getPortfolio() { return portfolio; }

    // ── Comprar ───────────────────────────────────────────
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

    // ── Vender ────────────────────────────────────────────
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

    // ── Valor total a precio de mercado ───────────────────
    public double getTotalValue() {
        double total = 0;
        for (PortfolioEntry p : portfolio)
            total += p.getValorTotal();
        return total;
    }

    /** Devuelve las acciones poseídas de una empresa (0 si no está en cartera). */
    public int getOwnedShares(int empresaId) {
        PortfolioEntry e = findEntry(empresaId);
        return (e == null) ? 0 : e.getAcciones();
    }

    private PortfolioEntry findEntry(int empresaId) {
        for (PortfolioEntry p : portfolio)
            if (p.getEmpresaId() == empresaId) return p;
        return null;
    }

    public void clearPortfolio() {
        repo.getAllCompanies().forEach(c -> c.setAccionesPropiedad(0));
        portfolio.clear();
    }
}