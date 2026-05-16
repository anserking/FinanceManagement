💰 Secure Finance Management System (v1.0)

A production-grade, multi-user finance management system built in Java (CLI).
The system features secure authentication, role-based access control, concurrent transaction handling, and PostgreSQL cloud persistence.

✨ Features


🔐 Secure Authentication
SHA-256 password hashing with unique per-user salt
Login using Email or 13-digit CNIC
Account lockout after 5 failed login attempts
Audit logging for authentication events


👥 Role-Based Access Control
CUSTOMER role
ADMIN role


🏦 Account Management
Apply for:
Savings Account
Current Account
Fixed Deposit Account
Admin approval workflow for account creation
Support for multiple accounts per user


💸 Transaction Engine
Deposit funds
Transfer funds between accounts
Daily transfer limits
Transaction history tracking
Deadlock-free concurrent transactions using ReentrantLock


📄 Loan Processing
Loan application system
Loan approval/rejection by admin
EMI calculation support
Loan status tracking (PENDING, APPROVED, REJECTED)


🛡️ Audit & Logging
Security event logging
Admin action logging
Database-backed audit trail


⚠️ Exception Handling
Invalid input handling
Database exception handling
Concurrency issue handling
Graceful runtime error management
🛠️ Tech Stack
Java 11+
PostgreSQL (Neon Cloud Database)
JDBC
java.util.concurrent.locks.ReentrantLock
java.math.BigDecimal
SHA-256 Password Hashing


📦 Running the Application
Prerequisites
Java 11 or higher installed
Adoptium JDK Download
Internet connection (required for Neon cloud database access)


▶️ Download & Execute
Download the latest FinanceApp.jar from the repository Releases section.
Open a terminal in the folder containing the JAR file.
Run the application:
java -jar FinanceApp.jar

Note:
No local database setup is required.
The application connects directly to a shared Neon cloud database.

👨‍💼 Admin Account

An administrator account is pre-configured in the database.

Email: admin@bank.com
Password: (configured manually during deployment)

The admin can:

Approve/reject loan applications
Approve/reject account applications
Search accounts by CNIC or account number
Monitor audit logs
🧑‍💻 How to Use
Customer Workflow


1️⃣ Register

Provide:

Full name
Email
13-digit CNIC
Password (minimum 4 characters)


2️⃣ Login

Login using:

Email + Password
OR
CNIC + Password


3️⃣ Apply for an Account

Choose one:

Savings Account
Current Account
Fixed Deposit Account

Minimum initial deposit: $100


4️⃣ Wait for Admin Approval

Your account application remains in PENDING state until approved by the admin.


5️⃣ Access Banking Features

After approval, users can:

Deposit money
Transfer funds
View balance
View transaction history
Apply for loans
Switch between accounts
Create additional accounts
Admin Workflow
Admin Dashboard Features
View pending loan applications
Approve/reject loans
View pending account applications
Approve/reject account requests
Search accounts by:
CNIC
Account number
Review audit logs

All admin actions are securely logged in the database.


🗄️ Database Schema (Simplified)
users

Stores:

Email
CNIC
Password hash
Salt
Role
Lock status
accounts

Stores:

Account number
Owner email
Account type
Balance
Status
CNIC
account_applications

Stores pending account requests.

loans

Stores:

Loan ID
Applicant email
Amount
Interest rate
Loan duration
Status
transactions

Stores:

Sender account
Receiver account
Amount
Timestamp
audit_logs

Stores:

User
Action
Timestamp
Additional details


⚠️ Troubleshooting
Problem	Solution
java not recognized	Install Java 11+ and add Java to PATH
Cannot connect to database	Check internet connection
Account locked	5 failed login attempts — contact admin
Invalid CNIC	CNIC must contain exactly 13 digits


📁 Project Structure
src/
├── Module1/        # User, AuthService
├── Module2/        # Account System & AccountService
├── Module3/        # Transaction Engine & Repository
├── Module04/       # Loan System
├── module05/       # Password Utilities & Audit Logging
├── moduledata/     # Database Connection (Neon)
└── Main.java

🚀 Future Enhancements (v2)
Spring Boot Web Interface
Email Notifications
Two-Factor Authentication (2FA)
Monthly Interest Batch Jobs
REST API Support
Docker Deployment


👥 Group Members
Esha Rashid — 02-134252-060
Syed Muhammad Anser — 02-134252-010
Rehan Ilyas — 02-134252-025


📄 License
Academic project developed for course submission purposes only.
