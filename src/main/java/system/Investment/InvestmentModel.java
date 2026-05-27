package system.Investment;

import database.CompanyRepository;
import system.Player;
import tools.CompanyData;

import java.util.List;


/**
 * Modelo MVC de la ventana de inversión.
 *
 * <p>Carga la lista completa de empresas desde la base de datos al
 * instanciarse, mantiene la referencia a la empresa seleccionada en el
 * dropdown y calcula el número máximo de acciones que el jugador puede
 * comprar en función de su dinero disponible y del stock en el mercado.</p>
 */
public class InvestmentModel {

    private final List<CompanyData> companies;

    private CompanyData selectedCompany;

    private int accionesSeleccionadas;
    

    /**
     * Crea el modelo cargando todas las empresas desde la base de datos
     * y preseleccionando la primera de la lista.
     */
    public InvestmentModel() {
        CompanyRepository repo = new CompanyRepository();
        companies = repo.getAllCompanies();
        if (!companies.isEmpty())
            selectedCompany = companies.get(0);
    }
    
    /**
     * Calcula el número máximo de acciones que el jugador puede comprar
     * de la empresa actualmente seleccionada.
     *
     * <p>El resultado es el mínimo entre las acciones disponibles en el
     * mercado (total del mercado menos las que ya posee el jugador) y las
     * que puede permitirse con su dinero actual.</p>
     *
     * @return número máximo de acciones comprables, o {@code 0} si no hay
     *         empresa seleccionada o el precio de la acción es cero.
     */
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
    
    
    /**
     * Devuelve la lista completa de empresas disponibles para invertir.
     *
     * @return lista inmutable de {@link CompanyData}.
     */
    public List<CompanyData> getCompanies() {
        return companies;
    }

    /**
     * Devuelve la empresa actualmente seleccionada en el dropdown.
     *
     * @return empresa seleccionada, o {@code null} si la lista está vacía.
     */
    public CompanyData getSelectedCompany() {
        return selectedCompany;
    }

    /**
     * Establece la empresa seleccionada en el dropdown.
     *
     * @param selectedCompany nueva empresa a mostrar en la vista.
     */
    public void setSelectedCompany(
            CompanyData selectedCompany
    ) {
        this.selectedCompany = selectedCompany;
    }

    /**
     * Devuelve el número de acciones actualmente seleccionadas en el slider.
     *
     * @return acciones seleccionadas.
     */
    public int getAccionesSeleccionadas() {
        return accionesSeleccionadas;
    }

    /**
     * Actualiza el número de acciones seleccionadas en el slider.
     *
     * @param accionesSeleccionadas nuevo valor del slider.
     */
    public void setAccionesSeleccionadas(
            int accionesSeleccionadas
    ) {
        this.accionesSeleccionadas =
                accionesSeleccionadas;
    }

    /**
     * Calcula el coste total de comprar las acciones seleccionadas al
     * precio actual de la empresa seleccionada.
     *
     * @return coste total en la moneda del juego, o {@code 0} si no hay
     *         empresa seleccionada.
     */
    public double getCosteTotal() {

        if (selectedCompany == null)
            return 0;

        return accionesSeleccionadas
                * selectedCompany
                .getValorAccion();
    }
}