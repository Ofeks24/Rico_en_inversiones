package tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class NewsGenerator {

    private final List<NewsTemplate> templates;
    private final Random rng = new Random();

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