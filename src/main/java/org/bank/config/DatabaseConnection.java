package org.bank.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static volatile DatabaseConnection instance;
    private Connection connection;
    private final String URL = "jdbc:postgresql://localhost:5432/bank_application";
    private final String USER = "postgres";
    private final String PASSWORD = "123456789";
    private boolean reconnectionMessageShown = false;

    private DatabaseConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            this.connection = createConnection();
        } catch (ClassNotFoundException e) {
            throw new SQLException("PostgreSQL Driver not found: " + e.getMessage());
        }
    }

    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        // Vérifier si la connexion est fermée et la recréer si nécessaire
        if (connection == null || connection.isClosed()) {
            synchronized (this) {
                if (connection == null || connection.isClosed()) {
                    if (!reconnectionMessageShown) {
                        System.out.println("Reconnexion à la base de données...");
                        reconnectionMessageShown = true;
                    }
                    connection = createConnection();
                }
            }
        }
        return connection;
    }
}
