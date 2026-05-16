CREATE TABLE loans (
    loan_id VARCHAR(20) PRIMARY KEY,
    applicant_email VARCHAR(255) REFERENCES users(email),
    principal NUMERIC(15, 2) NOT NULL,
    interest_rate NUMERIC(5, 4) NOT NULL, -- e.g., 0.0500 or 0.1000
    months INTEGER NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED
    date_applied TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

