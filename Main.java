import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    static Bank bank = new Bank("AIU Bank");
    static User currentUser = null;

    JPanel mainPanel = new JPanel(new CardLayout());
    CardLayout cl = (CardLayout) mainPanel.getLayout();

    public Main() {
        super("AIU Bank");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        setupDefaultAdmin();

        mainPanel.add(loginPanel(),    "login");
        mainPanel.add(customerPanel(), "customer");
        mainPanel.add(employeePanel(), "employee");
        mainPanel.add(adminPanel(),    "admin");

        add(mainPanel);
        cl.show(mainPanel, "login");
        setVisible(true);
    }

    void setupDefaultAdmin() {
        if (bank.findUser("admin") == null)
            bank.createUser(new Admin("A001", "admin123", "admin", "System Admin",
                    "admin@aiu.edu", "EMP001", "Administrator", 5, "FULL"));
        if (bank.findUser("employee") == null)
            bank.createUser(new Employee("E001", "employee123", "employee", "Default Employee",
                    "employee@aiu.edu", "EMP002", "Teller"));
    }



    JPanel loginPanel() {
        JPanel p = new JPanel(new GridLayout(5, 1, 5, 5));
        p.setBorder(BorderFactory.createEmptyBorder(25, 50, 25, 50));

        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JButton loginBtn   = new JButton("login");
        JButton regCustBtn = new JButton("register as customer");

        p.add(new JLabel("username:")); p.add(userField);
        p.add(new JLabel("password:")); p.add(passField);

        JPanel btns = new JPanel(new GridLayout(1, 2, 5, 0));
        btns.add(loginBtn); btns.add(regCustBtn);
        p.add(btns);

        loginBtn.addActionListener(e -> {
            User u = bank.login(userField.getText().trim(),
                    new String(passField.getPassword()).trim());
            if (u == null) {
                JOptionPane.showMessageDialog(this, "wrong username or password, try again.");
                return;
            }
            currentUser = u;
            userField.setText(""); passField.setText("");
            if      (u instanceof Admin)    cl.show(mainPanel, "admin");
            else if (u instanceof Employee) cl.show(mainPanel, "employee");
            else                            cl.show(mainPanel, "customer");
        });

        regCustBtn.addActionListener(e -> registerCustomer());

        return p;
    }

    void registerCustomer() {
        String name  = ask("full name:");      if (name  == null || name.isEmpty())  return;
        String uname = ask("username:");       if (uname == null || uname.isEmpty()) return;
        if (bank.findUser(uname) != null) { JOptionPane.showMessageDialog(this, "that username is already taken, try another one."); return; }
        String pass  = ask("password (min 6 characters):"); if (pass == null) return;
        if (pass.length() < 6) { JOptionPane.showMessageDialog(this, "password is too short, needs at least 6 characters."); return; }
        String email = ask("email:");   if (email == null || email.isEmpty()) return;
        String addr  = ask("address:"); if (addr  == null) addr = "";

        bank.createUser(new Customer("C" + System.currentTimeMillis(),
                pass, uname, name, email, addr, null));
        JOptionPane.showMessageDialog(this, "registered! you can now login as " + uname);
    }



    JPanel customerPanel() {
        JPanel p = new JPanel(new GridLayout(7, 1, 5, 5));
        p.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        p.add(new JLabel("customer menu", SwingConstants.CENTER));

        addBtn(p, "open account", () -> {
            String[] types = {"Checking", "Savings"};
            int t = JOptionPane.showOptionDialog(this, "what type of account?", "open account",
                    0, 3, null, types, types[0]);
            if (t < 0) return;
            double amt = askDouble("initial deposit amount:");
            if (amt <= 0) return;
            bank.createAccount(types[t], amt, (Customer) currentUser);
        });

        addBtn(p, "view my accounts", () -> showAccounts((Customer) currentUser));
        addBtn(p, "deposit",  () -> doDeposit());
        addBtn(p, "withdraw", () -> doWithdraw());
        addBtn(p, "transfer", () -> doTransfer());
        addBtn(p, "logout",   () -> { currentUser = null; cl.show(mainPanel, "login"); });

        return p;
    }



    JPanel employeePanel() {
        JPanel p = new JPanel(new GridLayout(3, 1, 5, 5));
        p.setBorder(BorderFactory.createEmptyBorder(60, 50, 60, 50));
        p.add(new JLabel("employee menu", SwingConstants.CENTER));

        addBtn(p, "view customer accounts", () -> {
            String uname = ask("customer username:");
            if (uname == null || uname.isEmpty()) return;
            User u = bank.findUser(uname);
            if (u instanceof Customer) showAccounts((Customer) u);
            else JOptionPane.showMessageDialog(this, "couldn't find that customer.");
        });

        addBtn(p, "logout", () -> { currentUser = null; cl.show(mainPanel, "login"); });

        return p;
    }


    JPanel adminPanel() {
        JPanel p = new JPanel(new GridLayout(13, 1, 5, 5));
        p.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));
        p.add(new JLabel("admin menu", SwingConstants.CENTER));

        addBtn(p, "create customer",  () -> createCustomerAsAdmin());
        addBtn(p, "create employee",  () -> createEmployeeAsAdmin());
        addBtn(p, "list all users",   () -> {
            StringBuilder sb = new StringBuilder();
            for (User u : bank.getUsers())
                sb.append("[").append(u.getRole()).append("] ")
                        .append(u.getName()).append(" (@").append(u.getUserName()).append(")\n");
            JOptionPane.showMessageDialog(this, sb.length() == 0 ? "no users found." : sb.toString());
        });
        addBtn(p, "reset password",   () -> {
            String un = ask("username:");
            String np = ask("new password (min 6 characters):");
            if (un == null || np == null) return;
            if (np.length() < 6) { JOptionPane.showMessageDialog(this, "password is too short, needs at least 6 characters."); return; }
            bank.resetPassword((Admin) currentUser, un, np);
            JOptionPane.showMessageDialog(this, "password has been reset.");
        });
        addBtn(p, "disable user",     () -> {
            String un = ask("username to disable:");
            if (un == null || un.isEmpty()) return;
            bank.disableUser((Admin) currentUser, un);
            JOptionPane.showMessageDialog(this, un + " has been disabled.");
        });
        addBtn(p, "set account status", () -> doSetAccountStatus());
        addBtn(p, "view all transactions", () -> {
            StringBuilder sb = new StringBuilder();
            for (Transaction t : bank.getTransactions())
                sb.append("ID: ").append(t.id)
                        .append("  type: ").append(t.type)
                        .append("  amount: ").append(String.format("%.2f", t.amount)).append("\n");
            JOptionPane.showMessageDialog(this,
                    sb.length() == 0 ? "no transactions yet." : sb.toString());
        });
        addBtn(p, "apply monthly interest", () -> {
            bank.applyMonthlyInterestAll((Admin) currentUser);
            JOptionPane.showMessageDialog(this, "monthly interest applied to all savings accounts.");
        });
        addBtn(p, "deposit",  () -> doDeposit());
        addBtn(p, "withdraw", () -> doWithdraw());
        addBtn(p, "transfer", () -> doTransfer());
        addBtn(p, "logout",   () -> { currentUser = null; cl.show(mainPanel, "login"); });

        return p;
    }

    void createCustomerAsAdmin() {
        String name  = ask("full name:");      if (name  == null || name.isEmpty())  return;
        String uname = ask("username:");       if (uname == null || uname.isEmpty()) return;
        if (bank.findUser(uname) != null) { JOptionPane.showMessageDialog(this, "that username is already taken, try another one."); return; }
        String pass  = ask("password (min 6 characters):"); if (pass == null) return;
        if (pass.length() < 6) { JOptionPane.showMessageDialog(this, "password is too short, needs at least 6 characters."); return; }
        String email = ask("email:");   if (email == null || email.isEmpty()) return;
        String addr  = ask("address:"); if (addr  == null) addr = "";

        bank.createUser(new Customer("C" + System.currentTimeMillis(),
                pass, uname, name, email, addr, null));
        JOptionPane.showMessageDialog(this, "customer created successfully.");
    }

    void createEmployeeAsAdmin() {
        String name  = ask("full name:");      if (name  == null || name.isEmpty())  return;
        String uname = ask("username:");       if (uname == null || uname.isEmpty()) return;
        if (bank.findUser(uname) != null) { JOptionPane.showMessageDialog(this, "that username is already taken, try another one."); return; }
        String pass  = ask("password (min 6 characters):"); if (pass == null) return;
        if (pass.length() < 6) { JOptionPane.showMessageDialog(this, "password is too short, needs at least 6 characters."); return; }
        String email = ask("email:");       if (email == null || email.isEmpty()) return;
        String empId = ask("employee ID:"); if (empId == null || empId.isEmpty()) return;
        String pos   = ask("position:");   if (pos   == null || pos.isEmpty())   return;

        bank.createUser(new Employee("E" + System.currentTimeMillis(),
                pass, uname, name, email, empId, pos));
        JOptionPane.showMessageDialog(this, "employee created successfully.");
    }

    void doSetAccountStatus() {
        int accNum = askInt("account number:");
        if (accNum <= 0) { JOptionPane.showMessageDialog(this, "that doesn't look like a valid account number."); return; }
        String[] options = {"Active", "Inactive", "Frozen"};
        int choice = JOptionPane.showOptionDialog(this, "pick the new status:", "set account status",
                0, 3, null, options, options[0]);
        if (choice < 0) return;
        bank.setAccountStatus((Admin) currentUser, accNum, options[choice]);
        JOptionPane.showMessageDialog(this, "account " + accNum + " is now set to " + options[choice]);
    }


    void doDeposit() {
        int accNum = askInt("account number:");
        if (accNum <= 0) { JOptionPane.showMessageDialog(this, "that doesn't look like a valid account number."); return; }
        double amount = askDouble("amount to deposit:");
        if (amount <= 0) { JOptionPane.showMessageDialog(this, "amount has to be more than 0."); return; }
        bank.deposit(accNum, amount);
    }

    void doWithdraw() {
        int accNum = askInt("account number:");
        if (accNum <= 0) { JOptionPane.showMessageDialog(this, "that doesn't look like a valid account number."); return; }
        double amount = askDouble("amount to withdraw:");
        if (amount <= 0) { JOptionPane.showMessageDialog(this, "amount has to be more than 0."); return; }
        bank.withdraw(accNum, amount);
    }

    void doTransfer() {
        int from = askInt("from account number:");
        if (from <= 0) { JOptionPane.showMessageDialog(this, "that doesn't look like a valid account number."); return; }
        int to = askInt("to account number:");
        if (to <= 0)   { JOptionPane.showMessageDialog(this, "that doesn't look like a valid account number."); return; }
        if (from == to){ JOptionPane.showMessageDialog(this, "source and destination can't be the same account."); return; }
        double amount = askDouble("amount to transfer:");
        if (amount <= 0) { JOptionPane.showMessageDialog(this, "amount has to be more than 0."); return; }
        bank.transfer(from, to, amount);
    }



    void addBtn(JPanel p, String label, Runnable action) {
        JButton btn = new JButton(label);
        btn.addActionListener(e -> action.run());
        p.add(btn);
    }

    void showAccounts(Customer c) {
        if (c.getAccountNumbers().isEmpty()) {
            JOptionPane.showMessageDialog(this, "no accounts found.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (String n : c.getAccountNumbers()) {
            Account a = bank.findAccount(Integer.parseInt(n));
            if (a != null)
                sb.append("account #").append(a.getAccountnum())
                        .append("  balance: ").append(String.format("%.2f", a.getBalance()))
                        .append("  status: ").append(a.getStatus()).append("\n");
        }
        JOptionPane.showMessageDialog(this, sb.toString());
    }

    String ask(String prompt) {
        return JOptionPane.showInputDialog(this, prompt);
    }

    int askInt(String prompt) {
        try { return Integer.parseInt(ask(prompt)); }
        catch (Exception e) { return -1; }
    }

    double askDouble(String prompt) {
        try { return Double.parseDouble(ask(prompt)); }
        catch (Exception e) { return -1; }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}