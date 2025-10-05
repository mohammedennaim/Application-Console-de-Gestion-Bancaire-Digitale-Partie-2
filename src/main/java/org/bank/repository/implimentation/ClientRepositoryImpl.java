package org.bank.repository.implimentation;

import org.bank.config.DatabaseConnection;
import org.bank.domain.Client;
import org.bank.domain.Currency;
import org.bank.repository.ClientRepository;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.time.LocalDate;

public class ClientRepositoryImpl implements ClientRepository {
    private final DatabaseConnection connection;

    public ClientRepositoryImpl() throws SQLException {
        this.connection = DatabaseConnection.getInstance();
    }

    public boolean findById(UUID id) {
        final Optional<Client> client = Optional.ofNullable(getClientById(id));
        return client.isPresent();
    }

    public boolean save(Client client) {
        try (Connection cnx = this.connection.getConnection()) {
            
            String sqlUsers = """
            INSERT INTO users (
                id, username, password_hash, full_name, role, active,
                created_at, updated_at, last_login_at
            ) VALUES (?::uuid, ?, 'CLIENT_NO_PASSWORD', ?, ?::user_role, ?, ?, ?, ?)
            """;
            
            try (PreparedStatement psUsers = cnx.prepareStatement(sqlUsers)) {
                psUsers.setString(1, client.getId().toString());
                psUsers.setString(2, client.getUsername());
                psUsers.setString(3, client.getFullName());
                psUsers.setString(4, client.getRole());
                psUsers.setBoolean(5, client.isActive());
                psUsers.setObject(6, client.getCreatedAt());
                psUsers.setObject(7, client.getUpdatedAt());
                psUsers.setObject(8, client.getLastLoginAt());
                
                psUsers.executeUpdate();
                System.out.println("Données utilisateur insérées pour le client : " + client.getId());
            }
            
            String sqlClients = """
            INSERT INTO clients (
                id, username, full_name, role, active,
                created_at, updated_at, last_login_at,
                national_id, monthly_income, currency_code, email, phone, birth_date
            ) VALUES (?::uuid, ?, ?, ?::user_role, ?, ?, ?, ?, ?, ?, ?::currency_enum, ?, ?, ?)
            """;
            
            try (PreparedStatement psClients = cnx.prepareStatement(sqlClients)) {
                psClients.setString(1, client.getId().toString());
                psClients.setString(2, client.getUsername());
                psClients.setString(3, client.getFullName());
                psClients.setString(4, client.getRole());
                psClients.setBoolean(5, client.isActive());
                psClients.setObject(6, client.getCreatedAt());
                psClients.setObject(7, client.getUpdatedAt());
                psClients.setObject(8, client.getLastLoginAt());
                psClients.setString(9, client.getNationalId());
                psClients.setBigDecimal(10, client.getMonthlyIncome());
                psClients.setString(11, client.getCurrency() != null ? client.getCurrency().getCode() : "MAD");
                psClients.setString(12, client.getEmail());
                psClients.setString(13, client.getPhone());
                psClients.setObject(14, client.getBirthDate());
                
                psClients.executeUpdate();
                System.out.println("Données client insérées : " + client.getId());
            }
            
           
            System.out.println("Client créé avec succès dans les deux tables : " + client.getId());
            return true;
            
        } catch (SQLException e) {
           System.out.println(e.getMessage());
        }
        return false;
    }

    public boolean update(Client client) {
        try (Connection cnx = this.connection.getConnection()) {
            String sqlUsers = """
            UPDATE users 
            SET username = ?, full_name = ?, updated_at = ?
            WHERE id = ?::uuid
            """;
            
            try (PreparedStatement psUsers = cnx.prepareStatement(sqlUsers)) {
                psUsers.setString(1, client.getUsername());
                psUsers.setString(2, client.getFullName());
                psUsers.setObject(3, client.getUpdatedAt());
                psUsers.setString(4, client.getId().toString());
                
                psUsers.executeUpdate();
                System.out.println("Données utilisateur mises à jour pour le client : " + client.getId());
            }
            
            String sqlClients = """
            UPDATE clients 
            SET username = ?, full_name = ?, national_id = ?, 
                monthly_income = ?, email = ?, phone = ?, 
                birth_date = ?, updated_at = ?
            WHERE id = ?::uuid
            """;
            try (PreparedStatement psClients = cnx.prepareStatement(sqlClients)) {
                psClients.setString(1, client.getUsername());
                psClients.setString(2, client.getFullName());
                psClients.setString(3, client.getNationalId());
                psClients.setBigDecimal(4, client.getMonthlyIncome());
                psClients.setString(5, client.getEmail());
                psClients.setString(6, client.getPhone());
                psClients.setObject(7, client.getBirthDate());
                psClients.setObject(8, client.getUpdatedAt());
                psClients.setString(9, client.getId().toString());
                
                int rowsAffected = psClients.executeUpdate();
                System.out.println("client mises à jour avec succès : " + client.getId());
                return rowsAffected > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du client: " + e.getMessage());
        }
        return false;
    }
    public boolean delete(UUID id) {
        String sql = "UPDATE clients SET active = false, updated_at = ? WHERE id = ?::uuid";
        try (PreparedStatement ps = connection.getConnection().prepareStatement(sql)) {
            ps.setTimestamp(1, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
            ps.setString(2, id.toString());
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du client: " + e.getMessage());
        }
        return false;
    }
    @Override
    public String getNationalIdByClientId(UUID clientId) {
        if (clientId == null) {
            System.err.println("Erreur: clientId ne peut pas être null");
        }
        
        String sql = """
        SELECT c.national_id
        FROM users u
        LEFT JOIN clients c ON u.id = c.id
        WHERE u.id = ?::uuid AND u.role = 'CLIENT'
        """;
        
        try (Connection cnx = this.connection.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, clientId.toString());
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getString("national_id");
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du nationalId: " + e.getMessage());
        }
        return "DEFAULT-ID-UNKNOWN";
    }

    @Override
    public Client getClientById(UUID clientId) {
        String sql = """
        SELECT u.*, c.national_id, c.monthly_income, c.email, c.phone, c.birth_date, c.currency_code 
        FROM users u 
        LEFT JOIN clients c ON u.id = c.id 
        WHERE u.id = ?::uuid AND u.role = 'CLIENT'
        """;
        
        try (Connection cnx = this.connection.getConnection();
             PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, clientId.toString());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Client client = new Client();
                client.setId((UUID) rs.getObject("id"));
                client.setUsername(rs.getString("username"));
                client.setFullName(rs.getString("full_name"));
                client.setRole(rs.getString("role"));
                client.setActive(rs.getBoolean("active"));
                client.setCreatedAt(rs.getTimestamp("created_at") != null ? 
                    rs.getTimestamp("created_at").toLocalDateTime() : null);
                client.setUpdatedAt(rs.getTimestamp("updated_at") != null ? 
                    rs.getTimestamp("updated_at").toLocalDateTime() : null);
                client.setLastLoginAt(rs.getTimestamp("last_login_at") != null ? 
                    rs.getTimestamp("last_login_at").toLocalDateTime() : null);       
                client.setNationalId(rs.getString("national_id") != null ? rs.getString("national_id") : "DEFAULT-ID");
                client.setMonthlyIncome(rs.getBigDecimal("monthly_income") != null ? rs.getBigDecimal("monthly_income") : new BigDecimal("5000.00"));
                client.setCurrency(rs.getString("currency_code") != null ? Currency.fromCode(rs.getString("currency_code")) : Currency.MAD);
                client.setEmail(rs.getString("email") != null ? rs.getString("email") : "client@example.com");
                client.setPhone(rs.getString("phone") != null ? rs.getString("phone") : "+212600000000");
                client.setBirthDate(rs.getDate("birth_date") != null ? 
                    rs.getDate("birth_date").toLocalDate() : LocalDate.of(1990, 1, 1));
                
                return client;
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du client: " + e.getMessage());
        }
        return null;
    }

}
