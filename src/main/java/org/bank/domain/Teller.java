package org.bank.domain;

import java.util.UUID;

public class Teller extends User {

    public Teller() {
        super.setRole(Role.TELLER);
    }

    public Teller(UUID id, String username, String passwordHash, String fullName) {
        super(id, username, passwordHash, fullName, Role.TELLER, true);
    }
}