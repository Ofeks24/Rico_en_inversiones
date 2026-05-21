package system.Stats;

import java.util.ArrayList;
import java.util.List;

public class StatsModel {

    private final List<PortfolioEntry> portfolio =
            new ArrayList<>();

    // =====================================================
    // GETTERS
    // =====================================================

    public List<PortfolioEntry> getPortfolio() {

        return portfolio;
    }

    // =====================================================
    // COMPRAR
    // =====================================================

    public void buyShares(

            int empresaId,
            String empresaNombre,
            int cantidad,
            double valorAccion
    ) {

        PortfolioEntry existing =
                findEntry(empresaId);

        // ya existe
        if (existing != null) {

            existing.setAcciones(

                    existing.getAcciones()
                    + cantidad
            );

            return;
        }

        // nueva entrada
        portfolio.add(

                new PortfolioEntry(

                        empresaId,
                        empresaNombre,
                        cantidad,
                        valorAccion
                )
        );
    }

    // =====================================================
    // VENDER
    // =====================================================

    public void sellShares(

            int empresaId,
            int cantidad
    ) {

        PortfolioEntry entry =
                findEntry(empresaId);

        if (entry == null) return;

        int restantes =
                entry.getAcciones()
                - cantidad;

        // eliminar si queda a 0
        if (restantes <= 0) {

            portfolio.remove(entry);

            return;
        }

        entry.setAcciones(restantes);
    }

    // =====================================================
    // BUSCAR
    // =====================================================

    private PortfolioEntry findEntry(
            int empresaId
    ) {

        for (PortfolioEntry p : portfolio) {

            if (p.getEmpresaId()
                    == empresaId) {

                return p;
            }
        }

        return null;
    }

    // =====================================================
    // VALOR TOTAL
    // =====================================================

    public double getTotalValue() {

        double total = 0;

        for (PortfolioEntry p : portfolio) {

            total += p.getValorTotal();
        }

        return total;
    }
}