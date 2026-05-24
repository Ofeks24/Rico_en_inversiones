package system.Stats;

import database.CompanyRepository;
import java.util.ArrayList;
import java.util.List;

public class StatsModel {

    private final List<PortfolioEntry> portfolio = new ArrayList<>();
    private final CompanyRepository repo = new CompanyRepository();

    // ── Cargar desde BD al construir ──────────────────────
    public StatsModel() {
        repo.getAllCompanies().forEach(c -> {
            if (c.getAccionesPropiedad() > 0) {
                portfolio.add(new PortfolioEntry(
                    c.getId(),
                    c.getNombre(),
                    c.getAccionesPropiedad(),
                    c.getValorAccion()
                ));
            }
        });
    }

    public List<PortfolioEntry> getPortfolio() { return portfolio; }

    // ── Comprar ───────────────────────────────────────────
    public void buyShares(int empresaId, String empresaNombre,
                          int cantidad, double valorAccion) {
        PortfolioEntry existing = findEntry(empresaId);
        if (existing != null) {
            existing.setAcciones(existing.getAcciones() + cantidad);
            repo.guardarAcciones(empresaId, existing.getAcciones());
        } else {
            PortfolioEntry entry = new PortfolioEntry(
                empresaId, empresaNombre, cantidad, valorAccion
            );
            portfolio.add(entry);
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

    private PortfolioEntry findEntry(int empresaId) {
        for (PortfolioEntry p : portfolio)
            if (p.getEmpresaId() == empresaId) return p;
        return null;
    }

    public double getTotalValue() {
        double total = 0;
        for (PortfolioEntry p : portfolio)
            total += p.getValorTotal();
        return total;
    }
}