package org.bank.domain;

public class Teller extends User {

    public Teller() {
        super.setRole(Role.TELLER);
    }

    public Teller(long id, String username, String passwordHash, String fullName) {
        super(id, username, passwordHash, fullName, Role.TELLER, true);
    }
}