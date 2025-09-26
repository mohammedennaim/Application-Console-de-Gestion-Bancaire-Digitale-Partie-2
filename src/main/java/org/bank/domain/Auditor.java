package org.bank.domain;

import java.util.UUID;

public class Auditor extends User {

    public Auditor() {
        super.setRole(Role.AUDITOR);
    }

    public Auditor(UUID id, String username, String passwordHash, String fullName) {
        super(id, username, passwordHash, fullName, Role.AUDITOR, true);
    }
}