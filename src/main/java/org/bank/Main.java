package org.bank;

import org.bank.ui.UiTeller;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        System.out.println("🏦 DÉMARRAGE DU SYSTÈME BANCAIRE 🏦");
        System.out.println("=".repeat(50));
        
        try {
            // Lancement de l'interface guichetier
            UiTeller uiTeller = new UiTeller();
            uiTeller.showMenu();
            uiTeller.fermer();
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion à la base de données:");
            System.err.println("   " + e.getMessage());
            System.err.println("🔧 Vérifiez votre configuration de base de données.");
            
        } catch (Exception e) {
            System.err.println("❌ Erreur inattendue lors du démarrage:");
            System.err.println("   " + e.getMessage());
            e.printStackTrace();
            
        } finally {
            System.out.println("\n👋 Fermeture du système bancaire.");
            System.out.println("   Merci d'avoir utilisé notre application !");
        }
    }
}