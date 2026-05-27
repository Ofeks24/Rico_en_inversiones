package database;

import tools.Clock;
import tools.CompanyData;
import tools.Sector;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;


/**
 * Repositorio de acceso a datos para las operaciones relacionadas con
 * empresas, acciones del jugador y el estado de la partida guardada.
 * <p>
 * Todas las operaciones trabajan contra la partida del usuario con
 * {@code usuario_id = 1}. Cada método abre su propia conexión a través
 * de {@link DatabaseManager#getConnection()} y la cierra al terminar,
 * por lo que no es necesario gestionar el ciclo de vida de la conexión
 * desde fuera de esta clase.
 * </p>
 * <p>
 * En caso de error SQL, los métodos capturan la excepción e imprimen
 * el stack trace sin relanzarla, devolviendo el valor por defecto
 * correspondiente (lista vacía, {@code 0}, etc.).
 * </p>
 */
public class CompanyRepository {

    /**
     * Recupera todas las empresas disponibles en el mercado, incluyendo
     * el número de acciones que el jugador posee de cada una.
     * <p>
     * Realiza un {@code LEFT JOIN} con {@code Partidas_guardadas} y
     * {@code Stats} para obtener las acciones en propiedad del usuario.
     * Si el usuario no posee acciones de una empresa, el campo
     * {@code acciones_usuario} devuelve {@code 0} gracias al
     * {@code COALESCE}.
     * </p>
     *
     * @return lista con todos los {@link CompanyData} encontrados en la
     *         tabla {@code Empresas}; nunca {@code null}, puede estar vacía
     *         si ocurre un error o la tabla está vacía.
     */
    public List<CompanyData> getAllCompanies() {

        List<CompanyData> companies =
                new ArrayList<>();

        String sql =
                "SELECT\r\n"
                + "    e.id,\r\n"
                + "    e.nombre,\r\n"
                + "    e.descripcion,\r\n"
                + "    e.total_acciones,\r\n"
                + "    COALESCE(ca.n_acciones, 0) AS acciones_usuario,\r\n"
                + "    e.valor_accion,\r\n"
                + "    e.marca,\r\n"
                + "    e.sector\r\n"
                + "FROM Empresas e\r\n"
                + "LEFT JOIN Partidas_guardadas pg ON pg.usuario_id = 1\r\n"
                + "LEFT JOIN Stats ca ON ca.empresa_id = e.id AND ca.partida_id = pg.id;";

        try (

            Connection conn =
                    DatabaseManager.getConnection();

            Statement stmt =
                    conn.createStatement();

            ResultSet rs =
                    stmt.executeQuery(sql)

        ) {

            while (rs.next()) {

                CompanyData company =
                        new CompanyData(
                        		rs.getInt("Id"),

                                rs.getString("Nombre"),

                                rs.getString("Descripcion"),

                                rs.getInt("Total_acciones"),

                                rs.getInt("Acciones_usuario"),

                                rs.getDouble("Valor_accion"),
                                
                                rs.getString("Marca"),
                                
                                Sector.fromString(rs.getString("sector"))
                        );

                companies.add(company);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return companies;
    }
    
    /**
     * Consulta el dinero actual del jugador almacenado en la partida guardada.
     *
     * @return el saldo del jugador en la columna {@code dinero} de
     *         {@code Partidas_guardadas}, o {@code 0} si no existe registro
     *         o se produce un error.
     */
    public double getDineroJugador() {
        String sql = "SELECT dinero FROM partidas_guardadas "  // ← ajusta el nombre de columna
                   + "WHERE usuario_id = 1 LIMIT 1;";
        try (
            Connection conn = DatabaseManager.getConnection();
            Statement stmt  = conn.createStatement();
            ResultSet rs    = stmt.executeQuery(sql)
        ) {
            if (rs.next()) return rs.getDouble("dinero");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Persiste el saldo actual del jugador en la base de datos.
     * <p>
     * Actualiza únicamente la columna {@code dinero} de
     * {@code Partidas_guardadas} para {@code usuario_id = 1}, sin
     * modificar fecha, hora ni acciones.
     * </p>
     *
     * @param dinero nuevo saldo del jugador que se escribirá en BD.
     */
    public void guardarDinero(double dinero) {
        String sql = "UPDATE Partidas_guardadas SET dinero = ? "
                   + "WHERE usuario_id = 1;";
        try (
            Connection conn = DatabaseManager.getConnection();
            java.sql.PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setDouble(1, dinero);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Guarda o actualiza el número de acciones que el jugador posee de
     * una empresa concreta, siguiendo un patrón <em>upsert</em>:
     * <ol>
     *   <li>Intenta actualizar la fila existente en {@code Stats}.</li>
     *   <li>Si no existía ninguna fila afectada, la inserta.</li>
     * </ol>
     *
     * @param empresaId id de la empresa ({@code Stats.empresa_id}).
     * @param nAcciones número de acciones a almacenar; debe ser mayor que
     *                  cero (para eliminar un registro usa
     *                  {@link #eliminarAcciones(int)}).
     */
    public void guardarAcciones(int empresaId, int nAcciones) {
        // Intentar actualizar primero
        String sqlUpdate =
            "UPDATE Stats SET n_acciones = ? "
          + "WHERE partida_id = (SELECT id FROM Partidas_guardadas "
          + "WHERE usuario_id = 1 LIMIT 1) "
          + "AND empresa_id = ?;";

        String sqlInsert =
            "INSERT INTO Stats (partida_id, empresa_id, n_acciones) "
          + "SELECT id, ?, ? FROM Partidas_guardadas "
          + "WHERE usuario_id = 1 LIMIT 1;";

        try (Connection conn = DatabaseManager.getConnection()) {

            java.sql.PreparedStatement ps =
                    conn.prepareStatement(sqlUpdate);
            ps.setInt(1, nAcciones);
            ps.setInt(2, empresaId);
            int filas = ps.executeUpdate();

            // Si no existía la fila, insertarla
            if (filas == 0) {
                java.sql.PreparedStatement ps2 =
                        conn.prepareStatement(sqlInsert);
                ps2.setInt(1, empresaId);
                ps2.setInt(2, nAcciones);
                ps2.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Elimina el registro de acciones del jugador para una empresa
     * determinada, equivalente a tener {@code 0} acciones en cartera.
     * <p>
     * Se invoca típicamente desde {@link system.Stats.StatsModel} cuando
     * el jugador vende todas sus acciones de una empresa.
     * </p>
     *
     * @param empresaId id de la empresa cuyas acciones se eliminarán de
     *                  la tabla {@code Stats}.
     */
    public void eliminarAcciones(int empresaId) {
        String sql =
            "DELETE FROM Stats "
          + "WHERE empresa_id = ? "
          + "AND partida_id = (SELECT id FROM Partidas_guardadas "
          + "WHERE usuario_id = 1 LIMIT 1);";
        try (
            Connection conn = DatabaseManager.getConnection();
            java.sql.PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, empresaId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Guarda el estado completo de la partida activa: dinero, fecha y hora
     * de juego en un único {@code UPDATE}.
     * <p>
     * Equivale a un "guardado rápido" que puede invocarse en cualquier
     * momento sin alterar las acciones en cartera del jugador.
     * </p>
     *
     * @param dinero saldo actual del jugador que se persistirá.
     * @param clock  reloj del juego del que se extraen año, mes, día,
     *               hora y minuto actuales para escribirlos en BD.
     */
    public void guardarPartida(double dinero, Clock clock) {
        String sql =
            "UPDATE Partidas_guardadas "
          + "SET dinero = ?, anyo = ?, mes = ?, dia = ?, "
          + "    hora = ?, minuto = ? "
          + "WHERE usuario_id = 1;";
        try (
            Connection conn = DatabaseManager.getConnection();
            java.sql.PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setDouble(1, dinero);
            ps.setInt(2, clock.getDate().getYear());
            ps.setInt(3, clock.getDate().getMonth());
            ps.setInt(4, clock.getDate().getDay());
            ps.setInt(5, clock.getHour());
            ps.setInt(6, clock.getMinute());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Resetea la partida del usuario a su estado inicial en dos pasos
     * atómicos dentro de la misma conexión:
     * <ol>
     *   <li>Borra todas las filas de {@code Stats} asociadas a la partida
     *       del usuario (elimina toda la cartera de acciones).</li>
     *   <li>Restaura el dinero y la fecha/hora de inicio en
     *       {@code Partidas_guardadas}.</li>
     * </ol>
     * <p>
     * Este método actúa únicamente sobre la capa de persistencia; el
     * llamador es responsable de reiniciar los objetos en memoria
     * (p. ej. {@link system.Player#reset(double)} y {@link tools.Clock}).
     * </p>
     *
     * @param dineroInicial  saldo con el que comenzará la nueva partida.
     * @param anyoInicial    año  de inicio del reloj de juego (ej. {@code 1996}).
     * @param mesInicial     mes  de inicio del reloj de juego (ej. {@code 6}).
     * @param diaInicial     día  de inicio del reloj de juego (ej. {@code 1}).
     * @param horaInicial    hora de inicio del reloj de juego (ej. {@code 8}).
     * @param minutoInicial  minuto de inicio del reloj de juego (ej. {@code 0}).
     */
    public void resetearPartida(double dineroInicial,
                                int anyoInicial, int mesInicial, int diaInicial,
                                int horaInicial, int minutoInicial) {
        String sqlDeleteStats =
            "DELETE FROM Stats "
          + "WHERE partida_id = (SELECT id FROM Partidas_guardadas "
          + "                    WHERE usuario_id = 1 LIMIT 1);";

        String sqlResetPartida =
            "UPDATE Partidas_guardadas "
          + "SET dinero = ?, anyo = ?, mes = ?, dia = ?, "
          + "    hora = ?, minuto = ? "
          + "WHERE usuario_id = 1;";

        try (Connection conn = DatabaseManager.getConnection()) {

            // 1. Borrar acciones del usuario
            try (java.sql.PreparedStatement ps =
                         conn.prepareStatement(sqlDeleteStats)) {
                ps.executeUpdate();
            }

            // 2. Restaurar dinero y fecha
            try (java.sql.PreparedStatement ps =
                         conn.prepareStatement(sqlResetPartida)) {
                ps.setDouble(1, dineroInicial);
                ps.setInt(2, anyoInicial);
                ps.setInt(3, mesInicial);
                ps.setInt(4, diaInicial);
                ps.setInt(5, horaInicial);
                ps.setInt(6, minutoInicial);
                ps.executeUpdate();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}