import Module1.*;
import Module2.*;
import Module3.*;
import Module04.*;
import module05.*;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.Scanner;
import java.util.InputMismatchException;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static AuthService authService = new AuthService();
    private static User currentUser = null;
    private static Account currentAccount = null;

    // Safe input methods
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    private static BigDecimal readBigDecimal(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return scanner.nextBigDecimal();
            } catch (InputMismatchException e) {
                System.out.println("Invalid amount. Please enter a valid number.");
                scanner.nextLine();
            }
        }
    }

    public static void main(String[] args) {
        while (true) {
            try {
                if (currentUser == null) {
                    showAuthMenu();
                } else if (currentUser.getRole().equalsIgnoreCase("ADMIN")) {
                    showAdminMenu();
                } else {
                    List<Account> userAccounts = AccountService.getUserAccounts(currentUser.getEmail());
                    if (userAccounts.isEmpty()) {
                        showNoAccountMenu();
                    } else {
                        if (currentAccount == null) {
                            selectAccount(userAccounts);
                        }
                        showUserMenu();
                    }
                }
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
                e.printStackTrace();
                currentUser = null;
                currentAccount = null;
            }
        }
    }

    private static void showAuthMenu() {
        while (currentUser == null) {
            System.out.println("\n--- WELCOME TO SECURE BANK ---");
            System.out.println("1. Register");
            System.out.println("2. Login (Email or CNIC)");
            System.out.println("3. Exit");
            int choice = readInt("Choice: ");
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    try {
                        System.out.print("Full Name: ");
                        String name = scanner.nextLine();
                        System.out.print("Email: ");
                        String email = scanner.next();
                        System.out.print("CNIC (13 digits): ");
                        String cnic = scanner.next();
                        System.out.print("Password: ");
                        String pass = scanner.next();
                        authService.register(name, email, cnic, pass);
                    } catch (Exception e) {
                        System.out.println("Registration failed: " + e.getMessage());
                    }
                    break;
                case 2:
                    try {
                        System.out.print("Email or CNIC: ");
                        String identifier = scanner.next();
                        System.out.print("Password: ");
                        String pass = scanner.next();
                        currentUser = authService.login(identifier, pass);
                        if (currentUser != null) {
                            currentAccount = null;
                            return;
                        }
                    } catch (Exception e) {
                        System.out.println("Login error: " + e.getMessage());
                    }
                    break;
                case 3:
                    System.out.println("Goodbye!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private static void showNoAccountMenu() {
        System.out.println("\n--- You don't have any active account yet ---");
        System.out.println("1. Apply for a new account");
        System.out.println("2. Logout");
        int choice = readInt("Choice: ");
        if (choice == 1) {
            applyForNewAccount();
        } else {
            currentUser = null;
        }
    }

    private static void applyForNewAccount() {
        try {
            System.out.println("\nSelect account type:");
            System.out.println("1. Savings");
            System.out.println("2. Current");
            System.out.println("3. Fixed Deposit");
            int typeChoice = readInt("Choice: ");
            String type = "";
            switch (typeChoice) {
                case 1: type = "SAVINGS"; break;
                case 2: type = "CURRENT"; break;
                case 3: type = "FIXED_DEPOSIT"; break;
                default: System.out.println("Invalid type"); return;
            }
            BigDecimal deposit = readBigDecimal("Initial deposit amount (minimum $100): ");
            if (deposit.compareTo(new BigDecimal("100")) < 0) {
                System.out.println("Minimum deposit is $100.");
                return;
            }
            AccountService.applyForAccount(currentUser.getEmail(), type, deposit);
        } catch (Exception e) {
            System.out.println("Failed to apply: " + e.getMessage());
        }
    }

    private static void selectAccount(List<Account> accounts) {
        if (accounts.isEmpty()) {
            System.out.println("No accounts available.");
            return;
        }
        System.out.println("\n--- Your Accounts ---");
        for (int i = 0; i < accounts.size(); i++) {
            Account acc = accounts.get(i);
            System.out.println((i+1) + ". " + acc.getType() + " | " + acc.getAccountNumber() + " | Balance: $" + acc.getBalance());
        }
        int idx = readInt("Select account to operate: ") - 1;
        if (idx >= 0 && idx < accounts.size()) {
            currentAccount = accounts.get(idx);
            System.out.println("Now using account: " + currentAccount.getAccountNumber());
        } else {
            System.out.println("Invalid selection.");
        }
    }

    private static void showUserMenu() {
        while (currentUser != null && currentAccount != null) {
            System.out.println("\n--- Account: " + currentAccount.getType() + " (" + maskAccount(currentAccount.getAccountNumber()) + ") Balance: $" + currentAccount.getBalance() + " ---");
            System.out.println("1. Deposit Money");
            System.out.println("2. Transfer Funds");
            System.out.println("3. View Current Balance");
            System.out.println("4. View Transaction History");
            System.out.println("5. Apply for a Loan");
            System.out.println("6. View My Loan History");
            System.out.println("7. Apply for Another Account");
            System.out.println("8. Switch to Another Account");
            System.out.println("9. Logout");
            int choice = readInt("Choice: ");

            try {
                switch (choice) {
                    case 1:
                        BigDecimal amt = readBigDecimal("Amount: ");
                        if (amt.compareTo(BigDecimal.ZERO) <= 0) {
                            System.out.println("Amount must be positive.");
                            break;
                        }
                        TransactionEngine.deposit(currentAccount, amt);
                        System.out.println("Deposit successful. New balance: $" + currentAccount.getBalance());
                        break;
                    case 2:
                        System.out.print("Receiver account number: ");
                        String recNum = scanner.next();
                        BigDecimal transferAmt = readBigDecimal("Amount: ");
                        if (transferAmt.compareTo(BigDecimal.ZERO) <= 0) {
                            System.out.println("Amount must be positive.");
                            break;
                        }
                        Account receiver = AccountService.getAccountByNumber(recNum);
                        if (receiver == null) {
                            System.out.println("Receiver account not found or inactive.");
                            break;
                        }
                        try {
                            TransactionEngine.transfer(currentAccount, receiver, transferAmt);
                            System.out.println("Transfer successful.");
                        } catch (LimitExceededException e) {
                            System.out.println("Transfer failed (limit exceeded): " + e.getMessage());
                        }
                        break;
                    case 3:
                        System.out.println("Current balance: $" + currentAccount.getBalance());
                        break;
                    case 4:
                        TransactionRepository.printMiniStatement(currentAccount.getAccountNumber());
                        break;
                    case 5:
                        BigDecimal loanAmt = readBigDecimal("Loan amount: ");
                        int months = readInt("Duration (months): ");
                        Loanservice.processLoanApplication(currentUser, loanAmt, months);
                        break;
                    case 6:
                        Loanhistory.displayUserLoanhistory(currentUser.getEmail());
                        break;
                    case 7:
                        applyForNewAccount();
                        break;
                    case 8:
                        List<Account> accounts = AccountService.getUserAccounts(currentUser.getEmail());
                        selectAccount(accounts);
                        break;
                    case 9:
                        currentUser = null;
                        currentAccount = null;
                        System.out.println("Logged out.");
                        return;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (LoanInvalidException e) {
                System.out.println("Loan error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Operation failed: " + e.getMessage());
            }
        }
    }

    private static void showAdminMenu() {
        while (currentUser != null && currentUser.getRole().equals("ADMIN")) {
            System.out.println("\n--- ADMIN DASHBOARD ---");
            System.out.println("1. View Pending Loans");
            System.out.println("2. Approve/Reject a Loan");
            System.out.println("3. View Pending Account Applications");
            System.out.println("4. Approve/Reject Account Application");
            System.out.println("5. Search Accounts by CNIC or Account Number");
            System.out.println("6. Logout");
            int choice = readInt("Choice: ");

            try {
                switch (choice) {
                    case 1:
                        List<loan> pendingLoans = Loanservice.getPendingLoans();
                        if (pendingLoans.isEmpty()) System.out.println("No pending loans.");
                        else pendingLoans.forEach(System.out::println);
                        break;
                    case 2:
                        System.out.print("Loan ID: ");
                        String loanId = scanner.next();
                        System.out.print("Action (APPROVE/REJECT): ");
                        String action = scanner.next().toUpperCase();
                        if (!action.equals("APPROVE") && !action.equals("REJECT")) {
                            System.out.println("Invalid action.");
                            break;
                        }
                        Loanservice.updateLoanStatus(loanId, action);
                        break;
                    case 3:
                        List<AccountService.AccountApplication> apps = AccountService.getPendingAccountApplications();
                        if (apps.isEmpty()) System.out.println("No pending account applications.");
                        else apps.forEach(System.out::println);
                        break;
                    case 4:
                        int appId = readInt("Application ID: ");
                        System.out.print("Approve or Reject? (A/R): ");
                        String dec = scanner.next();
                        if (dec.equalsIgnoreCase("A")) {
                            String newAccNum = generateAccountNumber();
                            AccountService.approveAccountApplication(appId, currentUser.getEmail(), newAccNum);
                        } else if (dec.equalsIgnoreCase("R")) {
                            AccountService.rejectAccountApplication(appId, currentUser.getEmail());
                        } else {
                            System.out.println("Invalid choice.");
                        }
                        break;
                    case 5:
                        System.out.print("Enter account number or CNIC to search: ");
                        String searchTerm = scanner.next();
                        List<Account> found = AccountService.searchAccounts(searchTerm);
                        if (found.isEmpty()) {
                            System.out.println("No accounts found.");
                        } else {
                            for (Account acc : found) {
                                System.out.println(acc);
                            }
                        }
                        break;
                    case 6:
                        currentUser = null;
                        currentAccount = null;
                        System.out.println("Admin logged out.");
                        return;
                    default:
                        System.out.println("Invalid.");
                }
            } catch (Exception e) {
                System.out.println("Admin operation error: " + e.getMessage());
            }
        }
    }

    private static String generateAccountNumber() {
        SecureRandom random = new SecureRandom();
        long number = 1000000000L + (long)(random.nextDouble() * 9000000000L);
        return String.valueOf(number);
    }

    private static String maskAccount(String accNum) {
        if (accNum == null || accNum.length() < 8) return "****";
        return "****" + accNum.substring(accNum.length() - 4);
    }
}