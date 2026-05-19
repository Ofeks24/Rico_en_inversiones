package main.java.system.Investment;

import java.util.List;

import main.java.tools.CompanyData;

public class InvestmentModel {

    private final List<CompanyData> companies;

    private CompanyData selectedCompany;

    private int accionesSeleccionadas;

    public InvestmentModel(List<CompanyData> companies) {

        this.companies = companies;

        if (!companies.isEmpty()) {
            selectedCompany = companies.get(0);
        }
    }

    public List<CompanyData> getCompanies() {
        return companies;
    }

    public CompanyData getSelectedCompany() {
        return selectedCompany;
    }

    public void setSelectedCompany(
            CompanyData selectedCompany
    ) {
        this.selectedCompany = selectedCompany;
    }

    public int getAccionesSeleccionadas() {
        return accionesSeleccionadas;
    }

    public void setAccionesSeleccionadas(
            int accionesSeleccionadas
    ) {
        this.accionesSeleccionadas =
                accionesSeleccionadas;
    }

    public double getCosteTotal() {

        if (selectedCompany == null) return 0;

        return accionesSeleccionadas *
                selectedCompany.getValorAccion();
    }
}