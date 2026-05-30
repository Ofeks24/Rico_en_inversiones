package tools;

import java.time.Clock;

/**
 * Representa una fecha del calendario del juego con día, mes y año.
 * <p>
 * Es utilizada por {@link tools.Clock} para llevar la cuenta del tiempo
 * en partida. Los valores no se validan internamente más allá de lo que
 * haga el propio reloj; la responsabilidad de mantener fechas coherentes
 * recae en {@code Clock}.
 * </p>
 */
public class Date {
	private int day;
	private int month;
	private int year;
	
	/**
     * Construye una fecha con los valores indicados.
     *
     * @param day   día del mes (1-31 según el mes)
     * @param month mes del año (1-12)
     * @param year  año (ej. 1996)
     */
	public Date(int day, int month, int year) {
		this.day = day;
		this.month = month;
		this.year = year;
	}
	
	/**
     * Devuelve el día del mes.
     *
     * @return día (1-31)
     */
	public int getDay() {
		return day;
	}

	/**
     * Establece el día del mes.
     *
     * @param day nuevo día
     */
	public void setDay(int day) {
		this.day = day;
	}

	/**
     * Devuelve el mes del año.
     *
     * @return mes (1-12)
     */
	public int getMonth() {
		return month;
	}

	/**
     * Establece el mes del año.
     *
     * @param month nuevo mes
     */
	public void setMonth(int month) {
		this.month = month;
	}

	/**
     * Devuelve el año.
     *
     * @return año (ej. 1996)
     */
	public int getYear() {
		return year;
	}

	/**
     * Establece el año.
     *
     * @param year nuevo año
     */
	public void setYear(int year) {
		this.year = year;
	}
	
	
	/**
     * Incrementa el día en una unidad.
     * No realiza comprobación de desbordamiento de mes; eso lo gestiona {@link Clock}.
     */
	public void sumDay(){
		day++;
	}

	/**
     * Incrementa el mes en una unidad.
     * No realiza comprobación de desbordamiento de año; eso lo gestiona {@link Clock}.
     */
	public void sumMonth(){
		month++;
	}

	/**
     * Incrementa el año en una unidad.
     */
	public void sumYear(){
		year++;
	}
	
	/**
     * Devuelve una representación textual de la fecha en formato legible.
     *
     * @return cadena con día, mes y año
     */
	@Override
	public String toString() {
		return "Day [day=" + day + ", month=" + month + ", year=" + year + "]";
	}
}
