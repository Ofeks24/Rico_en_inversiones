package system.Investment;

import database.CompanyRepository;
import system.Player;
import tools.CompanyData;

import java.util.List;

public class InvestmentModel {

    private final List<CompanyData> companies;

    private CompanyData selectedCompany;

    private int accionesSeleccionadas;
    


    public InvestmentModel() {
        CompanyRepository repo = new CompanyRepository();
        companies = repo.getAllCompanies();
        if (!companies.isEmpty())
            selectedCompany = companies.get(0);
    }
    
    public int getMaxAccionesComprables() {
        if (selectedCompany == null
                || selectedCompany.getValorAccion() <= 0) return 0;
        double dinero = Player.getInstance().getDinero();
        int disponiblesEnMercado =
                selectedCompany.getAccionesMercado()
                - selectedCompany.getAccionesPropiedad();
        int comprablesConDinero =
                (int)(dinero / selectedCompany.getValorAccion());
        return Math.min(disponiblesEnMercado, comprablesConDinero);
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

        if (selectedCompany == null)
            return 0;

        return accionesSeleccionadas
                * selectedCompany
                .getValorAccion();
    }
}