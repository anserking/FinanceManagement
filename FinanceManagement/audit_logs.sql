CREATE TABLE audit_logs (
    log_id SERIAL PRIMARY KEY,           -- Auto-increments for every log
    user_email VARCHAR(100),             -- Who did it?
    action_type VARCHAR(50),             -- What did they do? (LOGIN, TRANSFER, LOAN)
    details TEXT,                        -- Specific details (Amount, Success/Failure)
    ip_address VARCHAR(45) DEFAULT '127.0.0.1', 
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP -- When did it happen?
);