package org.bank.service;

import org.bank.repository.implimentation.AccountRepositoryImpl;
import org.bank.repository.implimentation.ClientRepositoryImpl;
import org.bank.domain.Account;

import java.sql.SQLException;
import java.util.UUID;

public class TransferValidationService {
    private final AccountRepositoryImpl accountRepository;
    private final ClientRepositoryImpl clientRepository;

    public TransferValidationService() throws SQLException {
        this.accountRepository = new AccountRepositoryImpl();
        this.clientRepository = new ClientRepositoryImpl();
    }

    /**
     * Valide un transfert entre deux comptes
     * @param sourceAccountId ID du compte source
     * @param targetAccountId ID du compte cible
     * @return TransferValidationResult contenant le résultat de la validation
     */
    public TransferValidationResult validateTransfer(UUID sourceAccountId, UUID targetAccountId) {
        TransferValidationResult result = new TransferValidationResult();

        // 1. Vérifier que les IDs ne sont pas null
        if (sourceAccountId == null) {
            result.setValid(false);
            result.setErrorMessage("L'ID du compte source ne peut pas être null");
            return result;
        }

        if (targetAccountId == null) {
            result.setValid(false);
            result.setErrorMessage("L'ID du compte cible ne peut pas être null");
            return result;
        }

        // 2. Vérifier que les comptes ne sont pas identiques
        if (sourceAccountId.equals(targetAccountId)) {
            result.setValid(false);
            result.setErrorMessage("Le compte source et le compte cible ne peuvent pas être identiques");
            return result;
        }

        // 3. Vérifier l'existence du compte source
        if (!accountRepository.accountExists(sourceAccountId)) {
            result.setValid(false);
            result.setErrorMessage("Le compte source avec l'ID " + sourceAccountId + " n'existe pas");
            return result;
        }

        // 4. Vérifier l'existence du compte cible
        if (!accountRepository.accountExists(targetAccountId)) {
            result.setValid(false);
            result.setErrorMessage("Le compte cible avec l'ID " + targetAccountId + " n'existe pas");
            return result;
        }

        // 5. Récupérer les IDs des clients propriétaires
        UUID sourceClientId = accountRepository.getClientIdByAccountId(sourceAccountId);
        UUID targetClientId = accountRepository.getClientIdByAccountId(targetAccountId);

        if (sourceClientId == null) {
            result.setValid(false);
            result.setErrorMessage("Impossible de récupérer le propriétaire du compte source");
            return result;
        }

        if (targetClientId == null) {
            result.setValid(false);
            result.setErrorMessage("Impossible de récupérer le propriétaire du compte cible");
            return result;
        }

        // 6. Récupérer les nationalId des clients
        String sourceNationalId = clientRepository.getNationalIdByClientId(sourceClientId);
        String targetNationalId = clientRepository.getNationalIdByClientId(targetClientId);

        if (sourceNationalId == null) {
            result.setValid(false);
            result.setErrorMessage("Impossible de récupérer le nationalId du client source");
            return result;
        }

        if (targetNationalId == null) {
            result.setValid(false);
            result.setErrorMessage("Impossible de récupérer le nationalId du client cible");
            return result;
        }

        // 7. Vérifier que les nationalId sont différents (pas de transfert vers soi-même)
        if (sourceNationalId.equals(targetNationalId)) {
            result.setValid(false);
            result.setErrorMessage("Transfert refusé : le compte source et le compte cible appartiennent au même client (nationalId: " + sourceNationalId + ")");
            return result;
        }

        // 8. Vérifier que les comptes ne sont pas fermés
        Account sourceAccount = accountRepository.getAccountById(sourceAccountId);
        Account targetAccount = accountRepository.getAccountById(targetAccountId);

        if (sourceAccount != null && sourceAccount.isClosed()) {
            result.setValid(false);
            result.setErrorMessage("Le compte source est fermé");
            return result;
        }

        if (targetAccount != null && targetAccount.isClosed()) {
            result.setValid(false);
            result.setErrorMessage("Le compte cible est fermé");
            return result;
        }

        // Si toutes les validations passent
        result.setValid(true);
        result.setSuccessMessage("Transfert validé : comptes valides et appartiennent à des clients différents");
        result.setSourceNationalId(sourceNationalId);
        result.setTargetNationalId(targetNationalId);

        return result;
    }

    /**
     * Classe pour encapsuler le résultat de la validation
     */
    public static class TransferValidationResult {
        private boolean valid;
        private String errorMessage;
        private String successMessage;
        private String sourceNationalId;
        private String targetNationalId;

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getSuccessMessage() {
            return successMessage;
        }

        public void setSuccessMessage(String successMessage) {
            this.successMessage = successMessage;
        }

        public String getSourceNationalId() {
            return sourceNationalId;
        }

        public void setSourceNationalId(String sourceNationalId) {
            this.sourceNationalId = sourceNationalId;
        }

        public String getTargetNationalId() {
            return targetNationalId;
        }

        public void setTargetNationalId(String targetNationalId) {
            this.targetNationalId = targetNationalId;
        }

        @Override
        public String toString() {
            if (valid) {
                return "✓ " + successMessage + " (Source: " + sourceNationalId + ", Cible: " + targetNationalId + ")";
            } else {
                return "✗ " + errorMessage;
            }
        }
    }
}