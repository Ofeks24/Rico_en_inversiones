package database;

import tools.CompanyData;
import tools.Sector;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

public class CompanyRepository {

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
    
 // Guardar n_acciones de una empresa para el usuario
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

    // Eliminar registro cuando las acciones llegan a 0
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
}