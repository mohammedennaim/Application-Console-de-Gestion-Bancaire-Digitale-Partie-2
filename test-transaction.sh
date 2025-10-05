#!/bin/bash

# Script pour tester automatiquement le TransactionService
echo "=== TEST AUTOMATIQUE DU TRANSACTIONSERVICE ==="

# Lancer le programme avec l'option 2 (TransactionService)
echo "2" | timeout 30 mvn exec:java -Dexec.mainClass="org.bank.Main"

echo ""
echo "=== FIN DU TEST ==="