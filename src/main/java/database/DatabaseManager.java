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
    	String sql[] = {
                "CREATE TABLE IF NOT EXISTS \"Empresas\" (\r\n"
                + "	\"id\"	INTEGER,\r\n"
                + "	\"nombre\"	TEXT NOT NULL,\r\n"
                + "	\"descripcion\"	TEXT,\r\n"
                + "	\"total_acciones\"	INTEGER NOT NULL,\r\n"
                + "	\"valor_accion\"	NUMERIC NOT NULL,\r\n"
                + "	\"marca\"	TEXT NOT NULL DEFAULT 'XXX',\r\n"
                + "	\"sector\"	TEXT NOT NULL DEFAULT 'GLOBAL',\r\n"
                + "	PRIMARY KEY(\"id\" AUTOINCREMENT)\r\n"
                + ")",
                "CREATE TABLE IF NOT EXISTS \"Partidas_guardadas\" (\r\n"
                + "	\"id\"	INTEGER,\r\n"
                + "	\"usuario_id\"	INTEGER NOT NULL,\r\n"
                + "	\"dinero\"	NUMERIC NOT NULL,\r\n"
                + "	\"anyo\"	INTEGER NOT NULL,\r\n"
                + "	\"mes\"	INTEGER NOT NULL,\r\n"
                + "	\"dia\"	INTEGER NOT NULL,\r\n"
                + "	\"hora\"	INTEGER NOT NULL,\r\n"
                + "	\"minuto\"	INTEGER NOT NULL,\r\n"
                + "	PRIMARY KEY(\"id\" AUTOINCREMENT),\r\n"
                + "	CONSTRAINT \"partida_usuario\" FOREIGN KEY(\"usuario_id\") REFERENCES \"Usuarios\"(\"id\")\r\n"
                + ")",
                "CREATE TABLE IF NOT EXISTS \"Stats\" (\r\n"
                + "	\"id\"	INTEGER,\r\n"
                + "	\"partida_id\"	INTEGER NOT NULL,\r\n"
                + "	\"empresa_id\"	INTEGER NOT NULL,\r\n"
                + "	\"n_acciones\"	INTEGER NOT NULL,\r\n"
                + "	PRIMARY KEY(\"id\" AUTOINCREMENT),\r\n"
                + "	CONSTRAINT \"stats_empresa\" FOREIGN KEY(\"empresa_id\") REFERENCES \"Empresas\"(\"id\"),\r\n"
                + "	CONSTRAINT \"stats_partida\" FOREIGN KEY(\"partida_id\") REFERENCES \"Partidas_guardadas\"(\"id\")\r\n"
                + ")",
                "CREATE TABLE IF NOT EXISTS\"Usuarios\" (\r\n"
                + "	\"id\"	INTEGER,\r\n"
                + "	\"nombre\"	TEXT NOT NULL,\r\n"
                + "	PRIMARY KEY(\"id\" AUTOINCREMENT)\r\n"
                + ")"
    			};
            try (
                Connection conn = DriverManager.getConnection(URL);
            ) {
            	for (String command : sql) {
            		java.sql.PreparedStatement ps = conn.prepareStatement(command);
                    ps.executeUpdate();
				}
            } catch (Exception e) {
                e.printStackTrace();
            }

        return DriverManager.getConnection(URL);
    }
}