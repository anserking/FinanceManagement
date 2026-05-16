-- First, create custom types to match your Enums
CREATE TYPE account_status AS ENUM ('ACTIVE', 'SUSPENDED', 'CLOSED');
CREATE TYPE account_type AS ENUM ('SAVINGS', 'CURRENT', 'FIXED_DEPOSIT');

CREATE TABLE accounts (
    account_number VARCHAR(10) PRIMARY KEY, -- From AccountUtils (10 digits)
    owner_name VARCHAR(100) NOT NULL,
    -- Numeric(15,2) is standard for currency (BigDecimal)
    balance NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    status account_status NOT NULL DEFAULT 'ACTIVE',
    type account_type NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- Optional: Link to the users table from your previous query
    owner_email VARCHAR(255) REFERENCES users(email) 
);
INSERT INTO accounts (account_number, owner_name, balance, type, status)
VALUES ('1234567890', 'John Doe', 1000.00, 'SAVINGS', 'ACTIVE');
-- Credit (Deposit)
UPDATE accounts 
SET balance = balance + 500.00 
WHERE account_number = '1234567890';

-- Debit (Withdrawal)
UPDATE accounts 
SET balance = balance - 200.00 
WHERE account_number = '1234567890' AND balance >= 200.00;

-- Monthly interest = (Annual Rate / 12)
-- 0.05 / 12 = 0.0041666667
UPDATE accounts 
SET balance = ROUND(balance + (balance * (0.05 / 12)), 2)
WHERE type = 'SAVINGS' AND status = 'ACTIVE';