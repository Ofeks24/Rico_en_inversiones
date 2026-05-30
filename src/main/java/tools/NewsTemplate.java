package tools;


/**
 * Plantilla reutilizable para generar eventos de noticias de mercado.
 * <p>
 * Almacena el titular y el cuerpo como cadenas con marcadores de posición
 * ({@code {empresa}}, {@code {sector}}, {@code {pct}}, {@code {marca}})
 * que se rellenan en el momento de la generación con los datos reales de
 * la empresa afectada. Cada plantilla también define el rango de impacto
 * económico y el sector al que pertenece.
 * </p>
 *
 * <p>Marcadores de posición admitidos en título y cuerpo:</p>
 * <ul>
 *   <li>{@code {empresa}} → nombre completo de la empresa</li>
 *   <li>{@code {marca}}   → marca comercial de la empresa</li>
 *   <li>{@code {sector}}  → sector en minúsculas</li>
 *   <li>{@code {pct}}     → porcentaje de impacto formateado (ej. «3,7 %»)</li>
 * </ul>
 */
public class NewsTemplate {

    /** Plantilla base utilizada para generar el titular de la noticia. */
    private final String tituloTemplate;

    /** Plantilla base utilizada para generar el cuerpo de la noticia. */
    private final String cuerpoTemplate;

    /**
     * Sector económico afectado por las noticias generadas.
     * <p>
     * Puede representar un sector concreto o {@link tools.Sector#GLOBAL}
     * para indicar un impacto sobre todo el mercado.
     * </p>
     */
    private final Sector sectorAfectado;  // GLOBAL o un sector concreto

    /**
     * Valor mínimo posible del impacto económico generado.
     * <p>
     * El impacto real se calcula aleatoriamente dentro del rango definido
     * entre {@code impactoMin} e {@code impactoMax}.
     * </p>
     */
    private final double impactoMin;

    /**
     * Valor máximo posible del impacto económico generado.
     *
     * @see #impactoMin
     */
    private final double impactoMax;

    /**
     * Crea una nueva plantilla de noticias reutilizable.
     *
     * @param titulo
     *        plantilla del titular de la noticia
     *
     * @param cuerpo
     *        plantilla del cuerpo descriptivo de la noticia
     *
     * @param sector
     *        sector económico afectado por la noticia
     *
     * @param impactoMin
     *        impacto mínimo posible generado por esta plantilla
     *
     * @param impactoMax
     *        impacto máximo posible generado por esta plantilla
     */
    public NewsTemplate(String titulo, String cuerpo,
                        Sector sector,
                        double impactoMin, double impactoMax) {
        this.tituloTemplate  = titulo;
        this.cuerpoTemplate  = cuerpo;
        this.sectorAfectado  = sector;
        this.impactoMin      = impactoMin;
        this.impactoMax      = impactoMax;
    }

    /**
     * Devuelve el sector económico al que está asociada esta plantilla.
     * <p>
     * El valor {@link tools.Sector#GLOBAL} indica que la noticia generada
     * afectará a todas las empresas del mercado.
     * </p>
     *
     * @return sector afectado por las noticias generadas con esta plantilla
     */
    public Sector getSector() { return sectorAfectado; }

    /**
     * Instancia la plantilla con los datos de una empresa concreta y genera
     * un {@link NewsEvent} listo para ser procesado por {@link tools.MarketService}.
     * <p>
     * El impacto se calcula aleatoriamente dentro del rango
     * [{@code impactoMin}, {@code impactoMax}] definido en la plantilla.
     * Para noticias globales ({@link tools.Sector#GLOBAL}), el {@code empresaId}
     * del evento resultante se establece a {@code -1}.
     * </p>
     *
     * @param empresa empresa de referencia cuyos datos rellenan los marcadores
     * @return un {@link NewsEvent} con título, cuerpo, sector e impacto generados
     */
    public NewsEvent generate(CompanyData empresa) {
        double impacto = impactoMin
                + Math.random() * (impactoMax - impactoMin);

        String titulo = fill(tituloTemplate, empresa, impacto);
        String cuerpo = fill(cuerpoTemplate, empresa, impacto);

        int targetId = sectorAfectado == Sector.GLOBAL
                ? -1
                : empresa.getId();

        return new NewsEvent(titulo, cuerpo, targetId,
                             sectorAfectado, impacto);
    }

    /**
     * Sustituye los marcadores de posición de una plantilla por los valores
     * reales de una empresa y un impacto concreto.
     * <p>
     * Los marcadores admitidos son:
     * </p>
     * <ul>
     *   <li>{@code {empresa}}</li>
     *   <li>{@code {marca}}</li>
     *   <li>{@code {sector}}</li>
     *   <li>{@code {pct}}</li>
     * </ul>
     *
     * @param template
     *        texto base con marcadores de posición
     *
     * @param empresa
     *        empresa cuyos datos se insertarán en el texto
     *
     * @param impacto
     *        impacto económico utilizado para generar el porcentaje visible
     *
     * @return texto final con todos los marcadores reemplazados
     */
    private String fill(String template,
                        CompanyData empresa, double impacto) {
        String pct = String.format("%.1f%%",
                                   Math.abs(impacto * 100));
        return template
            .replace("{empresa}", empresa.getNombre())
            .replace("{sector}",  empresa.getSector().name()
                                         .toLowerCase())
            .replace("{pct}",     pct)
            .replace("{marca}",   empresa.getMarca());
    }
}