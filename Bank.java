import java.util.*;
import java.io.*;

public class Bank {
    private String bankName;
    private List<User> users;
    private List<Account> accounts;
    private List<Transaction> transactions;
    private int nextAccountNumber = 10001;
    private int nextTransactionId = 1;

    public Bank(String bankName) {
        this.bankName = bankName;
        this.users = new ArrayList<>();
        this.accounts = new ArrayList<>();
        this.transactions = new ArrayList<>();
        loadUsers();
        loadAccounts();
    }


    public void createUser(User user) {
        users.add(user);
        saveUsers();
        System.out.println("User created successfully: " + user.getUserName());
    }

    public User findUser(String userName) {
        for (User u : users) {
            if (u.getUserName().equals(userName)) return u;
        }
        return null;
    }

    public User login(String userName, String password) {
        User user = findUser(userName);
        if (user != null && user.login(userName, password)) {
            System.out.println("Login successful, welcome, " + user.getName());
            return user;
        }
        System.out.println("Invalid username or password.");
        return null;
    }

    public boolean resetPassword(Admin admin, String userName, String newPassword) {
        if (admin == null) {
            System.out.println("Only admins can reset passwords.");
            return false;
        }
        User user = findUser(userName);
        if (user == null) {
            System.out.println("User not found.");
            return false;
        }
        user.setPassword(newPassword);
        saveUsers();
        System.out.println("Password reset successfully for: " + userName);
        return true;
    }

    public void disableUser(Admin admin, String userName) {
        if (admin == null) {
            System.out.println("Only admins can disable users.");
            return;
        }
        User user = findUser(userName);
        if (user == null) {
            System.out.println("User not found.");
            return;
        }
        if (user instanceof Customer) {
            Customer customer = (Customer) user;
            for (String accNum : customer.getAccountNumbers()) {
                Account acc = findAccount(Integer.parseInt(accNum));
                if (acc != null) acc.setStatus("Inactive");
            }
        }
        saveAccounts();
        System.out.println("User " + userName + " has been disabled.");
    }

    public void listAllUsers(Admin admin) {
        if (admin == null) {
            System.out.println("Access denied.");
            return;
        }
        System.out.println(" ALL USERS ");
        for (User u : users) {
            System.out.println(u.display());
        }
    }



    public Account createAccount(String type, double initialDeposit, Customer customer) {
        Account account;
        int accNum = nextAccountNumber++;

        if (type.equalsIgnoreCase("Checking")) {
            account = new CheckingAccount("Active", initialDeposit, accNum);
        } else if (type.equalsIgnoreCase("Savings")) {
            if (initialDeposit < 100) {
                System.out.println("Savings account requires a minimum deposit of 100$.");
                return null;
            }
            account = new SavingsAccount("Active", initialDeposit, accNum);
        }
        else {
            System.out.println("unknown account type: " + type);
            return null;
        }

        accounts.add(account);
        customer.addAccount(String.valueOf(accNum));
        saveAccounts();
        saveUsers();

        System.out.println("Account created successfully. ");
        account.displayInfo();
        return account;
    }

    public Account findAccount(int accountNumber) {
        for (Account a : accounts) {
            if (a.getAccountnum() == accountNumber)
                return a;
        }
        return null;
    }

    public void viewCustomerAccounts(Customer customer) {
        System.out.println(" ACCOUNTS FOR " + customer.getName() + " ");
        if (customer.getAccountNumbers().isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }
        for (String accNum : customer.getAccountNumbers()) {
            Account acc = findAccount(Integer.parseInt(accNum));
            if (acc != null) acc.displayInfo();
            System.out.println("  ");
        }
    }

    public void setAccountStatus(Admin admin, int accountNumber, String status) {
        if (admin == null) {
            System.out.println("Only admins can change account status.");
            return;
        }
        Account acc = findAccount(accountNumber);
        if (acc == null) {
            System.out.println("Account not found.");
            return;
        }
        acc.setStatus(status);
        saveAccounts();
        System.out.println("Account " + accountNumber + " status updated to: " + status);
    }


    public boolean deposit(int accountNumber, double amount) {
        Account acc = findAccount(accountNumber);
        if (acc == null) {
            System.out.println("Account not found.");
            return false;
        }
        if (!acc.getStatus().equalsIgnoreCase("Active")) {
            System.out.println("Account is not active.");
            return false;
        }
        acc.deposit(amount);
        Transaction t = new Transaction(nextTransactionId++, "Deposit", amount);
        transactions.add(t);
        FileManager.saveTransaction(buildTransactionRecord(t, accountNumber, -1));
        saveAccounts();
        return true;
    }

    public boolean withdraw(int accountNumber, double amount) {
        Account acc = findAccount(accountNumber);
        if (acc == null) {
            System.out.println("Account not found.");
            return false;
        }
        if (!acc.getStatus().equalsIgnoreCase("Active")) {
            System.out.println("Account is not active.");
            return false;
        }
        boolean success = acc.withdraw(amount);
        if (success) {
            Transaction t = new Transaction(nextTransactionId++, "Withdrawal", amount);
            transactions.add(t);
            FileManager.saveTransaction(buildTransactionRecord(t, accountNumber, -1));
            saveAccounts();
        }
        return success;
    }

    public boolean transfer(int fromAccountNumber, int toAccountNumber, double amount) {
        Account from = findAccount(fromAccountNumber);
        Account to = findAccount(toAccountNumber);

        if (from == null || to == null) {
            System.out.println("either one or both accounts not found.");
            return false;
        }
        if (!from.getStatus().equalsIgnoreCase("Active") || !to.getStatus().equalsIgnoreCase("Active")) {
            System.out.println("both accounts must be active for a transfer.");
            return false;
        }

        boolean success = from.withdraw(amount);
        if (success) {
            to.deposit(amount);
            Transaction t = new Transaction(nextTransactionId++, "Transfer", amount);
            transactions.add(t);
            FileManager.saveTransaction(buildTransactionRecord(t, fromAccountNumber, toAccountNumber));
            saveAccounts();
            System.out.println("Transfer completed.");
        }
        return success;
    }


    public void viewAllTransactions(Admin admin) {
        if (admin == null) {
            System.out.println("Access denied.");
            return;
        }
        System.out.println(" ALL TRANSACTIONS ");
        if (transactions.isEmpty()) {
            System.out.println("No transactions recorded.");
            return;
        }
        for (Transaction t : transactions) {
            t.display();
            System.out.println(" ");
        }
    }

    public void applyMonthlyInterestAll(Admin admin) {
        if (admin == null) {
            System.out.println("Access denied.");
            return;
        }
        System.out.println(" APPLYING MONTHLY INTEREST ");
        for (Account acc : accounts) {
            if (acc instanceof SavingsAccount && acc.getStatus().equalsIgnoreCase("Active")) {
                ((SavingsAccount) acc).applyMonthlyInterest();
                ((SavingsAccount) acc).restwithdraw();
            }
        }
        saveAccounts();
    }



    private void saveUsers() {
        try {
            FileWriter writer = new FileWriter("users.txt");
            for (User u : users) {
                writer.write(u.getRole() + "|" + u.display() + " ");
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    private void loadUsers() {
        File file = new File("users.txt");
        if (!file.exists()) return;
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) {
                    User user = parseUser(line);
                    if (user != null) users.add(user);
                }
            }
            scanner.close();
        } catch (IOException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
    }

    private User parseUser(String line) {
        try {
            String[] parts = line.split("\\|");
            String role = parts[0];
            String block = parts[1];
            String userId   = extractField(block, "userId='", "'");
            String password = extractField(block, "password='", "'");
            String userName = extractField(block, "userName='", "'");
            String name     = extractField(block, "name='", "'");
            String email    = extractField(block, "email='", "'");

            switch (role) {
                case "Customer": {
                    String address = parts.length > 2 ? parts[2] : "";
                    Customer c = new Customer(userId, password, userName, name, email, address, new ArrayList<>());
                    if (parts.length > 3) {
                        String accList = parts[3].replaceAll("[\\[\\] ]", "");
                        if (!accList.isEmpty()) {
                            for (String acc : accList.split(",")) {
                                if (!acc.isEmpty()) c.addAccount(acc);
                            }
                        }
                    }
                    return c;
                }
                case "employee": {
                    String empId    = parts.length > 2 ? parts[2] : "";
                    String position = parts.length > 3 ? parts[3] : "";
                    return new Employee(userId, password, userName, name, email, empId, position);
                }
                case "ADMIN": {
                    String empId    = parts.length > 2 ? parts[2] : "";
                    String position = parts.length > 3 ? parts[3] : "";
                    int clearance   = parts.length > 4 ? Integer.parseInt(parts[4]) : 1;
                    String privs    = parts.length > 5 ? parts[5] : "";
                    return new Admin(userId, password, userName, name, email, empId, position, clearance, privs);
                }
            }
        } catch (Exception e) {
            System.out.println("Skipping missing parts user line: " + line);
        }
        return null;
    }

    private void saveAccounts() {
        try {
            FileWriter writer = new FileWriter("accounts.txt");
            for (Account a : accounts) {
                String type = (a instanceof SavingsAccount) ? "Savings" : "Checking";
                writer.write(type + "|" + a.getAccountnum() + "|" + a.getBalance() + "|" + a.getStatus() + " ");
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving accounts: " + e.getMessage());
        }
    }

    private void loadAccounts() {
        File file = new File("accounts.txt");
        if (!file.exists()) return;
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (!line.isEmpty()) {
                    String[] parts = line.split("\\|");
                    if (parts.length < 4) continue;
                    String type    = parts[0];
                    int accNum     = Integer.parseInt(parts[1]);
                    double balance = Double.parseDouble(parts[2]);
                    String status  = parts[3];
                    Account acc;
                    if (type.equals("Savings")) {
                        acc = new SavingsAccount(status, balance, accNum);
                    }
                    else {
                        acc = new CheckingAccount(status, balance, accNum);
                    }
                    accounts.add(acc);
                    if (accNum >= nextAccountNumber) nextAccountNumber = accNum + 1;
                }
            }
            scanner.close();
        } catch (IOException e) {
            System.out.println("Error loading accounts: " + e.getMessage());
        }
    }



    private String extractField(String text, String prefix, String suffix) {
        int start = text.indexOf(prefix);
        if (start == -1) return "";
        start += prefix.length();
        int end = text.indexOf(suffix, start);
        if (end == -1) return "";
        return text.substring(start, end);
    }

    private String buildTransactionRecord(Transaction t, int fromAcc, int toAcc) {
        String timestamp = new java.util.Date().toString();
        String record = "ID:" + t.id + " | Type:" + t.type + " | Amount:" + t.amount
                + " | From:" + fromAcc;
        if (toAcc != -1) record += " | To:" + toAcc;
        record += " | Time:" + timestamp;
        return record;
    }



    public String getBankName() { return bankName; }
    public List<User> getUsers() { return users; }
    public List<Account> getAccounts() { return accounts; }
    public List<Transaction> getTransactions() { return transactions; }
}