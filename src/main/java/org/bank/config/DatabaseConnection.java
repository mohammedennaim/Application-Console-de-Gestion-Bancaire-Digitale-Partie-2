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

    /**
     * Ferme la connexion à la base de données de manière sécurisée
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔌 Connexion PostgreSQL fermée correctement.");
            }
        } catch (SQLException e) {
            System.err.println("⚠️ Erreur lors de la fermeture de la connexion: " + e.getMessage());
        } finally {
            connection = null;
            instance = null; // Réinitialiser l'instance pour permettre une nouvelle création
        }
    }

    /**
     * Méthode pour nettoyer les ressources et forcer la fermeture
     * Utile pour éviter les threads qui traînent
     */
    public static void cleanup() {
        if (instance != null) {
            instance.closeConnection();
        }
        
        // Forcer l'arrêt des threads PostgreSQL de nettoyage
        try {
            // Enregistrer le driver pour le désenregistrer
            java.sql.DriverManager.getDrivers().asIterator().forEachRemaining(driver -> {
                try {
                    if (driver.getClass().getName().contains("postgresql")) {
                        java.sql.DriverManager.deregisterDriver(driver);
                        System.out.println("🔌 Driver PostgreSQL désenregistré: " + driver.getClass().getName());
                    }
                } catch (Exception e) {
                    System.err.println("⚠️ Erreur désenregistrement driver: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("⚠️ Erreur lors du nettoyage des drivers: " + e.getMessage());
        }
        
        // Forcer le garbage collection pour nettoyer les références
        System.gc();
        System.runFinalization();
    }
}
