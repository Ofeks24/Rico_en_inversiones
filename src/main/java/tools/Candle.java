package tools;


/**
 * Representa una vela japonesa (candlestick) de un gráfico de precios.
 * <p>
 * Cada instancia es inmutable y almacena los cuatro precios clásicos
 * de un período de tiempo: apertura, máximo, mínimo y cierre.
 * Es usada por {@link tools.MarketService} para construir el historial
 * de precios de cada empresa y por los paneles de gráficos para renderizarlo.
 * </p>
 */
public class Candle {
    private final double open, high, low, close;

    /**
     * Construye una vela con los cuatro precios del período.
     *
     * @param open  precio de apertura del período
     * @param high  precio máximo alcanzado durante el período
     * @param low   precio mínimo alcanzado durante el período
     * @param close precio de cierre del período
     */
    public Candle(double open, double high,
                  double low,  double close) {
        this.open  = open;
        this.high  = high;
        this.low   = low;
        this.close = close;
    }

    /**
     * Devuelve el precio de apertura del período.
     *
     * @return precio de apertura
     */
    public double getOpen()  { return open;  }

    /**
     * Devuelve el precio máximo alcanzado durante el período.
     *
     * @return precio máximo
     */
    public double getHigh()  { return high;  }

    /**
     * Devuelve el precio mínimo alcanzado durante el período.
     *
     * @return precio mínimo
     */
    public double getLow()   { return low;   }

    /**
     * Devuelve el precio de cierre del período.
     * <p>
     * Este valor es el que se utiliza como precio actual de mercado
     * en {@link system.Stats.PortfolioEntry#getValorAccion()}.
     * </p>
     *
     * @return precio de cierre
     */
    public double getClose() { return close; }
}