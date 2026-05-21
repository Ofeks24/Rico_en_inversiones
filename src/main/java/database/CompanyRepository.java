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
                "SELECT * FROM Empresas";

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

                                rs.getString("Nombre"),

                                rs.getString("Actividad"),

                                rs.getInt("Total_acciones"),

                                0,

                                rs.getDouble(
                                        "Valor_accion"
                                )
                        );

                companies.add(company);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return companies;
    }
}