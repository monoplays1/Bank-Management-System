public class SavingsAccount extends Account{

    private double interestRate = 0.03;
    private int withdrawThisMonth = 0;
    private final int withdrawLimit = 3;
    private final double minimumBalance = 100;

    public SavingsAccount(String status, double balance, int accountnum) {
        super(status, balance, accountnum);

        if (balance<minimumBalance){
            System.out.println("saving account requires minimum 100$ ");
            setBalance(minimumBalance);
        }
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount<=0){
            System.out.println("invalid amount ");

            return false;

        }
        if (withdrawThisMonth >= withdrawLimit){
            System.out.println("withdraw limit reached ");
            return false;

        }

        if ((getBalance() - amount) < minimumBalance){
            System.out.println("cannot go below minimum balance ");
            return false;


        }
        setBalance(getBalance() - amount);
        withdrawThisMonth++;

        System.out.println("withdraw successful ");
        System.out.println("remaining balance : " + getBalance());
        return true;
    }
    public double calculateInterest(){

        return getBalance() * interestRate;

    }

    public void applyMonthlyInterest(){

        double interest = calculateInterest();

        setBalance(getBalance() + interest);

        System.out.println("interest added : " + interest);
        System.out.println("new balance : " + getBalance());

    }
    public void restwithdraw(){
        withdrawThisMonth = 0;

    }

    @Override
    public void displayInfo() {
        super.displayInfo();

        System.out.println("interest rate : " + (interestRate * 100) + "%");
        System.out.println("withdraw this month : " + withdrawThisMonth);
    }

}
