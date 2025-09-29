package org.bank.repository;

import java.util.UUID;

import org.bank.domain.Client;

public interface ClientRepository {
    boolean findById(UUID id);
    boolean save(Client client);
    
    // Nouvelles méthodes pour la validation des transferts
    String getNationalIdByClientId(UUID clientId);
    Client getClientById(UUID clientId);
}

