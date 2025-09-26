-- Database Schema for Bank Management System
-- Run this script BEFORE running database_schema.sql

-- Drop existing tables and types if they exist
DROP TABLE IF EXISTS rapports CASCADE;
DROP TABLE IF EXISTS statistiques CASCADE;
DROP TABLE IF EXISTS historique CASCADE;
DROP TABLE IF EXISTS transactions CASCADE;
DROP TABLE IF EXISTS credits CASCADE;
DROP TABLE IF EXISTS fee_rules CASCADE;
DROP TABLE IF EXISTS accounts CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS clients CASCADE;
DROP TABLE IF EXISTS auditors CASCADE;
DROP TABLE IF EXISTS tellers CASCADE;
DROP TABLE IF EXISTS managers CASCADE;
DROP TABLE IF EXISTS admins CASCADE;

-- Drop ENUM types if they exist
DROP TYPE IF EXISTS user_role CASCADE;
DROP TYPE IF EXISTS account_type CASCADE;
DROP TYPE IF EXISTS transaction_type CASCADE;
DROP TYPE IF EXISTS transaction_status CASCADE;
DROP TYPE IF EXISTS credit_status CASCADE;
DROP TYPE IF EXISTS interest_mode CASCADE;
DROP TYPE IF EXISTS fee_mode CASCADE;
DROP TYPE IF EXISTS operation_type CASCADE;
DROP TYPE IF EXISTS action_type CASCADE;
DROP TYPE IF EXISTS entity_type CASCADE;

-- Create ENUM types
CREATE TYPE user_role AS ENUM ('ADMIN', 'MANAGER', 'TELLER', 'AUDITOR', 'CLIENT');
CREATE TYPE account_type AS ENUM ('COURANT', 'EPARGNE', 'CREDIT');
CREATE TYPE transaction_type AS ENUM ('DEPOSIT', 'WITHDRAW', 'TRANSFER_IN', 'TRANSFER_OUT', 'TRANSFER_EXTERNAL');
CREATE TYPE transaction_status AS ENUM ('PENDING', 'SETTLED', 'CANCELLED', 'FAILED');
CREATE TYPE credit_status AS ENUM ('PENDING', 'APPROVED', 'REJECTED', 'ACTIVE', 'COMPLETED', 'DEFAULTED');
CREATE TYPE interest_mode AS ENUM ('SIMPLE', 'COMPOUND');
CREATE TYPE fee_mode AS ENUM ('FIX', 'PERCENT');
CREATE TYPE operation_type AS ENUM ('TRANSFER_EXTERNAL', 'WITHDRAW_FOREIGN_CURRENCY');
CREATE TYPE action_type AS ENUM ('DEPOSIT', 'REQUEST_CREDIT', 'CLOSE_ACCOUNT');
CREATE TYPE entity_type AS ENUM ('TRANSACTION', 'CREDIT', 'ACCOUNT');

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

-- Admin users table
CREATE TABLE admins (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role user_role DEFAULT 'ADMIN',
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP
);

-- Manager users table
CREATE TABLE managers (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role user_role DEFAULT 'MANAGER',
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP
);

-- Teller users table
CREATE TABLE tellers (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role user_role DEFAULT 'TELLER',
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP
);

-- Auditor users table
CREATE TABLE auditors (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role user_role DEFAULT 'AUDITOR',
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP
);

-- Client users table (with additional client-specific fields)
CREATE TABLE clients (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role user_role DEFAULT 'CLIENT',
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP,
    -- Client-specific fields
    national_id VARCHAR(20) UNIQUE,
    monthly_income DECIMAL(15,2),
    email VARCHAR(100),
    phone VARCHAR(20),
    birth_date DATE
);

-- Accounts table
CREATE TABLE accounts (
    id VARCHAR(20) PRIMARY KEY, -- Format: BK-2025-0001
    client_id UUID REFERENCES clients(id) ON DELETE CASCADE,
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

-- Credits table
CREATE TABLE credits (
    id VARCHAR(20) PRIMARY KEY, -- Format: CR-2025-0001
    client_id UUID REFERENCES clients(id) ON DELETE CASCADE,
    linked_account_id VARCHAR(20) REFERENCES accounts(id),
    principal DECIMAL(15,2) NOT NULL,
    annual_rate DECIMAL(8,6) NOT NULL, -- e.g., 0.060000 for 6%
    duration_months INTEGER NOT NULL,
    interest_mode interest_mode DEFAULT 'SIMPLE',
    credit_status credit_status DEFAULT 'PENDING',
    penalty_rate DECIMAL(8,6) DEFAULT 0.000000,
    start_date DATE,
    end_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Transactions table
CREATE TABLE transactions (
    id VARCHAR(20) PRIMARY KEY, -- Format: TX-2025-0001
    transaction_type transaction_type NOT NULL,
    transaction_status transaction_status DEFAULT 'PENDING',
    source_account_id VARCHAR(20) REFERENCES accounts(id),
    target_account_id VARCHAR(20) REFERENCES accounts(id),
    amount DECIMAL(15,2) NOT NULL,
    fee DECIMAL(10,2) DEFAULT 0.00,
    currency_code VARCHAR(3) DEFAULT 'MAD',
    initiated_by_user_id UUID REFERENCES users(id),
    external_reference VARCHAR(50),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    executed_at TIMESTAMP
);

-- Historique (History/Audit) table
CREATE TABLE historique (
    id SERIAL PRIMARY KEY,
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    action action_type NOT NULL,
    entity_type entity_type NOT NULL,
    entity_id VARCHAR(20) NOT NULL, -- Can reference transactions, credits, or accounts
    details TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Statistiques (Statistics) table
CREATE TABLE statistiques (
    id SERIAL PRIMARY KEY,
    solde_total_banque DECIMAL(20,2) DEFAULT 0.00,
    revenus_credits DECIMAL(15,2) DEFAULT 0.00,
    nombre_transactions INTEGER DEFAULT 0,
    nombre_comptes INTEGER DEFAULT 0,
    nombre_credits_actifs INTEGER DEFAULT 0,
    periode VARCHAR(10), -- e.g., '2025-09'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Rapports (Reports) table
CREATE TABLE rapports (
    id SERIAL PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    period VARCHAR(10), -- e.g., '2025-09'
    statistique_id INTEGER REFERENCES statistiques(id) ON DELETE CASCADE,
    report_lines TEXT[], -- Array of report lines
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_accounts_client_id ON accounts(client_id);
CREATE INDEX idx_accounts_type ON accounts(account_type);
CREATE INDEX idx_transactions_source ON transactions(source_account_id);
CREATE INDEX idx_transactions_target ON transactions(target_account_id);
CREATE INDEX idx_transactions_status ON transactions(transaction_status);
CREATE INDEX idx_transactions_type ON transactions(transaction_type);
CREATE INDEX idx_credits_client_id ON credits(client_id);
CREATE INDEX idx_credits_status ON credits(credit_status);
CREATE INDEX idx_historique_user_id ON historique(user_id);
CREATE INDEX idx_historique_entity ON historique(entity_type, entity_id);

-- Create triggers for updating timestamps
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language plpgsql;

CREATE TRIGGER update_accounts_updated_at
    BEFORE UPDATE ON accounts
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_credits_updated_at
    BEFORE UPDATE ON credits
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

COMMIT;