package database;

import tools.CompanyData;

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
                + "    ca.n_acciones AS acciones_usuario,\r\n"
                + "    e.valor_accion AS valor_accion,\r\n"
                + "    e.marca\r\n"
                + "FROM empresas e\r\n"
                + "JOIN Stats ca\r\n"
                + "    ON e.id = ca.empresa_id\r\n"
                + "JOIN partidas_guardadas pg\r\n"
                + "    ON ca.partida_id = pg.id\r\n"
                + "WHERE pg.usuario_id = 1;";

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
                                
                                rs.getString("Marca")
                        );

                companies.add(company);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return companies;
    }
}