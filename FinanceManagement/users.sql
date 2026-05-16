CREATE TABLE users (
    -- nationalID as Primary Key (or use a serial id)
    national_id VARCHAR(50) PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL, -- Storing as hash is best practice
    role VARCHAR(20) NOT NULL,
    login_attempts INTEGER DEFAULT 0,
    is_locked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
INSERT INTO users (full_name, email, national_id, password_hash, role)
VALUES ('John Doe', 'john@gmail.com', 'ID12345', 'hashed_password_here', 'admin')
ON CONFLICT (email) DO NOTHING;
UPDATE users 
SET login_attempts = login_attempts + 1,
    is_locked = CASE WHEN login_attempts + 1 >= 5 THEN TRUE ELSE FALSE END
WHERE email = 'john@gmail.com';
UPDATE users 
SET login_attempts = 0, 
    is_locked = FALSE 
WHERE email = 'john@gmail.com';
