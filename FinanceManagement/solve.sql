INSERT INTO users (full_name, email, national_id, password_hash, role, login_attempts, is_locked)
VALUES ('Rehan Ilyas', 'rehanilyas@bahria.edu.pk', 'CNIC-42201-3790470-7', '1234567', 'CUSTOMER', 0, FALSE);

INSERT INTO accounts (account_number, owner_name, balance, type, status, owner_email)
VALUES ('1000000001', 'Rehan Ilyas', 5000.00, 'SAVINGS', 'ACTIVE', 'rehanilyas@bahria.edu.pk');

Create the second user
INSERT INTO users (full_name, email, national_id, password_hash, role)
VALUES ('John Receiver', 'john@gmail.com', 'CNIC-42101-9999999-1', 'johnPass', 'CUSTOMER');

-- Create their account
INSERT INTO accounts (account_number, owner_name, balance, type, status, owner_email)
VALUES ('1000000002', 'John Receiver', 1500.00, 'CURRENT', 'ACTIVE', 'john@gmail.com');

An approved small loan
INSERT INTO loans (loan_id, applicant_email, principal, interest_rate, months, status)
VALUES ('LNT-77231', 'rehanilyas@bahria.edu.pk', 500.00, 0.0500, 12, 'APPROVED');

-- A pending large loan
INSERT INTO loans (loan_id, applicant_email, principal, interest_rate, months, status)
VALUES ('LNT-88122', 'rehanilyas@bahria.edu.pk', 5000.00, 0.1000, 24, 'PENDING');

INSERT INTO audit_logs (user_email, action_type, details)
VALUES 
('rehan@bahria.edu.pk', 'LOGIN', 'Successful login from Main Dashboard'),
('rehan@bahria.edu.pk', 'DEPOSIT', 'Deposited 200.00 via TransactionEngine'),
('rehan@bahria.edu.pk', 'LOAN_APP', 'Applied for loan LNT-88122 (Amount: 5000.00)');