package org.bank.repository;

import java.util.UUID;

import org.bank.domain.Client;

public interface ClientRepository {
    Client findById(UUID id);
    boolean save(Client client);
    boolean delete(Client client);
}

