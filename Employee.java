public class Employee extends User {

    private String employeeId;
    private String position;

    public Employee(String userId, String password, String userName, String name, String email, String employeeId, String position) {
        super(userId, password, userName, name, email);
        this.employeeId = employeeId;
        this.position = position;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public String getRole() {
        return "employee";
    }

    @Override
    public String display() {
        return super.display() + "|" + employeeId + "|" + position;
    }


}