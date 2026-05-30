package tools;

import javax.swing.Timer;


/**
 * Reloj del juego que avanza el tiempo de forma simulada mediante un
 * {@link javax.swing.Timer}.
 * <p>
 * Cada tick del timer incrementa un minuto del tiempo de juego. Al llegar
 * a 60 minutos se avanza una hora; al llegar a 24 horas, un día; y así
 * sucesivamente hasta gestionar el cambio de mes y año, teniendo en cuenta
 * los meses con distinto número de días y los años bisiestos
 * (a través de {@link Utils#diasDelMes(int, int)}).
 * </p>
 * <p>
 * Cualquier componente del juego puede suscribirse a los ticks mediante
 * {@link #addListener(Runnable)}, recibiendo una notificación cada vez
 * que el tiempo avanza un minuto.
 * </p>
 */
public class Clock {

	/** Fecha actual del juego. */
	private Date date;

	/** Hora actual del juego (0-23). */
	private int hour;

	/** Minuto actual del juego (0-59). */
	private int minute;

	/** Timer de Swing que dispara cada tick del reloj. */
	public Timer clock;

	/** Lista de listeners notificados en cada avance de minuto. */
	private java.util.List<Runnable> listeners = new java.util.ArrayList<>();
	
	/**
     * Construye un reloj con hora y minuto iniciales específicos.
     * Los valores fuera de rango se ignoran y se mantiene el valor por defecto.
     *
     * @param hour   hora inicial (0-24)
     * @param minute minuto inicial (0-59)
     */
	public Clock(int hour,int minute) {
		if(hour<=24&&hour>=0) {
			this.hour=hour;
		}
		if(minute<=59&&minute>=0) {
			this.minute=minute;
		}
		
		this.date= new Date(1,6,1996);
	}

	/**
     * Construye un reloj con la hora por defecto de inicio de partida (08:00)
     * y la fecha inicial {@code 01/06/1996}.
     */
	public Clock() {
		minute=0;
		hour=8;
		date= new Date(1,6,1996);
	}
	
	/**
     * Inicia el reloj con un intervalo de tick en milisegundos.
     * Cada vez que el timer se dispara, se avanza un minuto de juego
     * y se notifica a todos los listeners registrados.
     *
     * @param time intervalo entre ticks en milisegundos
     */
	public void initClock(int time) {
		clock= new Timer(time,e->{
			sumarTiempo();
		});
		clock.start();
	}
	
	/**
     * Detiene el timer del reloj sin reiniciar el tiempo acumulado.
     */
	public void detener() {
		clock.stop();
	}

	/**
     * Devuelve la fecha actual del juego.
     *
     * @return fecha en curso
     */
	public Date getDate() {
		return date;
	}

	/**
     * Devuelve la hora actual del juego.
     *
     * @return hora (0-23)
     */
	public int getHour() {
		return hour;
	}

	/**
     * Devuelve el minuto actual del juego.
     *
     * @return minuto (0-59)
     */
	public int getMinute() {
		return minute;
	}
	
	/**
	 * Avanza el reloj en un minuto y propaga el cambio en cascada:
	 * minutos → horas → días → meses → años.
	 * <p>
	 * Este método es invocado internamente por el {@link javax.swing.Timer}
	 * iniciado en {@link #initClock(int)} en cada tick del reloj. La lógica
	 * de desbordamiento sigue este orden:
	 * </p>
	 * <ol>
	 *   <li>Incrementa el minuto actual.</li>
	 *   <li>Si supera 59, reinicia el minuto a 0 e incrementa la hora.</li>
	 *   <li>Si la hora supera 23, reinicia a 0 y avanza un día
	 *       mediante {@link Date#sumDay()}.</li>
	 *   <li>Si el día supera el máximo del mes actual (calculado con
	 *       {@link Utils#diasDelMes(int, int)}, considerando años bisiestos),
	 *       reinicia el día a 1 y avanza el mes con {@link Date#sumMonth()}.</li>
	 *   <li>Si el mes supera 12, reinicia a 1 y avanza el año
	 *       con {@link Date#sumYear()}.</li>
	 * </ol>
	 * <p>
	 * Al finalizar, notifica a todos los listeners registrados mediante
	 * {@link #addListener(Runnable)}.
	 * </p>
	 */
	private void sumarTiempo() {
		minute++;
		if(minute>59) {
			minute=0;
			hour++;
			if(hour>23) {
				hour=0;
				date.sumDay();
				if(date.getDay()>Utils.diasDelMes(date.getMonth(),date.getYear())) {
					date.setDay(1);
					date.sumMonth();
					if(date.getMonth()>12) {
						date.setMonth(1);
						date.sumYear();
					}
				}
			}
		}
		listeners.forEach(Runnable::run);
	}
	
	/**
     * Establece una nueva fecha para el reloj.
     *
     * @param date nueva fecha del juego
     */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
     * Establece la hora del reloj. Valores fuera del rango 0-23 se ignoran.
     *
     * @param hour nueva hora (0-23)
     */
	public void setHour(int hour) {
		if(hour<24&&hour>=0) this.hour=hour;
		
	}

	/**
     * Establece el minuto del reloj. Valores fuera del rango 0-59 se ignoran.
     *
     * @param minute nuevo minuto (0-59)
     */
	public void setMinute(int minute) {
		if(minute<60&&minute>=0) this.minute = minute;
	}
	
	/**
     * Registra un listener que será invocado en cada tick del reloj (cada minuto).
     *
     * @param r tarea a ejecutar en cada avance de tiempo
     */
	public void addListener(Runnable r) {
	    listeners.add(r);
	}
}
