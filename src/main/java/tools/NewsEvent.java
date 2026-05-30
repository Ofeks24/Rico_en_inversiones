package tools;


/**
 * Representa una noticia o evento económico que puede afectar al mercado
 * y al valor de una o varias empresas del juego.
 * <p>
 * Cada {@code NewsEvent} contiene un titular, un cuerpo descriptivo y un
 * impacto porcentual que será aplicado posteriormente por el sistema de
 * mercado ({@link tools.MarketService}).
 * </p>
 *
 * <p>
 * Una noticia puede:
 * </p>
 * <ul>
 *   <li>Afectar a una empresa concreta mediante su identificador.</li>
 *   <li>Afectar a todas las empresas de un sector específico.</li>
 *   <li>Ser global y afectar a todo el mercado.</li>
 * </ul>
 *
 * <p>
 * El campo {@code impacto} representa una variación relativa del valor de
 * mercado. Por ejemplo:
 * </p>
 * <ul>
 *   <li>{@code 0.08} → aumento del 8%</li>
 *   <li>{@code -0.15} → caída del 15%</li>
 * </ul>
 *
 * <p>
 * Si {@code empresaId} vale {@code -1}, la noticia no está asociada a una
 * empresa concreta y su alcance dependerá del {@link tools.Sector}.
 * </p>
 */
public class NewsEvent {

    /** Titular principal de la noticia. */
    private final String titulo;

    /** Texto descriptivo o cuerpo completo de la noticia. */
    private final String cuerpo;

    /**
     * Identificador de la empresa afectada.
     * <p>
     * El valor {@code -1} indica que la noticia no pertenece a una empresa
     * concreta y que su efecto debe interpretarse como sectorial o global.
     * </p>
     */
    private final int    empresaId;   // -1 = global

    /**
     * Sector económico afectado por la noticia.
     * <p>
     * Puede representar un sector específico o {@link tools.Sector#GLOBAL}.
     * </p>
     */
    private final Sector sector;

    /**
     * Impacto económico relativo producido por la noticia.
     * <p>
     * Se expresa como porcentaje decimal:
     * </p>
     * <ul>
     *   <li>{@code 0.05} → +5%</li>
     *   <li>{@code -0.12} → -12%</li>
     * </ul>
     */
    private final double impacto;     // +0.08 = +8%

    /**
     * Crea un nuevo evento de noticias para el mercado.
     *
     * @param titulo
     *        titular principal de la noticia
     *
     * @param cuerpo
     *        descripción o contenido completo de la noticia
     *
     * @param empresaId
     *        identificador de la empresa afectada;
     *        {@code -1} si la noticia es sectorial o global
     *
     * @param sector
     *        sector económico afectado por la noticia
     *
     * @param impacto
     *        variación relativa del mercado provocada por la noticia
     */
    public NewsEvent(String titulo, String cuerpo,
                     int empresaId, Sector sector,
                     double impacto) {
        this.titulo    = titulo;
        this.cuerpo    = cuerpo;
        this.empresaId = empresaId;
        this.sector    = sector;
        this.impacto   = impacto;
    }

    /**
     * Devuelve el titular de la noticia.
     *
     * @return título principal del evento
     */
    public String getTitulo()    { return titulo;    }

    /**
     * Devuelve el cuerpo descriptivo de la noticia.
     *
     * @return contenido textual completo del evento
     */
    public String getCuerpo()    { return cuerpo;    }

    /**
     * Devuelve el identificador de la empresa afectada.
     *
     * @return id de empresa, o {@code -1} si el evento es sectorial/global
     */
    public int    getEmpresaId() { return empresaId; }

    /**
     * Devuelve el sector económico afectado por la noticia.
     *
     * @return sector asociado al evento
     */
    public Sector getSector()    { return sector;    }

    /**
     * Devuelve el impacto económico de la noticia.
     *
     * @return variación relativa aplicada al mercado
     */
    public double getImpacto()   { return impacto;   }
}