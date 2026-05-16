ROLLBACK;
Create the Sender
INSERT INTO accounts (account_number, owner_name, balance, type, status)
VALUES ('SENDER_123', 'Test Sender', 1000.00, 'SAVINGS', 'ACTIVE');

-- Create the Receiver
INSERT INTO accounts (account_number, owner_name, balance, type, status)
VALUES ('RECEIVER_456', 'Test Receiver', 0.00, 'CURRENT', 'ACTIVE');
BEGIN;

UPDATE accounts 
SET balance = balance - 500.00 
WHERE account_number = 'SENDER_123' AND status = 'ACTIVE' AND balance >= 500.00;

UPDATE accounts 
SET balance = balance + 500.00 
WHERE account_number = 'RECEIVER_456' AND status = 'ACTIVE';

INSERT INTO transactions (transaction_id, sender_account, receiver_account, transaction_type, amount)
VALUES ('TRX789AB', 'SENDER_123', 'RECEIVER_456', 'TRANSFER', 500.00);

COMMIT;

