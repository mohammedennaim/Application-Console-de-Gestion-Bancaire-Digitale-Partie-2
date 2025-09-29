package org.bank.repository.implimentation;

import org.bank.domain.Client;
import org.bank.repository.TellerRepository;

import java.sql.SQLException;
import java.util.UUID;

public class TellerRepositoryImpl implements TellerRepository {
    ClientRepositoryImpl clientImpl= new ClientRepositoryImpl();

    public TellerRepositoryImpl() throws SQLException {
    }

    public boolean findClientParTeller(Client client){
        return clientImpl.findById(UUID.fromString(client.getId().toString()));
    }

    public boolean createClient(Client client){
        clientImpl.save(client);
        return true;
    }
}
