import java.util.ArrayList;
import java.util.List;
public class Customer extends User {
    private String address;
    private List<String> accountNumbers;

    public Customer(String userId, String password, String userName, String name, String email, String address, List<String> accountNumbers) {
        super(userId, password, userName, name, email);
        this.address = address;
        this.accountNumbers = accountNumbers != null ? accountNumbers : new ArrayList<>();
    }

    public void addAccount(String accountNumber) {
        if (!accountNumbers.contains(accountNumber))
            accountNumbers.add(accountNumber);
    }

    public void removeAccount(String accountNumber) {
        accountNumbers.remove(accountNumber);
    }



    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getAccountNumbers() {
        return accountNumbers;
    }

    public void setAccountNumbers(List<String> accountNumbers) {
        this.accountNumbers = accountNumbers;
    }

    @Override
    public String getRole() {
        return "Customer";
    }

    @Override
    public String display() {
        return super.display() +"|"+ address + "|" + accountNumbers;
    }
}
