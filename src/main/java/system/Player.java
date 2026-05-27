package system;

import database.CompanyRepository;


/**
 * Representa al jugador dentro de la partida.
 *
 * <p>Implementado como singleton para que todas las clases del juego
 * accedan siempre al mismo estado económico. Al instanciarse por primera
 * vez, carga el dinero persistido en la base de datos mediante
 * {@link CompanyRepository#getDineroJugador()}.</p>
 */
public class Player {

    private double dinero;

    /** Instancia única del jugador. */
    private static Player instance;

    /**
     * Devuelve la instancia única del jugador.
     *
     * <p>Si todavía no existe, la crea cargando el dinero actual desde
     * la base de datos.</p>
     *
     * @return instancia singleton de {@code Player}.
     */
    public static Player getInstance() {
        if (instance == null)
            instance = new Player(
                new CompanyRepository().getDineroJugador()
            );
        return instance;
    }

    /**
     * Crea un nuevo jugador con el dinero indicado.
     *
     * @param dinero cantidad de dinero inicial.
     */
    private Player(double dinero) {
        this.dinero = dinero;
    }

    /**
     * Devuelve el dinero actual del jugador.
     *
     * @return saldo disponible en la partida.
     */
    public double getDinero() { return dinero; }

    /**
     * Descuenta una cantidad del saldo del jugador y persiste el nuevo
     * valor en la base de datos.
     *
     * @param cantidad importe a gastar (debe ser positivo).
     */
    public void gastar(double cantidad) {
        dinero -= cantidad;
        new CompanyRepository().guardarDinero(dinero);
    }

    /**
     * Añade una cantidad al saldo del jugador y persiste el nuevo valor
     * en la base de datos.
     *
     * @param cantidad importe a ingresar (debe ser positivo).
     */
    public void ingresar(double cantidad) {
        dinero += cantidad;
        new CompanyRepository().guardarDinero(dinero);
    }

    /**
     * Reinicia el singleton con un dinero inicial concreto.
     *
     * <p>Útil tras un reset de partida: todas las clases que llamen
     * posteriormente a {@link #getInstance()} recibirán el nuevo valor
     * sin necesidad de reiniciar la aplicación.</p>
     *
     * @param dineroInicial saldo con el que se reinicia la partida.
     */
    public static void reset(double dineroInicial) {
        instance = new Player(dineroInicial);
    }

}