package tools;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


/**
 * Generador de eventos de noticias para el mercado del juego.
 * <p>
 * Carga en el constructor un banco de plantillas desde
 * {@link tools.NewsTemplateLoader} y, en cada llamada a
 * {@link #generate(List)}, selecciona aleatoriamente una plantilla
 * compatible con las empresas disponibles para producir un
 * {@link tools.NewsEvent} listo para ser aplicado por
 * {@link tools.MarketService}.
 * </p>
 *
 * <p>
 * La selección sigue estas reglas de probabilidad:
 * </p>
 * <ul>
 *   <li>30 % de probabilidad de elegir una plantilla de tipo
 *       {@link tools.Sector#GLOBAL}.</li>
 *   <li>70 % de probabilidad de elegir una plantilla sectorial,
 *       asociando el evento a una empresa del sector correspondiente.</li>
 * </ul>
 */
public class NewsGenerator {

    private final List<NewsTemplate> templates;
    private final Random rng = new Random();

    /**
     * Elige aleatoriamente una plantilla compatible con la lista de empresas
     * proporcionada y genera el {@link NewsEvent} instanciado.
     * <p>
     * Si la plantilla elegida es global, la empresa de referencia se selecciona
     * al azar entre todas las disponibles. Si es sectorial, se filtra primero
     * por sector; en caso de no haber empresas del sector, se usa cualquiera.
     * </p>
     *
     * @return un {@link NewsEvent} nombre generado a partir de la plantilla seleccionada
     */
    public NewsGenerator() {
        templates = NewsTemplateLoader.load();

        if (templates.isEmpty()) {
            System.err.println("[NewsGenerator] "
                    + "¡Sin templates! Revisa data/news_templates.json");
        }
    }

    // =========================================================
    // GENERAR NOTICIA
    // =========================================================

    /**
     * Elige una template compatible con alguna empresa de la lista
     * y devuelve el NewsEvent instanciado.
     */
    public NewsEvent generate(List<CompanyData> companies) {

        // 30% de probabilidad de noticia global
        boolean global = rng.nextDouble() < 0.30;

        List<NewsTemplate> candidatas = templates.stream()
            .filter(t -> global
                    ? t.getSector() == Sector.GLOBAL
                    : t.getSector() != Sector.GLOBAL)
            .collect(Collectors.toList());

        if (candidatas.isEmpty()) candidatas = templates;

        NewsTemplate template =
                candidatas.get(rng.nextInt(candidatas.size()));

        // Para noticias sectoriales, elegir empresa del sector
        CompanyData empresa;
        if (template.getSector() == Sector.GLOBAL) {
            empresa = companies.get(rng.nextInt(companies.size()));
        } else {
            List<CompanyData> delSector = companies.stream()
                .filter(c -> c.getSector() == template.getSector())
                .collect(Collectors.toList());

            empresa = delSector.isEmpty()
                    ? companies.get(rng.nextInt(companies.size()))
                    : delSector.get(rng.nextInt(delSector.size()));
        }

        return template.generate(empresa);
    }

    // =========================================================
    // BANCO DE NOTICIAS
    // Formato impacto: (min, max). Negativo = bajada.
    // =========================================================

    
}