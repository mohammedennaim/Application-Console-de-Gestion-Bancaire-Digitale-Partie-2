package org.bank.domain;

public class Admin extends User {

    public Admin() {
        super.setRole(Role.ADMIN);
    }

    public Admin(long id, String username, String passwordHash, String fullName) {
        super(id, username, passwordHash, fullName, Role.ADMIN, true);
    }
}
