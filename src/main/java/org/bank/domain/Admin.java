package org.bank.domain;

import java.util.UUID;

public class Admin extends User {

    public Admin() {
        super.setRole(Role.ADMIN);
    }

    public Admin(UUID id, String username, String passwordHash, String fullName) {
        super(id, username, passwordHash, fullName, Role.ADMIN, true);
    }
}
