package org.bank.repository;

import java.util.UUID;

import org.bank.domain.Client;

public interface ClientRepository {
    boolean findById(UUID id);
    boolean save(Client client);
    boolean update(Client client);
    boolean delete(UUID id);
    String getNationalIdByClientId(UUID clientId);
    Client getClientById(UUID clientId);
}

