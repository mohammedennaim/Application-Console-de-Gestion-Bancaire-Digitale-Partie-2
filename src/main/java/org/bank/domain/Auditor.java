package org.bank.domain;

public class Auditor extends User {

    public Auditor() {
        super.setRole(Role.AUDITOR);
    }

    public Auditor(long id, String username, String passwordHash, String fullName) {
        super(id, username, passwordHash, fullName, Role.AUDITOR, true);
    }
}