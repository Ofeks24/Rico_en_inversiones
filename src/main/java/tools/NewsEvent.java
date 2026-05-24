package tools;

public class NewsEvent {

    private final String titulo;
    private final String cuerpo;
    private final int    empresaId;   // -1 = global
    private final Sector sector;
    private final double impacto;     // +0.08 = +8%

    public NewsEvent(String titulo, String cuerpo,
                     int empresaId, Sector sector,
                     double impacto) {
        this.titulo    = titulo;
        this.cuerpo    = cuerpo;
        this.empresaId = empresaId;
        this.sector    = sector;
        this.impacto   = impacto;
    }

    public String getTitulo()    { return titulo;    }
    public String getCuerpo()    { return cuerpo;    }
    public int    getEmpresaId() { return empresaId; }
    public Sector getSector()    { return sector;    }
    public double getImpacto()   { return impacto;   }
}