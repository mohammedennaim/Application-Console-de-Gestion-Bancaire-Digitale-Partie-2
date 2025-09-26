package org.bank.repository.implimentation;

import org.bank.config.DatabaseConnection;
import org.bank.domain.Client;
import org.bank.repository.ClientRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class ClientRepositoryImpl implements ClientRepository {
    private final DatabaseConnection connection;

    public ClientRepositoryImpl() throws SQLException {
        this.connection = DatabaseConnection.getInstance();
    }

    public Client findById(UUID id) {
        Connection cnx = this.connection.getConnection();
        String sql = "SELECT * FROM clients WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setObject(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Client client = new Client();
                // Mapping tous les champs de la table clients
                client.setId((UUID) rs.getObject("id"));
                client.setUsername(rs.getString("username"));
                client.setPasswordHash(rs.getString("password_hash"));
                client.setFullName(rs.getString("full_name"));
                client.setRole(rs.getString("role"));
                client.setActive(rs.getBoolean("active"));
                client.setCreatedAt(rs.getTimestamp("created_at") != null ? 
                    rs.getTimestamp("created_at").toLocalDateTime() : null);
                client.setUpdatedAt(rs.getTimestamp("updated_at") != null ? 
                    rs.getTimestamp("updated_at").toLocalDateTime() : null);
                client.setLastLoginAt(rs.getTimestamp("last_login_at") != null ? 
                    rs.getTimestamp("last_login_at").toLocalDateTime() : null);
                
                // Champs spécifiques aux clients
                client.setNationalId(rs.getString("national_id"));
                client.setMonthlyIncome(rs.getBigDecimal("monthly_income"));
                client.setEmail(rs.getString("email"));
                client.setPhone(rs.getString("phone"));
                client.setBirthDate(rs.getDate("birth_date") != null ? 
                    rs.getDate("birth_date").toLocalDate() : null);
                
                return client;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean save(Client client) {
        Connection cnx = this.connection.getConnection();
        String sql = """
        INSERT INTO clients (
            id, username, password_hash, full_name, role, active,
            created_at, updated_at, last_login_at,
            national_id, monthly_income, email, phone, birth_date
        ) VALUES (?, ?, ?, ?, ?::user_role, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setObject(1, client.getId());
            ps.setString(2, client.getUsername());
            ps.setString(3, client.getPasswordHash());
            ps.setString(4, client.getFullName());
            ps.setString(5, client.getRole());
            ps.setBoolean(6, client.isActive());
            ps.setObject(7, client.getCreatedAt());
            ps.setObject(8, client.getUpdatedAt());
            ps.setObject(9, client.getLastLoginAt());
            ps.setString(10, client.getNationalId());
            ps.setBigDecimal(11, client.getMonthlyIncome());
            ps.setString(12, client.getEmail());
            ps.setString(13, client.getPhone());
            ps.setObject(14, client.getBirthDate());

            ps.executeUpdate();
            System.out.println("Client inséré avec succès : " + client.getId());
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean delete(Client client){
        if (client == null || client.getId() == null) {
            System.out.println("Client ou ID client invalide");
            return false;
        }
        Connection cnx = this.connection.getConnection();
        String sql = "DELETE FROM clients WHERE id = ?;";
        try(PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setObject(1, client.getId());
            ps.executeUpdate();
            System.out.println("Client et données associées supprimés avec succès");
            return true;
        }catch (Exception e){
            System.out.println(e.getStackTrace());
        }
        return false;
    }
}
