public class Admin extends Employee {
    private int securityClearance;
    private String adminPrivileges;

    public Admin(String userId, String password, String userName, String name, String email, String employeeId, String position, int securityClearance, String adminPrivileges) {
        super(userId, password, userName, name, email, employeeId, position);
        this.securityClearance = securityClearance;
        this.adminPrivileges = adminPrivileges;
    }

    public int getSecurityClearance() {
        return securityClearance;
    }

    public void setSecurityClearance(int securitylevel) {
        this.securityClearance = securitylevel;
    }

    public String getAdminPrivileges() {
        return adminPrivileges;
    }

    public void setAdminPrivileges(String privAdmin) {
        this.adminPrivileges = privAdmin;
    }

    @Override
    public String getRole() { return "ADMIN"; }


    @Override
    public String display() {
        return super.display() + "|" + securityClearance + "|" + adminPrivileges;
    }

}
