package tools;

public class NewsTemplate {

    private final String tituloTemplate;
    private final String cuerpoTemplate;
    private final Sector sectorAfectado;  // GLOBAL o un sector concreto
    private final double impactoMin;
    private final double impactoMax;

    public NewsTemplate(String titulo, String cuerpo,
                        Sector sector,
                        double impactoMin, double impactoMax) {
        this.tituloTemplate  = titulo;
        this.cuerpoTemplate  = cuerpo;
        this.sectorAfectado  = sector;
        this.impactoMin      = impactoMin;
        this.impactoMax      = impactoMax;
    }

    public Sector getSector() { return sectorAfectado; }

    /** Instancia la template rellenando los huecos */
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