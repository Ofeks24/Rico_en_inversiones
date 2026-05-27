package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * Gestiona la conexión a la base de datos SQLite de la aplicación.
 * <p>
 * La ruta de la base de datos se construye dinámicamente a partir del
 * directorio de trabajo actual ({@code user.dir}), apuntando al archivo
 * {@code data/app.db}.
 * </p>
 * <p>
 * Esta clase no mantiene ninguna conexión persistente; cada llamada a
 * {@link #getConnection()} abre una conexión nueva que el llamador es
 * responsable de cerrar (idealmente mediante try-with-resources).
 * </p>
 */
public class DatabaseManager {

    /**
     * URL JDBC que apunta al archivo SQLite {@code data/app.db},
     * relativo al directorio de trabajo de la JVM.
     */
    private static final String URL =
            "jdbc:sqlite:"
            + System.getProperty("user.dir")
            + "/data/app.db";

    /**
     * Abre y devuelve una nueva conexión a la base de datos SQLite.
     * <p>
     * El llamador es responsable de cerrar la conexión devuelta para
     * liberar los recursos asociados. Se recomienda usarla dentro de un
     * bloque try-with-resources.
     * </p>
     *
     * @return una {@link Connection} activa a la base de datos.
     * @throws SQLException si el driver no puede establecer la conexión
     *                      (p. ej. archivo no encontrado o bloqueado).
     */
    public static Connection getConnection()
            throws SQLException {

        return DriverManager.getConnection(URL);
    }
}