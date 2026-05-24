package system;

import database.CompanyRepository;

public class Player {

    private double dinero;

    // ── Singleton para compartirlo entre clases ───────────
    private static Player instance;

    public static Player getInstance() {
        if (instance == null)
            instance = new Player(
                new CompanyRepository().getDineroJugador()
            );
        return instance;
    }

    // ── Constructor ───────────────────────────────────────
    private Player(double dinero) {
        this.dinero = dinero;
    }

    // ── Getters / Setters ─────────────────────────────────
    public double getDinero() { return dinero; }

    // ── Operaciones ───────────────────────────────────────
    public void gastar(double cantidad) {
        dinero -= cantidad;
        new CompanyRepository().guardarDinero(dinero);
    }

    public void ingresar(double cantidad) {
        dinero += cantidad;
        new CompanyRepository().guardarDinero(dinero);
    }
}