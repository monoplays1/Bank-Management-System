public abstract class Account {

    private int accountnum;
    private double balance;
    private String status;


    public Account(String status, double balance, int accountnum) {
        this.status = status;
        this.balance = balance;
        this.accountnum = accountnum;
    }

    public void deposit(double amount){

        if (amount>0){

            balance+= amount;

            System.out.println("successful");
            System.out.println("the new balance is : " + balance);
        }

        else{
            System.out.println("invalid deposit amount");
        }
    }

    public abstract boolean withdraw(double amount);

    public void displayInfo(){

        System.out.println("account number : " + accountnum);
        System.out.println("balance : " + balance);
        System.out.println("status : "+ status);

    }

    public int getAccountnum() {
        return accountnum;
    }

    public void setAccountnum(int accountnum) {
        this.accountnum = accountnum;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
