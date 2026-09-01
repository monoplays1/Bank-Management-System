public abstract class User {


    private String userId;
    private String password;
    private String userName;
    private String name;
    private String email;

    public User(String userId, String password, String userName, String name, String email) {
        this.userId = userId;
        this.password = password;
        this.userName = userName;
        this.name = name;
        this.email = email;
    }

    public boolean login(String userName, String password) {
        return this.userName.equals(userName) && this.password.equals(password);
    }

    public abstract String getRole();


    public String display() {
        return "User{" + "userId= " + userId + " password= " + password + " userName= " + userName + " name= " + name + " email= " + email + '}';
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password == null || password.length() < 6)
            throw new IllegalArgumentException("password must be at least 6 characters. ");
        this.password = password;
    }


    public String getUserName() {
        return userName;
    }


    public String getName() {
        return name;
    }

}
