-- Script to update accounts and transactions tables to use UUID instead of VARCHAR

-- First, clear dependent data to avoid foreign key issues
TRUNCATE transactions, credits, historique CASCADE;

-- Drop foreign key constraints that reference accounts.id
ALTER TABLE transactions DROP CONSTRAINT IF EXISTS transactions_source_account_id_fkey;
ALTER TABLE transactions DROP CONSTRAINT IF EXISTS transactions_target_account_id_fkey;
ALTER TABLE credits DROP CONSTRAINT IF EXISTS credits_linked_account_id_fkey;

-- Update the accounts table first
ALTER TABLE accounts ALTER COLUMN id TYPE UUID USING gen_random_uuid();

-- Update transactions table to use UUID for IDs
ALTER TABLE transactions 
    ALTER COLUMN id TYPE UUID USING gen_random_uuid(),
    ALTER COLUMN source_account_id TYPE UUID,
    ALTER COLUMN target_account_id TYPE UUID;

-- Update historique table entity_id to handle UUID references
ALTER TABLE historique ALTER COLUMN entity_id TYPE UUID;

-- Update credits table to use UUID for linked_account_id references
ALTER TABLE credits ALTER COLUMN linked_account_id TYPE UUID;

-- Recreate foreign key constraints
ALTER TABLE transactions ADD CONSTRAINT transactions_source_account_id_fkey 
    FOREIGN KEY (source_account_id) REFERENCES accounts(id);
ALTER TABLE transactions ADD CONSTRAINT transactions_target_account_id_fkey 
    FOREIGN KEY (target_account_id) REFERENCES accounts(id);
ALTER TABLE credits ADD CONSTRAINT credits_linked_account_id_fkey 
    FOREIGN KEY (linked_account_id) REFERENCES accounts(id);