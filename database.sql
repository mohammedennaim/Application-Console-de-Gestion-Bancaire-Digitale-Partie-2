
SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

-- Création de la base de données
DROP DATABASE IF EXISTS bank_application;
CREATE DATABASE bank_application;

\c bank_application;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA public;

CREATE TYPE public.account_type AS ENUM (
    'COURANT',
    'EPARGNE', 
    'CREDIT'
);

CREATE TYPE public.action_type AS ENUM (
    'DEPOSIT',
    'REQUEST_CREDIT',
    'CLOSE_ACCOUNT'
);

CREATE TYPE public.credit_status AS ENUM (
    'PENDING',
    'APPROVED',
    'REJECTED',
    'ACTIVE',
    'COMPLETED',
    'DEFAULTED'
);

CREATE TYPE public.currency_code AS ENUM (
    'MAD',
    'EUR',
    'USD'
);

CREATE TYPE public.entity_type AS ENUM (
    'TRANSACTION',
    'CREDIT',
    'ACCOUNT'
);

CREATE TYPE public.fee_mode AS ENUM (
    'FIX',
    'PERCENT'
);

CREATE TYPE public.interest_mode AS ENUM (
    'SIMPLE',
    'COMPOUND'
);

CREATE TYPE public.operation_type AS ENUM (
    'TRANSFER_EXTERNAL',
    'WITHDRAW_FOREIGN_CURRENCY'
);

CREATE TYPE public.transaction_status AS ENUM (
    'PENDING',
    'SETTLED',
    'CANCELLED',
    'FAILED'
);

CREATE TYPE public.transaction_type AS ENUM (
    'TRANSFER',
    'TRANSFER_EXTERNAL'
);

CREATE TYPE public.user_role AS ENUM (
    'ADMIN',
    'MANAGER',
    'TELLER',
    'AUDITOR',
    'CLIENT'
);

CREATE OR REPLACE FUNCTION public.check_account_balance() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    IF NEW.balance < 0 AND NOT NEW.overdraft_allowed THEN
        RAISE EXCEPTION 'Le solde ne peut pas être négatif pour un compte sans découvert autorisé';    
    END IF;

    IF NEW.balance < (-1 * NEW.overdraft_limit) THEN
        RAISE EXCEPTION 'Le solde dépasse la limite de découvert autorisée: %', NEW.overdraft_limit;
    END IF;

    RETURN NEW;
END;
$$;

CREATE OR REPLACE FUNCTION public.update_timestamp() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$;

CREATE OR REPLACE FUNCTION public.update_updated_at_column() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$;

CREATE TABLE public.users (
    id uuid DEFAULT gen_random_uuid() NOT NULL PRIMARY KEY,
    username character varying(50) NOT NULL UNIQUE,
    password_hash character varying(255) NOT NULL,
    full_name character varying(100) NOT NULL,
    role public.user_role NOT NULL,
    active boolean DEFAULT true,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    last_login_at timestamp without time zone
);

CREATE TABLE public.accounts (
    id uuid DEFAULT gen_random_uuid() NOT NULL PRIMARY KEY,
    client_id uuid,
    account_type public.account_type NOT NULL,
    balance numeric(15,2) DEFAULT 0.00,
    currency_code character varying(3) DEFAULT 'MAD',
    overdraft_allowed boolean DEFAULT false,
    overdraft_limit numeric(15,2) DEFAULT 0.00,
    closed boolean DEFAULT false,
    opened_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    closed_at timestamp without time zone,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES public.users(id) ON DELETE CASCADE
);

CREATE TABLE public.clients (
    national_id character varying(20) UNIQUE,
    monthly_income numeric(12,2),
    email character varying(100),
    phone character varying(20),
    birth_date date
) INHERITS (public.users);

CREATE TABLE public.admins (
) INHERITS (public.users);

CREATE TABLE public.managers (
) INHERITS (public.users);

CREATE TABLE public.tellers (
) INHERITS (public.users);

CREATE TABLE public.auditors (
) INHERITS (public.users);

CREATE TABLE public.credits (
    id uuid DEFAULT gen_random_uuid() NOT NULL PRIMARY KEY,
    linked_account_id uuid,
    requested_amount numeric(15,2) NOT NULL,
    approved_amount numeric(15,2),
    interest_rate numeric(5,4) NOT NULL,
    term_months integer NOT NULL,
    status public.credit_status DEFAULT 'PENDING',
    interest_mode public.interest_mode DEFAULT 'SIMPLE',
    requested_by_client_id uuid,
    approved_by_user_id uuid,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    approved_at timestamp without time zone,
    completed_at timestamp without time zone,
    FOREIGN KEY (linked_account_id) REFERENCES public.accounts(id) ON DELETE CASCADE,
    FOREIGN KEY (requested_by_client_id) REFERENCES public.users(id),
    FOREIGN KEY (approved_by_user_id) REFERENCES public.users(id)
);

CREATE TABLE public.transactions (
    id uuid DEFAULT gen_random_uuid() NOT NULL PRIMARY KEY,
    transaction_type public.transaction_type NOT NULL,
    transaction_status public.transaction_status DEFAULT 'PENDING',
    source_account_id uuid,
    target_account_id uuid,
    amount numeric(15,2) NOT NULL,
    fee numeric(10,2) DEFAULT 0.00,
    currency_code character varying(3) DEFAULT 'MAD',
    initiated_by_user_id uuid,
    external_reference character varying(50),
    description text,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    executed_at timestamp without time zone,
    FOREIGN KEY (source_account_id) REFERENCES public.accounts(id),
    FOREIGN KEY (target_account_id) REFERENCES public.accounts(id),
    FOREIGN KEY (initiated_by_user_id) REFERENCES public.users(id)
);

CREATE TABLE public.fee_rules (
    id integer NOT NULL PRIMARY KEY,
    operation_type public.operation_type NOT NULL,
    fee_mode public.fee_mode NOT NULL,
    fee_value numeric(10,4) NOT NULL,
    currency_code character varying(3) DEFAULT 'MAD',
    active boolean DEFAULT true,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);

CREATE SEQUENCE public.fee_rules_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.fee_rules_id_seq OWNED BY public.fee_rules.id;
ALTER TABLE ONLY public.fee_rules ALTER COLUMN id SET DEFAULT nextval('public.fee_rules_id_seq'::regclass);

CREATE TABLE public.historique (
    id integer NOT NULL PRIMARY KEY,
    user_id uuid,
    action public.action_type NOT NULL,
    entity_type public.entity_type NOT NULL,
    entity_id uuid NOT NULL,
    details text,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE SET NULL
);

CREATE SEQUENCE public.historique_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.historique_id_seq OWNED BY public.historique.id;
ALTER TABLE ONLY public.historique ALTER COLUMN id SET DEFAULT nextval('public.historique_id_seq'::regclass);

CREATE TABLE public.rapports (
    id integer NOT NULL PRIMARY KEY,
    title character varying(200) NOT NULL,
    content text NOT NULL,
    generated_by_user_id uuid,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    period_start date,
    period_end date,
    FOREIGN KEY (generated_by_user_id) REFERENCES public.users(id)
);

CREATE SEQUENCE public.rapports_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.rapports_id_seq OWNED BY public.rapports.id;
ALTER TABLE ONLY public.rapports ALTER COLUMN id SET DEFAULT nextval('public.rapports_id_seq'::regclass);

CREATE TABLE public.statistiques (
    id integer NOT NULL PRIMARY KEY,
    metric_name character varying(100) NOT NULL,
    metric_value numeric(15,2) NOT NULL,
    period_start date NOT NULL,
    period_end date NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);

CREATE SEQUENCE public.statistiques_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

ALTER SEQUENCE public.statistiques_id_seq OWNED BY public.statistiques.id;
ALTER TABLE ONLY public.statistiques ALTER COLUMN id SET DEFAULT nextval('public.statistiques_id_seq'::regclass);

CREATE TRIGGER check_balance_trigger
    BEFORE INSERT OR UPDATE ON public.accounts
    FOR EACH ROW
    EXECUTE FUNCTION public.check_account_balance();

CREATE TRIGGER update_accounts_updated_at
    BEFORE UPDATE ON public.accounts
    FOR EACH ROW
    EXECUTE FUNCTION public.update_updated_at_column();

CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON public.users
    FOR EACH ROW
    EXECUTE FUNCTION public.update_updated_at_column();

INSERT INTO public.users VALUES 
('550e8400-e29b-41d4-a716-446655440000', 'client.test', '$2b$10$encrypted_hash', 'Test Client', 'CLIENT', true, '2025-09-26 10:46:36.123421', '2025-09-26 10:46:36.123421', NULL),
('550e8400-e29b-41d4-a716-446655440001', 'john.doe', '$2b$10$encrypted_hash', 'John Doe', 'CLIENT', true, '2025-09-26 10:46:36.123421', '2025-09-26 10:46:36.123421', NULL),
('550e8400-e29b-41d4-a716-446655440002', 'marie.martin', '$2b$10$encrypted_hash', 'Marie Martin', 'CLIENT', true, '2025-09-26 10:46:36.123421', '2025-09-26 10:46:36.123421', NULL),
('550e8400-e29b-41d4-a716-446655440003', 'ahmed.alaoui', '$2b$10$encrypted_hash', 'Ahmed Alaoui', 'CLIENT', true, '2025-09-26 10:46:36.123421', '2025-09-26 10:46:36.123421', NULL),
('660e8400-e29b-41d4-a716-446655440001', 'admin.test', '$2b$10$encrypted_hash', 'Test Admin', 'ADMIN', true, '2025-09-26 10:46:36.123421', '2025-09-26 10:46:36.123421', NULL),
('660e8400-e29b-41d4-a716-446655440002', 'admin.system', '$2b$10$encrypted_hash', 'System Administrator', 'ADMIN', true, '2025-09-26 10:46:36.123421', '2025-09-26 10:46:36.123421', NULL),
('770e8400-e29b-41d4-a716-446655440001', 'manager.branch', '$2b$10$encrypted_hash', 'Branch Manager', 'MANAGER', true, '2025-09-26 10:46:36.123421', '2025-09-26 10:46:36.123421', NULL),
('770e8400-e29b-41d4-a716-446655440002', 'manager.risk', '$2b$10$encrypted_hash', 'Risk Manager', 'MANAGER', true, '2025-09-26 10:46:36.123421', '2025-09-26 10:46:36.123421', NULL),
('880e8400-e29b-41d4-a716-446655440001', 'teller.window1', '$2b$10$encrypted_hash', 'Teller Window 1', 'TELLER', true, '2025-09-26 10:46:36.123421', '2025-09-26 10:46:36.123421', NULL),
('880e8400-e29b-41d4-a716-446655440002', 'teller.window2', '$2b$10$encrypted_hash', 'Teller Window 2', 'TELLER', true, '2025-09-26 10:46:36.123421', '2025-09-26 10:46:36.123421', NULL),
('990e8400-e29b-41d4-a716-446655440001', 'auditor.internal', '$2b$10$encrypted_hash', 'Internal Auditor', 'AUDITOR', true, '2025-09-26 10:46:36.123421', '2025-09-26 10:46:36.123421', NULL),
('990e8400-e29b-41d4-a716-446655440002', 'auditor.compliance', '$2b$10$encrypted_hash', 'Compliance Auditor', 'AUDITOR', true, '2025-09-26 10:46:36.123421', '2025-09-26 10:46:36.123421', NULL);

INSERT INTO public.accounts VALUES 
('1723acda-3145-48b0-8440-b3126a95834c', '550e8400-e29b-41d4-a716-446655440000', 'COURANT', 5000.00, 'MAD', true, 2000.00, false, '2025-09-26 10:46:36.133243', NULL, '2025-09-26 10:46:36.133243'),
('2723acda-3145-48b0-8440-b3126a95834c', '550e8400-e29b-41d4-a716-446655440000', 'EPARGNE', 10000.00, 'MAD', false, 0.00, false, '2025-09-26 10:46:36.133243', NULL, '2025-09-26 10:46:36.133243'),
('1823acda-3145-48b0-8440-b3126a95834c', '550e8400-e29b-41d4-a716-446655440001', 'COURANT', 12000.00, 'MAD', true, 5000.00, false, '2025-09-26 10:46:36.133243', NULL, '2025-09-26 10:46:36.133243'),
('2823acda-3145-48b0-8440-b3126a95834c', '550e8400-e29b-41d4-a716-446655440001', 'EPARGNE', 25000.00, 'MAD', false, 0.00, false, '2025-09-26 10:46:36.133243', NULL, '2025-09-26 10:46:36.133243'),
('3823acda-3145-48b0-8440-b3126a95834c', '550e8400-e29b-41d4-a716-446655440001', 'CREDIT', 0.00, 'MAD', false, 0.00, false, '2025-09-26 10:46:36.133243', NULL, '2025-09-26 10:46:36.133243'),
('1923acda-3145-48b0-8440-b3126a95834c', '550e8400-e29b-41d4-a716-446655440002', 'COURANT', 8500.00, 'MAD', true, 3000.00, false, '2025-09-26 10:46:36.133243', NULL, '2025-09-26 10:46:36.133243'),
('2923acda-3145-48b0-8440-b3126a95834c', '550e8400-e29b-41d4-a716-446655440002', 'EPARGNE', 15000.00, 'MAD', false, 0.00, false, '2025-09-26 10:46:36.133243', NULL, '2025-09-26 10:46:36.133243'),
('1a23acda-3145-48b0-8440-b3126a95834c', '550e8400-e29b-41d4-a716-446655440003', 'COURANT', 22000.00, 'MAD', true, 10000.00, false, '2025-09-26 10:46:36.133243', NULL, '2025-09-26 10:46:36.133243'),
('2a23acda-3145-48b0-8440-b3126a95834c', '550e8400-e29b-41d4-a716-446655440003', 'EPARGNE', 50000.00, 'MAD', false, 0.00, false, '2025-09-26 10:46:36.133243', NULL, '2025-09-26 10:46:36.133243');

INSERT INTO public.clients VALUES 
('550e8400-e29b-41d4-a716-446655440000', 'client.test', '$2b$10$encrypted_hash', 'Test Client', 'CLIENT', true, '2025-09-26 10:46:36.125317', '2025-09-26 10:46:36.125317', NULL, 'MA123456789', 15000.00, 'test@example.com', '+212612345678', '1990-01-01'),
('550e8400-e29b-41d4-a716-446655440001', 'john.doe', '$2b$10$encrypted_hash', 'John Doe', 'CLIENT', true, '2025-09-26 10:46:36.125317', '2025-09-26 10:46:36.125317', NULL, 'MA987654321', 25000.00, 'john.doe@email.com', '+212687654321', '1985-05-15'),
('550e8400-e29b-41d4-a716-446655440002', 'marie.martin', '$2b$10$encrypted_hash', 'Marie Martin', 'CLIENT', true, '2025-09-26 10:46:36.125317', '2025-09-26 10:46:36.125317', NULL, 'MA456789123', 18000.00, 'marie.martin@email.com', '+212698765432', '1992-12-08'),
('550e8400-e29b-41d4-a716-446655440003', 'ahmed.alaoui', '$2b$10$encrypted_hash', 'Ahmed Alaoui', 'CLIENT', true, '2025-09-26 10:46:36.125317', '2025-09-26 10:46:36.125317', NULL, 'MA321654987', 30000.00, 'ahmed.alaoui@email.com', '+212654987321', '1988-03-22');

INSERT INTO public.admins VALUES 
('660e8400-e29b-41d4-a716-446655440001', 'admin.test', '$2b$10$encrypted_hash', 'Test Admin', 'ADMIN', true, '2025-09-26 10:46:36.126777', '2025-09-26 10:46:36.126777', NULL),
('660e8400-e29b-41d4-a716-446655440002', 'admin.system', '$2b$10$encrypted_hash', 'System Administrator', 'ADMIN', true, '2025-09-26 10:46:36.126777', '2025-09-26 10:46:36.126777', NULL);

INSERT INTO public.managers VALUES 
('770e8400-e29b-41d4-a716-446655440001', 'manager.branch', '$2b$10$encrypted_hash', 'Branch Manager', 'MANAGER', true, '2025-09-26 10:46:36.128516', '2025-09-26 10:46:36.128516', NULL),
('770e8400-e29b-41d4-a716-446655440002', 'manager.risk', '$2b$10$encrypted_hash', 'Risk Manager', 'MANAGER', true, '2025-09-26 10:46:36.128516', '2025-09-26 10:46:36.128516', NULL);

INSERT INTO public.tellers VALUES 
('880e8400-e29b-41d4-a716-446655440001', 'teller.window1', '$2b$10$encrypted_hash', 'Teller Window 1', 'TELLER', true, '2025-09-26 10:46:36.130546', '2025-09-26 10:46:36.130546', NULL),
('880e8400-e29b-41d4-a716-446655440002', 'teller.window2', '$2b$10$encrypted_hash', 'Teller Window 2', 'TELLER', true, '2025-09-26 10:46:36.130546', '2025-09-26 10:46:36.130546', NULL);

INSERT INTO public.auditors VALUES 
('990e8400-e29b-41d4-a716-446655440001', 'auditor.internal', '$2b$10$encrypted_hash', 'Internal Auditor', 'AUDITOR', true, '2025-09-26 10:46:36.131945', '2025-09-26 10:46:36.131945', NULL),
('990e8400-e29b-41d4-a716-446655440002', 'auditor.compliance', '$2b$10$encrypted_hash', 'Compliance Auditor', 'AUDITOR', true, '2025-09-26 10:46:36.131945', '2025-09-26 10:46:36.131945', NULL);

INSERT INTO public.credits VALUES 
('4723acda-3145-48b0-8440-b3126a95834c', '1723acda-3145-48b0-8440-b3126a95834c', 50000.00, 45000.00, 0.0750, 36, 'APPROVED', 'COMPOUND', '550e8400-e29b-41d4-a716-446655440000', '770e8400-e29b-41d4-a716-446655440001', '2025-09-26 10:46:36.13789', NULL, NULL),
('4823acda-3145-48b0-8440-b3126a95834c', '1823acda-3145-48b0-8440-b3126a95834c', 100000.00, 90000.00, 0.0650, 60, 'ACTIVE', 'COMPOUND', '550e8400-e29b-41d4-a716-446655440001', '770e8400-e29b-41d4-a716-446655440002', '2025-09-26 10:46:36.13789', NULL, NULL),
('4923acda-3145-48b0-8440-b3126a95834c', '2823acda-3145-48b0-8440-b3126a95834c', 25000.00, NULL, 0.0550, 24, 'PENDING', 'SIMPLE', '550e8400-e29b-41d4-a716-446655440001', NULL, '2025-09-26 10:46:36.13789', NULL, NULL),
('4a23acda-3145-48b0-8440-b3126a95834c', '1923acda-3145-48b0-8440-b3126a95834c', 75000.00, 75000.00, 0.0580, 48, 'ACTIVE', 'COMPOUND', '550e8400-e29b-41d4-a716-446655440002', '770e8400-e29b-41d4-a716-446655440001', '2025-09-26 10:46:36.13789', NULL, NULL),
('4b23acda-3145-48b0-8440-b3126a95834c', '1a23acda-3145-48b0-8440-b3126a95834c', 200000.00, 180000.00, 0.0480, 72, 'COMPLETED', 'COMPOUND', '550e8400-e29b-41d4-a716-446655440003', '770e8400-e29b-41d4-a716-446655440002', '2025-09-26 10:46:36.13789', NULL, NULL);

INSERT INTO public.transactions VALUES 
('3723acda-3145-48b0-8440-b3126a95834c', 'TRANSFER_IN', 'SETTLED', '1723acda-3145-48b0-8440-b3126a95834c', '2723acda-3145-48b0-8440-b3126a95834c', 1000.00, 0.00, 'MAD', '880e8400-e29b-41d4-a716-446655440001', NULL, 'Transfer to savings', '2025-09-26 10:46:36.14021', NULL),
('3823acda-3145-48b0-8440-b3126a95834c', 'DEPOSIT', 'SETTLED', NULL, '1723acda-3145-48b0-8440-b3126a95834c', 2000.00, 0.00, 'MAD', '880e8400-e29b-41d4-a716-446655440001', NULL, 'Cash deposit', '2025-09-26 10:46:36.14021', NULL),
('3923acda-3145-48b0-8440-b3126a95834c', 'WITHDRAW', 'SETTLED', '2723acda-3145-48b0-8440-b3126a95834c', NULL, 500.00, 0.00, 'MAD', '880e8400-e29b-41d4-a716-446655440002', NULL, 'ATM withdrawal', '2025-09-26 10:46:36.14021', NULL),
('3a23acda-3145-48b0-8440-b3126a95834c', 'TRANSFER_EXTERNAL', 'SETTLED', '1823acda-3145-48b0-8440-b3126a95834c', NULL, 3000.00, 15.00, 'MAD', '880e8400-e29b-41d4-a716-446655440001', NULL, 'External transfer to other bank', '2025-09-26 10:46:36.14021', NULL),
('3b23acda-3145-48b0-8440-b3126a95834c', 'DEPOSIT', 'SETTLED', NULL, '2823acda-3145-48b0-8440-b3126a95834c', 5000.00, 0.00, 'MAD', '880e8400-e29b-41d4-a716-446655440002', NULL, 'Salary deposit', '2025-09-26 10:46:36.14021', NULL),
('3c23acda-3145-48b0-8440-b3126a95834c', 'TRANSFER_IN', 'SETTLED', '1923acda-3145-48b0-8440-b3126a95834c', '2923acda-3145-48b0-8440-b3126a95834c', 1500.00, 0.00, 'MAD', '880e8400-e29b-41d4-a716-446655440001', NULL, 'Monthly savings transfer', '2025-09-26 10:46:36.14021', NULL),
('3d23acda-3145-48b0-8440-b3126a95834c', 'WITHDRAW', 'PENDING', '1923acda-3145-48b0-8440-b3126a95834c', NULL, 800.00, 0.00, 'MAD', '880e8400-e29b-41d4-a716-446655440002', NULL, 'Pending withdrawal', '2025-09-26 10:46:36.14021', NULL),
('3e23acda-3145-48b0-8440-b3126a95834c', 'DEPOSIT', 'SETTLED', NULL, '1a23acda-3145-48b0-8440-b3126a95834c', 10000.00, 0.00, 'MAD', '880e8400-e29b-41d4-a716-446655440001', NULL, 'Business revenue deposit', '2025-09-26 10:46:36.14021', NULL),
('3f23acda-3145-48b0-8440-b3126a95834c', 'TRANSFER_EXTERNAL', 'FAILED', '2a23acda-3145-48b0-8440-b3126a95834c', NULL, 25000.00, 15.00, 'MAD', '880e8400-e29b-41d4-a716-446655440002', NULL, 'Failed international transfer', '2025-09-26 10:46:36.14021', NULL);

INSERT INTO public.fee_rules VALUES 
(1, 'TRANSFER_EXTERNAL', 'FIX', 15.0000, 'MAD', true, '2025-09-26 10:46:36.135182'),
(2, 'WITHDRAW_FOREIGN_CURRENCY', 'PERCENT', 0.0250, 'MAD', true, '2025-09-26 10:46:36.135182'),
(3, 'TRANSFER_EXTERNAL', 'PERCENT', 0.0150, 'EUR', true, '2025-09-26 10:46:36.135182'),
(4, 'WITHDRAW_FOREIGN_CURRENCY', 'FIX', 5.0000, 'USD', true, '2025-09-26 10:46:36.135182');

INSERT INTO public.historique VALUES 
(1, '880e8400-e29b-41d4-a716-446655440001', 'DEPOSIT', 'ACCOUNT', '1723acda-3145-48b0-8440-b3126a95834c', 'Account opened with initial deposit', '2025-09-26 10:46:36.142388'),
(2, '880e8400-e29b-41d4-a716-446655440002', 'DEPOSIT', 'ACCOUNT', '2723acda-3145-48b0-8440-b3126a95834c', 'Savings account opened', '2025-09-26 10:46:36.142388'),
(3, '770e8400-e29b-41d4-a716-446655440001', 'REQUEST_CREDIT', 'CREDIT', '4723acda-3145-48b0-8440-b3126a95834c', 'Credit request approved by manager', '2025-09-26 10:46:36.142388'),
(4, '770e8400-e29b-41d4-a716-446655440002', 'REQUEST_CREDIT', 'CREDIT', '4823acda-3145-48b0-8440-b3126a95834c', 'Large credit request processed', '2025-09-26 10:46:36.142388'),
(5, '770e8400-e29b-41d4-a716-446655440001', 'REQUEST_CREDIT', 'CREDIT', '4a23acda-3145-48b0-8440-b3126a95834c', 'Credit approved for home purchase', '2025-09-26 10:46:36.142388'),
(6, '880e8400-e29b-41d4-a716-446655440001', 'DEPOSIT', 'TRANSACTION', '3823acda-3145-48b0-8440-b3126a95834c', 'Large cash deposit processed', '2025-09-26 10:46:36.142388'),
(7, '880e8400-e29b-41d4-a716-446655440002', 'DEPOSIT', 'TRANSACTION', '3b23acda-3145-48b0-8440-b3126a95834c', 'Monthly salary deposit', '2025-09-26 10:46:36.142388'),
(8, '990e8400-e29b-41d4-a716-446655440001', 'CLOSE_ACCOUNT', 'ACCOUNT', '3823acda-3145-48b0-8440-b3126a95834c', 'Account closure reviewed by auditor', '2025-09-26 10:46:36.142388'),
(9, '660e8400-e29b-41d4-a716-446655440001', 'CLOSE_ACCOUNT', 'ACCOUNT', '3823acda-3145-48b0-8440-b3126a95834c', 'Admin approved account closure', '2025-09-26 10:46:36.142388');

INSERT INTO public.rapports VALUES 
(1, 'Monthly Activity Report - September 2025', 'Summary: Total deposits: 28,500 MAD. Total withdrawals: 1,300 MAD. Net flow: +27,200 MAD. New accounts opened: 9. Credit applications: 5 (4 approved, 1 pending). Notable transactions: Large business deposit of 10,000 MAD by Ahmed Alaoui. Risk assessment: Low risk profile maintained across all accounts.', '770e8400-e29b-41d4-a716-446655440001', '2025-09-26 10:46:36.147891', '2025-09-01', '2025-09-30'),
(2, 'Credit Risk Assessment Report', 'Credit portfolio analysis: Total outstanding: 390,000 MAD across 4 active credits. Risk distribution: Low risk: 75%, Medium risk: 25%, High risk: 0%. Recommendations: Continue current credit policies. Monitor John Doe pending application. Default rate: 0% (excellent performance). Average interest rate: 6.15%.', '770e8400-e29b-41d4-a716-446655440002', '2025-09-26 10:46:36.147891', '2025-09-01', '2025-09-30'),
(3, 'Compliance Audit Report', 'Audit findings: All transactions comply with regulatory requirements. AML compliance: 100% - all large transactions properly documented. KYC verification: Complete for all clients. Documentation score: Excellent. Recommendations: Maintain current compliance procedures. Schedule next audit in 6 months.', '990e8400-e29b-41d4-a716-446655440002', '2025-09-26 10:46:36.147891', '2025-09-01', '2025-09-30'),
(4, 'Branch Performance Summary', 'Branch metrics: Customer satisfaction: 95%. Transaction processing time: Average 2.3 minutes. Teller performance: Window 1: 98% accuracy, Window 2: 97% accuracy. Queue management: Average wait time 4.2 minutes during peak hours. Recommendations: Consider additional staff during lunch hours.', '770e8400-e29b-41d4-a716-446655440001', '2025-09-26 10:46:36.147891', '2025-09-01', '2025-09-30');

INSERT INTO public.statistiques VALUES 
(1, 'Total Deposits', 28500.00, '2025-09-01', '2025-09-30', '2025-09-26 10:46:36.144334'),
(2, 'Total Withdrawals', 1300.00, '2025-09-01', '2025-09-30', '2025-09-26 10:46:36.144334'),
(3, 'Active Accounts', 9.00, '2025-09-01', '2025-09-30', '2025-09-26 10:46:36.144334'),
(4, 'New Clients', 4.00, '2025-09-01', '2025-09-30', '2025-09-26 10:46:36.144334'),
(5, 'Credits Approved', 4.00, '2025-09-01', '2025-09-30', '2025-09-26 10:46:36.144334'),
(6, 'Total Credit Amount', 390000.00, '2025-09-01', '2025-09-30', '2025-09-26 10:46:36.144334'),
(7, 'Average Account Balance', 16611.11, '2025-09-01', '2025-09-30', '2025-09-26 10:46:36.144334'),
(8, 'Transaction Volume', 9.00, '2025-09-01', '2025-09-30', '2025-09-26 10:46:36.144334'),
(9, 'Fee Revenue', 45.00, '2025-09-01', '2025-09-30', '2025-09-26 10:46:36.144334');

SELECT pg_catalog.setval('public.fee_rules_id_seq', 4, true);
SELECT pg_catalog.setval('public.historique_id_seq', 9, true);
SELECT pg_catalog.setval('public.rapports_id_seq', 4, true);
SELECT pg_catalog.setval('public.statistiques_id_seq', 9, true);

SELECT 'Tables créées:' AS message;
SELECT schemaname, tablename FROM pg_tables WHERE schemaname = 'public' ORDER BY tablename;

SELECT 'Statistiques des données:' AS message;
SELECT 'users' as table_name, count(*) as row_count FROM users
UNION ALL
SELECT 'accounts', count(*) FROM accounts
UNION ALL
SELECT 'clients', count(*) FROM clients
UNION ALL
SELECT 'transactions', count(*) FROM transactions
UNION ALL
SELECT 'credits', count(*) FROM credits
ORDER BY table_name;

SELECT '=========================================' AS message
UNION ALL
SELECT 'BASE DE DONNÉES CRÉÉE AVEC SUCCÈS !'
UNION ALL
SELECT 'Structure complète avec héritage PostgreSQL'
UNION ALL
SELECT 'Données de test insérées'
UNION ALL
SELECT 'Triggers et contraintes activés'
UNION ALL
SELECT '=========================================';