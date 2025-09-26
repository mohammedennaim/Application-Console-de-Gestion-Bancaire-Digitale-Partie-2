package org.bank.domain;

import java.util.UUID;

public class Manager extends User {

    public Manager() {
        super.setRole(Role.MANAGER);
    }

    public Manager(UUID id, String username, String passwordHash, String fullName) {
        super(id, username, passwordHash, fullName, Role.MANAGER, true);
    }
}
