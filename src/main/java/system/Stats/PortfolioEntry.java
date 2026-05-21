package system.Stats;

public class PortfolioEntry {

    private int empresaId;

    private String empresaNombre;

    private int acciones;

    private double valorAccion;

    public PortfolioEntry(
            int empresaId,
            String empresaNombre,
            int acciones,
            double valorAccion
    ) {

        this.empresaId = empresaId;
        this.empresaNombre = empresaNombre;
        this.acciones = acciones;
        this.valorAccion = valorAccion;
    }

    public int getEmpresaId() {
        return empresaId;
    }

    public String getEmpresaNombre() {
        return empresaNombre;
    }

    public int getAcciones() {
        return acciones;
    }

    public void setAcciones(int acciones) {
        this.acciones = acciones;
    }

    public double getValorAccion() {
        return valorAccion;
    }

    public double getValorTotal() {

        return acciones * valorAccion;
    }
}