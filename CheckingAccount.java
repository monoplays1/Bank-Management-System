public class CheckingAccount extends Account{

    public CheckingAccount(String status, double balance, int accountnum) {
        super(status, balance, accountnum);
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("invalid amount ");
            return false;
        }

        if (amount> getBalance()){
            System.out.println("insufficient funds ");
            return false;
        }

        setBalance(getBalance() - amount);

        System.out.println("withdraw successful ");
        System.out.println("new balance : " + getBalance());
        return true;
    }

}
