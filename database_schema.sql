-- Script d'insertion de données sécurisé pour le système de gestion bancaire
-- Version sans transaction globale pour éviter les blocages

-- Nettoyage préventif (optionnel)
-- DELETE FROM rapports;
-- DELETE FROM statistiques;
-- DELETE FROM historique;
-- DELETE FROM transactions;
-- DELETE FROM credits;
-- DELETE FROM fee_rules;
-- DELETE FROM accounts;
-- DELETE FROM clients;
-- DELETE FROM admins;
-- DELETE FROM managers;
-- DELETE FROM tellers;
-- DELETE FROM auditors;
-- DELETE FROM users;

-- Insertion des utilisateurs dans la table parent 'users'
INSERT INTO
    users (
        id,
        username,
        password_hash,
        full_name,
        role,
        active
    )
VALUES
    -- Admins
    (
        '550e8400-e29b-41d4-a716-446655440001',
        'admin1',
        '$2a$12$LQv3c1yqBw2LbGjrX5n8seQGl7cU0x5v9KYXl5Z8VpZYKyZ8xHuKe',
        'Mohamed Admin',
        'ADMIN',
        true
    ),
    -- Managers
    (
        '550e8400-e29b-41d4-a716-446655440002',
        'manager1',
        '$2a$12$LQv3c1yqBw2LbGjrX5n8seQGl7cU0x5v9KYXl5Z8VpZYKyZ8xHuKe',
        'Fatima Manager',
        'MANAGER',
        true
    ),
    -- Tellers
    (
        '550e8400-e29b-41d4-a716-446655440003',
        'teller1',
        '$2a$12$LQv3c1yqBw2LbGjrX5n8seQGl7cU0x5v9KYXl5Z8VpZYKyZ8xHuKe',
        'Ahmed Guichetier',
        'TELLER',
        true
    ),
    -- Auditors
    (
        '550e8400-e29b-41d4-a716-446655440004',
        'auditor1',
        '$2a$12$LQv3c1yqBw2LbGjrX5n8seQGl7cU0x5v9KYXl5Z8VpZYKyZ8xHuKe',
        'Aicha Auditeur',
        'AUDITOR',
        true
    ),
    -- Clients
    (
        '550e8400-e29b-41d4-a716-446655440010',
        'client1',
        '$2a$12$LQv3c1yqBw2LbGjrX5n8seQGl7cU0x5v9KYXl5Z8VpZYKyZ8xHuKe',
        'Youssef Alami',
        'CLIENT',
        true
    ),
    (
        '550e8400-e29b-41d4-a716-446655440011',
        'client2',
        '$2a$12$LQv3c1yqBw2LbGjrX5n8seQGl7cU0x5v9KYXl5Z8VpZYKyZ8xHuKe',
        'Khadija Benali',
        'CLIENT',
        true
    ),
    (
        '550e8400-e29b-41d4-a716-446655440012',
        'client3',
        '$2a$12$LQv3c1yqBw2LbGjrX5n8seQGl7cU0x5v9KYXl5Z8VpZYKyZ8xHuKe',
        'Omar Tazi',
        'CLIENT',
        true
    ),
    (
        '550e8400-e29b-41d4-a716-446655440013',
        'client4',
        '$2a$12$LQv3c1yqBw2LbGjrX5n8seQGl7cU0x5v9KYXl5Z8VpZYKyZ8xHuKe',
        'Salma Idrissi',
        'CLIENT',
        true
    ),
    (
        '550e8400-e29b-41d4-a716-446655440014',
        'client5',
        '$2a$12$LQv3c1yqBw2LbGjrX5n8seQGl7cU0x5v9KYXl5Z8VpZYKyZ8xHuKe',
        'Hassan Rifai',
        'CLIENT',
        true
    ) ON CONFLICT (id) DO NOTHING;

-- Insertion des clients avec informations supplémentaires
INSERT INTO
    clients (
        id,
        username,
        password_hash,
        full_name,
        role,
        national_id,
        monthly_income,
        email,
        phone,
        birth_date,
        active
    )
VALUES (
        '550e8400-e29b-41d4-a716-446655440010',
        'client1',
        '$2a$12$LQv3c1yqBw2LbGjrX5n8seQGl7cU0x5v9KYXl5Z8VpZYKyZ8xHuKe',
        'Youssef Alami',
        'CLIENT',
        'A123456789',
        5000.00,
        'youssef.alami@email.com',
        '0661234567',
        '1985-03-15',
        true
    ),
    (
        '550e8400-e29b-41d4-a716-446655440011',
        'client2',
        '$2a$12$LQv3c1yqBw2LbGjrX5n8seQGl7cU0x5v9KYXl5Z8VpZYKyZ8xHuKe',
        'Khadija Benali',
        'CLIENT',
        'B987654321',
        7500.00,
        'khadija.benali@email.com',
        '0662345678',
        '1990-07-22',
        true
    ),
    (
        '550e8400-e29b-41d4-a716-446655440012',
        'client3',
        '$2a$12$LQv3c1yqBw2LbGjrX5n8seQGl7cU0x5v9KYXl5Z8VpZYKyZ8xHuKe',
        'Omar Tazi',
        'CLIENT',
        'C456789123',
        3200.00,
        'omar.tazi@email.com',
        '0663456789',
        '1988-12-10',
        true
    ),
    (
        '550e8400-e29b-41d4-a716-446655440013',
        'client4',
        '$2a$12$LQv3c1yqBw2LbGjrX5n8seQGl7cU0x5v9KYXl5Z8VpZYKyZ8xHuKe',
        'Salma Idrissi',
        'CLIENT',
        'D789123456',
        4800.00,
        'salma.idrissi@email.com',
        '0664567890',
        '1992-05-08',
        true
    ),
    (
        '550e8400-e29b-41d4-a716-446655440014',
        'client5',
        '$2a$12$LQv3c1yqBw2LbGjrX5n8seQGl7cU0x5v9KYXl5Z8VpZYKyZ8xHuKe',
        'Hassan Rifai',
        'CLIENT',
        'E321654987',
        6200.00,
        'hassan.rifai@email.com',
        '0665678901',
        '1987-11-30',
        true
    ) ON CONFLICT (id) DO NOTHING;

-- Insertion des comptes
INSERT INTO
    accounts (
        id,
        client_id,
        account_type,
        balance,
        currency_code,
        overdraft_allowed,
        overdraft_limit
    )
VALUES (
        'BK-2025-0001',
        '550e8400-e29b-41d4-a716-446655440010',
        'COURANT',
        15000.00,
        'MAD',
        true,
        2000.00
    ),
    (
        'BK-2025-0002',
        '550e8400-e29b-41d4-a716-446655440010',
        'EPARGNE',
        25000.00,
        'MAD',
        false,
        0.00
    ),
    (
        'BK-2025-0003',
        '550e8400-e29b-41d4-a716-446655440011',
        'COURANT',
        8500.00,
        'MAD',
        true,
        1500.00
    ),
    (
        'BK-2025-0004',
        '550e8400-e29b-41d4-a716-446655440011',
        'EPARGNE',
        12000.00,
        'MAD',
        false,
        0.00
    ),
    (
        'BK-2025-0005',
        '550e8400-e29b-41d4-a716-446655440012',
        'COURANT',
        3200.00,
        'MAD',
        false,
        0.00
    ),
    (
        'BK-2025-0006',
        '550e8400-e29b-41d4-a716-446655440013',
        'COURANT',
        7800.00,
        'MAD',
        true,
        1000.00
    ),
    (
        'BK-2025-0007',
        '550e8400-e29b-41d4-a716-446655440013',
        'EPARGNE',
        18500.00,
        'MAD',
        false,
        0.00
    ),
    (
        'BK-2025-0008',
        '550e8400-e29b-41d4-a716-446655440014',
        'COURANT',
        9600.00,
        'MAD',
        true,
        2500.00
    ),
    (
        'BK-2025-0009',
        '550e8400-e29b-41d4-a716-446655440014',
        'EPARGNE',
        22000.00,
        'EUR',
        false,
        0.00
    ) ON CONFLICT (id) DO NOTHING;

-- Insertion des règles de frais
INSERT INTO
    fee_rules (
        operation_type,
        fee_mode,
        fee_value,
        currency_code,
        active
    )
VALUES (
        'TRANSFER_EXTERNAL',
        'FIX',
        10.00,
        'MAD',
        true
    ),
    (
        'TRANSFER_EXTERNAL',
        'PERCENT',
        0.015,
        'EUR',
        true
    ),
    (
        'TRANSFER_EXTERNAL',
        'PERCENT',
        0.02,
        'USD',
        true
    ),
    (
        'WITHDRAW_FOREIGN_CURRENCY',
        'FIX',
        25.00,
        'MAD',
        true
    ),
    (
        'WITHDRAW_FOREIGN_CURRENCY',
        'PERCENT',
        0.025,
        'EUR',
        true
    ),
    (
        'WITHDRAW_FOREIGN_CURRENCY',
        'PERCENT',
        0.03,
        'USD',
        true
    );

-- Insertion des crédits
INSERT INTO
    credits (
        id,
        client_id,
        linked_account_id,
        principal,
        annual_rate,
        duration_months,
        interest_mode,
        credit_status,
        penalty_rate,
        start_date,
        end_date
    )
VALUES (
        'CR-2025-0001',
        '550e8400-e29b-41d4-a716-446655440010',
        'BK-2025-0001',
        50000.00,
        0.055000,
        36,
        'SIMPLE',
        'ACTIVE',
        0.02,
        '2025-01-15',
        '2028-01-15'
    ),
    (
        'CR-2025-0002',
        '550e8400-e29b-41d4-a716-446655440011',
        'BK-2025-0003',
        30000.00,
        0.048000,
        24,
        'SIMPLE',
        'ACTIVE',
        0.015,
        '2025-02-01',
        '2027-02-01'
    ),
    (
        'CR-2025-0003',
        '550e8400-e29b-41d4-a716-446655440013',
        'BK-2025-0006',
        75000.00,
        0.062000,
        60,
        'COMPOUND',
        'PENDING',
        0.025,
        NULL,
        NULL
    ),
    (
        'CR-2025-0004',
        '550e8400-e29b-41d4-a716-446655440014',
        'BK-2025-0008',
        40000.00,
        0.052000,
        30,
        'SIMPLE',
        'APPROVED',
        0.018,
        NULL,
        NULL
    ) ON CONFLICT (id) DO NOTHING;

-- Insertion des transactions
INSERT INTO
    transactions (
        id,
        transaction_type,
        transaction_status,
        source_account_id,
        target_account_id,
        amount,
        fee,
        currency_code,
        initiated_by_user_id,
        description
    )
VALUES (
        'TX-2025-0001',
        'DEPOSIT',
        'SETTLED',
        NULL,
        'BK-2025-0001',
        5000.00,
        0.00,
        'MAD',
        '550e8400-e29b-41d4-a716-446655440003',
        'Dépôt en espèces'
    ),
    (
        'TX-2025-0002',
        'WITHDRAW',
        'SETTLED',
        'BK-2025-0001',
        NULL,
        1200.00,
        0.00,
        'MAD',
        '550e8400-e29b-41d4-a716-446655440003',
        'Retrait au guichet'
    ),
    (
        'TX-2025-0003',
        'TRANSFER_IN',
        'SETTLED',
        'BK-2025-0002',
        'BK-2025-0001',
        3000.00,
        0.00,
        'MAD',
        '550e8400-e29b-41d4-a716-446655440010',
        'Virement interne épargne vers courant'
    ),
    (
        'TX-2025-0004',
        'TRANSFER_OUT',
        'SETTLED',
        'BK-2025-0002',
        'BK-2025-0001',
        3000.00,
        0.00,
        'MAD',
        '550e8400-e29b-41d4-a716-446655440010',
        'Virement interne épargne vers courant'
    ),
    (
        'TX-2025-0005',
        'DEPOSIT',
        'SETTLED',
        NULL,
        'BK-2025-0003',
        2500.00,
        0.00,
        'MAD',
        '550e8400-e29b-41d4-a716-446655440003',
        'Dépôt par chèque'
    ),
    (
        'TX-2025-0006',
        'TRANSFER_EXTERNAL',
        'SETTLED',
        'BK-2025-0006',
        NULL,
        500.00,
        10.00,
        'MAD',
        '550e8400-e29b-41d4-a716-446655440013',
        'Virement externe vers Attijariwafa Bank'
    ),
    (
        'TX-2025-0007',
        'DEPOSIT',
        'SETTLED',
        NULL,
        'BK-2025-0008',
        1800.00,
        0.00,
        'MAD',
        '550e8400-e29b-41d4-a716-446655440003',
        'Dépôt par virement'
    ),
    (
        'TX-2025-0008',
        'WITHDRAW',
        'PENDING',
        'BK-2025-0005',
        NULL,
        800.00,
        0.00,
        'MAD',
        '550e8400-e29b-41d4-a716-446655440012',
        'Retrait en cours de traitement'
    ) ON CONFLICT (id) DO NOTHING;

-- Insertion de l'historique
INSERT INTO
    historique (
        user_id,
        action,
        entity_type,
        entity_id,
        details
    )
VALUES (
        '550e8400-e29b-41d4-a716-446655440010',
        'DEPOSIT',
        'TRANSACTION',
        'TX-2025-0001',
        'Client a effectué un dépôt de 5000 MAD'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440003',
        'DEPOSIT',
        'TRANSACTION',
        'TX-2025-0001',
        'Guichetier a traité le dépôt'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440011',
        'REQUEST_CREDIT',
        'CREDIT',
        'CR-2025-0002',
        'Demande de crédit de 30000 MAD sur 24 mois'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440002',
        'REQUEST_CREDIT',
        'CREDIT',
        'CR-2025-0002',
        'Manager a approuvé la demande de crédit'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440013',
        'REQUEST_CREDIT',
        'CREDIT',
        'CR-2025-0003',
        'Demande de crédit de 75000 MAD en attente'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440012',
        'CLOSE_ACCOUNT',
        'ACCOUNT',
        'BK-2025-0005',
        'Demande de fermeture de compte en cours'
    );

-- Insertion des statistiques
INSERT INTO
    statistiques (
        solde_total_banque,
        revenus_credits,
        nombre_transactions,
        nombre_comptes,
        nombre_credits_actifs,
        periode
    )
VALUES (
        125600.00,
        2850.75,
        8,
        9,
        2,
        '2025-09'
    ),
    (
        118200.00,
        2650.50,
        6,
        9,
        2,
        '2025-08'
    ),
    (
        112500.00,
        2420.25,
        5,
        8,
        1,
        '2025-07'
    );

-- Insertion des rapports
INSERT INTO rapports (title, period, statistique_id, report_lines) VALUES
                                                                       ('Rapport Mensuel Septembre 2025', '2025-09', 1, ARRAY[
                                                                           'Solde total de la banque: 125,600.00 MAD',
                                                                           'Revenus des crédits: 2,850.75 MAD',
                                                                           'Nombre total de transactions: 8',
                                                                           'Nombre de comptes actifs: 9',
                                                                           'Nombre de crédits actifs: 2',
                                                                           'Croissance par rapport au mois précédent: +6.2%'
                                                                           ]),
                                                                       ('Rapport Mensuel Août 2025', '2025-08', 2, ARRAY[
                                                                           'Solde total de la banque: 118,200.00 MAD',
                                                                           'Revenus des crédits: 2,650.50 MAD',
                                                                           'Nombre total de transactions: 6',
                                                                           'Nombre de comptes actifs: 9',
                                                                           'Nombre de crédits actifs: 2',
                                                                           'Croissance par rapport au mois précédent: +5.1%'
                                                                           ]);

-- Mise à jour des timestamps
UPDATE users
SET
    last_login_at = CURRENT_TIMESTAMP - INTERVAL '2 hours'
WHERE
    username IN ('admin1', 'manager1');

UPDATE users
SET
    last_login_at = CURRENT_TIMESTAMP - INTERVAL '1 day'
WHERE
    username = 'teller1';

UPDATE users
SET
    last_login_at = CURRENT_TIMESTAMP - INTERVAL '3 hours'
WHERE
    username IN ('client1', 'client2');