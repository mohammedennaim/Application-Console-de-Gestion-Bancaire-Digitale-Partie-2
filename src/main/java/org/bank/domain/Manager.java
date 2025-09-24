package org.bank.domain;

public class Manager extends User {

    public Manager() {
        super.setRole(Role.MANAGER);
    }

    public Manager(long id, String username, String passwordHash, String fullName) {
        super(id, username, passwordHash, fullName, Role.MANAGER, true);
    }
}
