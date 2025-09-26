-- Complete reset and recreation with UUID types
-- Drop and recreate all tables with proper UUID types

-- Clear all existing data and constraints
DROP TABLE IF EXISTS rapports CASCADE;
DROP TABLE IF EXISTS statistiques CASCADE;
DROP TABLE IF EXISTS historique CASCADE;
DROP TABLE IF EXISTS transactions CASCADE;
DROP TABLE IF EXISTS credits CASCADE;
DROP TABLE IF EXISTS fee_rules CASCADE;
DROP TABLE IF EXISTS accounts CASCADE;
DROP TABLE IF EXISTS clients CASCADE;
DROP TABLE IF EXISTS auditors CASCADE;
DROP TABLE IF EXISTS tellers CASCADE;
DROP TABLE IF EXISTS managers CASCADE;
DROP TABLE IF EXISTS admins CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Base table for user inheritance
CREATE TABLE users (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role user_role NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP
);

-- Admin users table - inherits from users
CREATE TABLE admins () INHERITS (users);

-- Manager users table - inherits from users
CREATE TABLE managers () INHERITS (users);

-- Teller users table - inherits from users
CREATE TABLE tellers () INHERITS (users);

-- Auditor users table - inherits from users
CREATE TABLE auditors () INHERITS (users);

-- Client users table - inherits from users (database inheritance only)
CREATE TABLE clients (
    national_id VARCHAR(20) UNIQUE,
    monthly_income DECIMAL(12,2),
    email VARCHAR(100),
    phone VARCHAR(20),
    birth_date DATE
) INHERITS (users);

-- Accounts table with UUID IDs (reference users table instead of clients for inheritance compatibility)
CREATE TABLE accounts (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    client_id UUID REFERENCES users(id) ON DELETE CASCADE,
    account_type account_type NOT NULL,
    balance DECIMAL(15,2) DEFAULT 0.00,
    currency_code VARCHAR(3) DEFAULT 'MAD',
    overdraft_allowed BOOLEAN DEFAULT FALSE,
    overdraft_limit DECIMAL(15,2) DEFAULT 0.00,
    closed BOOLEAN DEFAULT FALSE,
    opened_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    closed_at TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Fee rules table
CREATE TABLE fee_rules (
    id SERIAL PRIMARY KEY,
    operation_type operation_type NOT NULL,
    fee_mode fee_mode NOT NULL,
    fee_value DECIMAL(10,4) NOT NULL,
    currency_code VARCHAR(3) DEFAULT 'MAD',
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Credits table with UUID references
CREATE TABLE credits (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    linked_account_id UUID REFERENCES accounts(id) ON DELETE CASCADE,
    requested_amount DECIMAL(15,2) NOT NULL,
    approved_amount DECIMAL(15,2),
    interest_rate DECIMAL(5,4) NOT NULL,
    term_months INTEGER NOT NULL,
    status credit_status DEFAULT 'PENDING',
    interest_mode interest_mode DEFAULT 'SIMPLE',
    requested_by_client_id UUID REFERENCES users(id),
    approved_by_user_id UUID REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    approved_at TIMESTAMP,
    completed_at TIMESTAMP
);

-- Transactions table with UUID IDs
CREATE TABLE transactions (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    transaction_type transaction_type NOT NULL,
    transaction_status transaction_status DEFAULT 'PENDING',
    source_account_id UUID REFERENCES accounts(id),
    target_account_id UUID REFERENCES accounts(id),
    amount DECIMAL(15,2) NOT NULL,
    fee DECIMAL(10,2) DEFAULT 0.00,
    currency_code VARCHAR(3) DEFAULT 'MAD',
    initiated_by_user_id UUID REFERENCES users(id),
    external_reference VARCHAR(50),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    executed_at TIMESTAMP
);

-- Historique (History/Audit) table with UUID references
CREATE TABLE historique (
    id SERIAL PRIMARY KEY,
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    action action_type NOT NULL,
    entity_type entity_type NOT NULL,
    entity_id UUID NOT NULL, -- UUID references to transactions, credits, or accounts
    details TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Statistiques table
CREATE TABLE statistiques (
    id SERIAL PRIMARY KEY,
    metric_name VARCHAR(100) NOT NULL,
    metric_value DECIMAL(15,2) NOT NULL,
    period_start DATE NOT NULL,
    period_end DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Rapports table
CREATE TABLE rapports (
    id SERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    generated_by_user_id UUID REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    period_start DATE,
    period_end DATE
);

-- Insert test data with proper UUIDs
-- ===== USERS =====
-- Insert base users first
INSERT INTO users (id, username, password_hash, full_name, role) VALUES
    -- Clients (base data)
    ('550e8400-e29b-41d4-a716-446655440000'::uuid, 'client.test', '$2b$10$encrypted_hash', 'Test Client', 'CLIENT'),
    ('550e8400-e29b-41d4-a716-446655440001'::uuid, 'john.doe', '$2b$10$encrypted_hash', 'John Doe', 'CLIENT'),
    ('550e8400-e29b-41d4-a716-446655440002'::uuid, 'marie.martin', '$2b$10$encrypted_hash', 'Marie Martin', 'CLIENT'),
    ('550e8400-e29b-41d4-a716-446655440003'::uuid, 'ahmed.alaoui', '$2b$10$encrypted_hash', 'Ahmed Alaoui', 'CLIENT'),
    
    -- Admin users
    ('660e8400-e29b-41d4-a716-446655440001'::uuid, 'admin.test', '$2b$10$encrypted_hash', 'Test Admin', 'ADMIN'),
    ('660e8400-e29b-41d4-a716-446655440002'::uuid, 'admin.system', '$2b$10$encrypted_hash', 'System Administrator', 'ADMIN'),
    
    -- Manager users
    ('770e8400-e29b-41d4-a716-446655440001'::uuid, 'manager.branch', '$2b$10$encrypted_hash', 'Branch Manager', 'MANAGER'),
    ('770e8400-e29b-41d4-a716-446655440002'::uuid, 'manager.risk', '$2b$10$encrypted_hash', 'Risk Manager', 'MANAGER'),
    
    -- Teller users
    ('880e8400-e29b-41d4-a716-446655440001'::uuid, 'teller.window1', '$2b$10$encrypted_hash', 'Teller Window 1', 'TELLER'),
    ('880e8400-e29b-41d4-a716-446655440002'::uuid, 'teller.window2', '$2b$10$encrypted_hash', 'Teller Window 2', 'TELLER'),
    
    -- Auditor users
    ('990e8400-e29b-41d4-a716-446655440001'::uuid, 'auditor.internal', '$2b$10$encrypted_hash', 'Internal Auditor', 'AUDITOR'),
    ('990e8400-e29b-41d4-a716-446655440002'::uuid, 'auditor.compliance', '$2b$10$encrypted_hash', 'Compliance Auditor', 'AUDITOR');

-- ===== SPECIALIZED TABLES (with inheritance) =====
-- Insert clients with specific attributes
INSERT INTO clients (id, username, password_hash, full_name, role, national_id, monthly_income, email, phone, birth_date) VALUES
    ('550e8400-e29b-41d4-a716-446655440000'::uuid, 'client.test', '$2b$10$encrypted_hash', 'Test Client', 'CLIENT', 'MA123456789', 15000.00, 'test@example.com', '+212612345678', '1990-01-01'),
    ('550e8400-e29b-41d4-a716-446655440001'::uuid, 'john.doe', '$2b$10$encrypted_hash', 'John Doe', 'CLIENT', 'MA987654321', 25000.00, 'john.doe@email.com', '+212687654321', '1985-05-15'),
    ('550e8400-e29b-41d4-a716-446655440002'::uuid, 'marie.martin', '$2b$10$encrypted_hash', 'Marie Martin', 'CLIENT', 'MA456789123', 18000.00, 'marie.martin@email.com', '+212698765432', '1992-12-08'),
    ('550e8400-e29b-41d4-a716-446655440003'::uuid, 'ahmed.alaoui', '$2b$10$encrypted_hash', 'Ahmed Alaoui', 'CLIENT', 'MA321654987', 30000.00, 'ahmed.alaoui@email.com', '+212654987321', '1988-03-22');

-- Insert admins (inheritance)
INSERT INTO admins (id, username, password_hash, full_name, role) VALUES
    ('660e8400-e29b-41d4-a716-446655440001'::uuid, 'admin.test', '$2b$10$encrypted_hash', 'Test Admin', 'ADMIN'),
    ('660e8400-e29b-41d4-a716-446655440002'::uuid, 'admin.system', '$2b$10$encrypted_hash', 'System Administrator', 'ADMIN');

-- Insert managers (inheritance)
INSERT INTO managers (id, username, password_hash, full_name, role) VALUES
    ('770e8400-e29b-41d4-a716-446655440001'::uuid, 'manager.branch', '$2b$10$encrypted_hash', 'Branch Manager', 'MANAGER'),
    ('770e8400-e29b-41d4-a716-446655440002'::uuid, 'manager.risk', '$2b$10$encrypted_hash', 'Risk Manager', 'MANAGER');

-- Insert tellers (inheritance)
INSERT INTO tellers (id, username, password_hash, full_name, role) VALUES
    ('880e8400-e29b-41d4-a716-446655440001'::uuid, 'teller.window1', '$2b$10$encrypted_hash', 'Teller Window 1', 'TELLER'),
    ('880e8400-e29b-41d4-a716-446655440002'::uuid, 'teller.window2', '$2b$10$encrypted_hash', 'Teller Window 2', 'TELLER');

-- Insert auditors (inheritance)
INSERT INTO auditors (id, username, password_hash, full_name, role) VALUES
    ('990e8400-e29b-41d4-a716-446655440001'::uuid, 'auditor.internal', '$2b$10$encrypted_hash', 'Internal Auditor', 'AUDITOR'),
    ('990e8400-e29b-41d4-a716-446655440002'::uuid, 'auditor.compliance', '$2b$10$encrypted_hash', 'Compliance Auditor', 'AUDITOR');

-- ===== ACCOUNTS =====
INSERT INTO accounts (id, client_id, account_type, balance, currency_code, overdraft_allowed, overdraft_limit) VALUES
    -- Test Client accounts
    ('1723acda-3145-48b0-8440-b3126a95834c'::uuid, '550e8400-e29b-41d4-a716-446655440000'::uuid, 'COURANT', 5000.00, 'MAD', true, 2000.00),
    ('2723acda-3145-48b0-8440-b3126a95834c'::uuid, '550e8400-e29b-41d4-a716-446655440000'::uuid, 'EPARGNE', 10000.00, 'MAD', false, 0.00),
    
    -- John Doe accounts
    ('1823acda-3145-48b0-8440-b3126a95834c'::uuid, '550e8400-e29b-41d4-a716-446655440001'::uuid, 'COURANT', 12000.00, 'MAD', true, 5000.00),
    ('2823acda-3145-48b0-8440-b3126a95834c'::uuid, '550e8400-e29b-41d4-a716-446655440001'::uuid, 'EPARGNE', 25000.00, 'MAD', false, 0.00),
    ('3823acda-3145-48b0-8440-b3126a95834c'::uuid, '550e8400-e29b-41d4-a716-446655440001'::uuid, 'CREDIT', 0.00, 'MAD', false, 0.00),
    
    -- Marie Martin accounts  
    ('1923acda-3145-48b0-8440-b3126a95834c'::uuid, '550e8400-e29b-41d4-a716-446655440002'::uuid, 'COURANT', 8500.00, 'MAD', true, 3000.00),
    ('2923acda-3145-48b0-8440-b3126a95834c'::uuid, '550e8400-e29b-41d4-a716-446655440002'::uuid, 'EPARGNE', 15000.00, 'MAD', false, 0.00),
    
    -- Ahmed Alaoui accounts
    ('1a23acda-3145-48b0-8440-b3126a95834c'::uuid, '550e8400-e29b-41d4-a716-446655440003'::uuid, 'COURANT', 22000.00, 'MAD', true, 10000.00),
    ('2a23acda-3145-48b0-8440-b3126a95834c'::uuid, '550e8400-e29b-41d4-a716-446655440003'::uuid, 'EPARGNE', 50000.00, 'MAD', false, 0.00);

-- ===== FEE_RULES =====
INSERT INTO fee_rules (operation_type, fee_mode, fee_value, currency_code, active) VALUES
    ('TRANSFER_EXTERNAL', 'FIX', 15.00, 'MAD', true),
    ('WITHDRAW_FOREIGN_CURRENCY', 'PERCENT', 0.025, 'MAD', true),
    ('TRANSFER_EXTERNAL', 'PERCENT', 0.015, 'EUR', true),
    ('WITHDRAW_FOREIGN_CURRENCY', 'FIX', 5.00, 'USD', true);

-- ===== CREDITS =====
INSERT INTO credits (id, linked_account_id, requested_amount, approved_amount, interest_rate, term_months, status, interest_mode, requested_by_client_id, approved_by_user_id) VALUES
    -- Test Client credit request
    ('4723acda-3145-48b0-8440-b3126a95834c'::uuid, '1723acda-3145-48b0-8440-b3126a95834c'::uuid, 50000.00, 45000.00, 0.075, 36, 'APPROVED', 'COMPOUND', '550e8400-e29b-41d4-a716-446655440000'::uuid, '770e8400-e29b-41d4-a716-446655440001'::uuid),
    
    -- John Doe credit requests
    ('4823acda-3145-48b0-8440-b3126a95834c'::uuid, '1823acda-3145-48b0-8440-b3126a95834c'::uuid, 100000.00, 90000.00, 0.065, 60, 'ACTIVE', 'COMPOUND', '550e8400-e29b-41d4-a716-446655440001'::uuid, '770e8400-e29b-41d4-a716-446655440002'::uuid),
    ('4923acda-3145-48b0-8440-b3126a95834c'::uuid, '2823acda-3145-48b0-8440-b3126a95834c'::uuid, 25000.00, NULL, 0.055, 24, 'PENDING', 'SIMPLE', '550e8400-e29b-41d4-a716-446655440001'::uuid, NULL),
    
    -- Marie Martin credit
    ('4a23acda-3145-48b0-8440-b3126a95834c'::uuid, '1923acda-3145-48b0-8440-b3126a95834c'::uuid, 75000.00, 75000.00, 0.058, 48, 'ACTIVE', 'COMPOUND', '550e8400-e29b-41d4-a716-446655440002'::uuid, '770e8400-e29b-41d4-a716-446655440001'::uuid),
    
    -- Ahmed Alaoui credit
    ('4b23acda-3145-48b0-8440-b3126a95834c'::uuid, '1a23acda-3145-48b0-8440-b3126a95834c'::uuid, 200000.00, 180000.00, 0.048, 72, 'COMPLETED', 'COMPOUND', '550e8400-e29b-41d4-a716-446655440003'::uuid, '770e8400-e29b-41d4-a716-446655440002'::uuid);

-- ===== TRANSACTIONS =====
INSERT INTO transactions (id, transaction_type, transaction_status, source_account_id, target_account_id, amount, fee, initiated_by_user_id, description) VALUES
    -- Transfers between Test Client accounts
    ('3723acda-3145-48b0-8440-b3126a95834c'::uuid, 'TRANSFER_IN', 'SETTLED', '1723acda-3145-48b0-8440-b3126a95834c'::uuid, '2723acda-3145-48b0-8440-b3126a95834c'::uuid, 1000.00, 0.00, '880e8400-e29b-41d4-a716-446655440001'::uuid, 'Transfer to savings'),
    ('3823acda-3145-48b0-8440-b3126a95834c'::uuid, 'DEPOSIT', 'SETTLED', NULL, '1723acda-3145-48b0-8440-b3126a95834c'::uuid, 2000.00, 0.00, '880e8400-e29b-41d4-a716-446655440001'::uuid, 'Cash deposit'),
    ('3923acda-3145-48b0-8440-b3126a95834c'::uuid, 'WITHDRAW', 'SETTLED', '2723acda-3145-48b0-8440-b3126a95834c'::uuid, NULL, 500.00, 0.00, '880e8400-e29b-41d4-a716-446655440002'::uuid, 'ATM withdrawal'),
    
    -- John Doe transactions
    ('3a23acda-3145-48b0-8440-b3126a95834c'::uuid, 'TRANSFER_EXTERNAL', 'SETTLED', '1823acda-3145-48b0-8440-b3126a95834c'::uuid, NULL, 3000.00, 15.00, '880e8400-e29b-41d4-a716-446655440001'::uuid, 'External transfer to other bank'),
    ('3b23acda-3145-48b0-8440-b3126a95834c'::uuid, 'DEPOSIT', 'SETTLED', NULL, '2823acda-3145-48b0-8440-b3126a95834c'::uuid, 5000.00, 0.00, '880e8400-e29b-41d4-a716-446655440002'::uuid, 'Salary deposit'),
    
    -- Marie Martin transactions
    ('3c23acda-3145-48b0-8440-b3126a95834c'::uuid, 'TRANSFER_IN', 'SETTLED', '1923acda-3145-48b0-8440-b3126a95834c'::uuid, '2923acda-3145-48b0-8440-b3126a95834c'::uuid, 1500.00, 0.00, '880e8400-e29b-41d4-a716-446655440001'::uuid, 'Monthly savings transfer'),
    ('3d23acda-3145-48b0-8440-b3126a95834c'::uuid, 'WITHDRAW', 'PENDING', '1923acda-3145-48b0-8440-b3126a95834c'::uuid, NULL, 800.00, 0.00, '880e8400-e29b-41d4-a716-446655440002'::uuid, 'Pending withdrawal'),
    
    -- Ahmed Alaoui transactions
    ('3e23acda-3145-48b0-8440-b3126a95834c'::uuid, 'DEPOSIT', 'SETTLED', NULL, '1a23acda-3145-48b0-8440-b3126a95834c'::uuid, 10000.00, 0.00, '880e8400-e29b-41d4-a716-446655440001'::uuid, 'Business revenue deposit'),
    ('3f23acda-3145-48b0-8440-b3126a95834c'::uuid, 'TRANSFER_EXTERNAL', 'FAILED', '2a23acda-3145-48b0-8440-b3126a95834c'::uuid, NULL, 25000.00, 15.00, '880e8400-e29b-41d4-a716-446655440002'::uuid, 'Failed international transfer');

-- ===== HISTORIQUE =====
INSERT INTO historique (user_id, action, entity_type, entity_id, details) VALUES
    -- Account creation history
    ('880e8400-e29b-41d4-a716-446655440001'::uuid, 'DEPOSIT', 'ACCOUNT', '1723acda-3145-48b0-8440-b3126a95834c'::uuid, 'Account opened with initial deposit'),
    ('880e8400-e29b-41d4-a716-446655440002'::uuid, 'DEPOSIT', 'ACCOUNT', '2723acda-3145-48b0-8440-b3126a95834c'::uuid, 'Savings account opened'),
    
    -- Credit request history
    ('770e8400-e29b-41d4-a716-446655440001'::uuid, 'REQUEST_CREDIT', 'CREDIT', '4723acda-3145-48b0-8440-b3126a95834c'::uuid, 'Credit request approved by manager'),
    ('770e8400-e29b-41d4-a716-446655440002'::uuid, 'REQUEST_CREDIT', 'CREDIT', '4823acda-3145-48b0-8440-b3126a95834c'::uuid, 'Large credit request processed'),
    ('770e8400-e29b-41d4-a716-446655440001'::uuid, 'REQUEST_CREDIT', 'CREDIT', '4a23acda-3145-48b0-8440-b3126a95834c'::uuid, 'Credit approved for home purchase'),
    
    -- Transaction history
    ('880e8400-e29b-41d4-a716-446655440001'::uuid, 'DEPOSIT', 'TRANSACTION', '3823acda-3145-48b0-8440-b3126a95834c'::uuid, 'Large cash deposit processed'),
    ('880e8400-e29b-41d4-a716-446655440002'::uuid, 'DEPOSIT', 'TRANSACTION', '3b23acda-3145-48b0-8440-b3126a95834c'::uuid, 'Monthly salary deposit'),
    
    -- Account closure attempts
    ('990e8400-e29b-41d4-a716-446655440001'::uuid, 'CLOSE_ACCOUNT', 'ACCOUNT', '3823acda-3145-48b0-8440-b3126a95834c'::uuid, 'Account closure reviewed by auditor'),
    ('660e8400-e29b-41d4-a716-446655440001'::uuid, 'CLOSE_ACCOUNT', 'ACCOUNT', '3823acda-3145-48b0-8440-b3126a95834c'::uuid, 'Admin approved account closure');

-- ===== STATISTIQUES =====
INSERT INTO statistiques (metric_name, metric_value, period_start, period_end) VALUES
    ('Total Deposits', 28500.00, '2025-09-01', '2025-09-30'),
    ('Total Withdrawals', 1300.00, '2025-09-01', '2025-09-30'),
    ('Active Accounts', 9, '2025-09-01', '2025-09-30'),
    ('New Clients', 4, '2025-09-01', '2025-09-30'),
    ('Credits Approved', 4, '2025-09-01', '2025-09-30'),
    ('Total Credit Amount', 390000.00, '2025-09-01', '2025-09-30'),
    ('Average Account Balance', 16611.11, '2025-09-01', '2025-09-30'),
    ('Transaction Volume', 9, '2025-09-01', '2025-09-30'),
    ('Fee Revenue', 45.00, '2025-09-01', '2025-09-30');

-- ===== RAPPORTS =====
INSERT INTO rapports (title, content, generated_by_user_id, period_start, period_end) VALUES
    ('Monthly Activity Report - September 2025', 
     'Summary: Total deposits: 28,500 MAD. Total withdrawals: 1,300 MAD. Net flow: +27,200 MAD. 
      New accounts opened: 9. Credit applications: 5 (4 approved, 1 pending).
      Notable transactions: Large business deposit of 10,000 MAD by Ahmed Alaoui.
      Risk assessment: Low risk profile maintained across all accounts.',
     '770e8400-e29b-41d4-a716-446655440001'::uuid, '2025-09-01', '2025-09-30'),
    
    ('Credit Risk Assessment Report',
     'Credit portfolio analysis: Total outstanding: 390,000 MAD across 4 active credits.
      Risk distribution: Low risk: 75%, Medium risk: 25%, High risk: 0%.
      Recommendations: Continue current credit policies. Monitor John Doe pending application.
      Default rate: 0% (excellent performance). Average interest rate: 6.15%.',
     '770e8400-e29b-41d4-a716-446655440002'::uuid, '2025-09-01', '2025-09-30'),
    
    ('Compliance Audit Report',
     'Audit findings: All transactions comply with regulatory requirements.
      AML compliance: 100% - all large transactions properly documented.
      KYC verification: Complete for all clients. Documentation score: Excellent.
      Recommendations: Maintain current compliance procedures. Schedule next audit in 6 months.',
     '990e8400-e29b-41d4-a716-446655440002'::uuid, '2025-09-01', '2025-09-30'),
    
    ('Branch Performance Summary',
     'Branch metrics: Customer satisfaction: 95%. Transaction processing time: Average 2.3 minutes.
      Teller performance: Window 1: 98% accuracy, Window 2: 97% accuracy.
      Queue management: Average wait time 4.2 minutes during peak hours.
      Recommendations: Consider additional staff during lunch hours.',
     '770e8400-e29b-41d4-a716-446655440001'::uuid, '2025-09-01', '2025-09-30');