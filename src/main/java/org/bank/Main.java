package org.bank;

import org.bank.ui.UiTeller;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException {
        UiTeller uiTeller = new UiTeller();
        try {
            uiTeller.showMenu();
        } finally {
            uiTeller.fermer();
        }
    }
}