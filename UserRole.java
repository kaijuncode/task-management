public class UserRole {
    private String id;
    private String userName;
    private String userRole;

    public UserRole(String id, String userName, String userRole){
        this.id = id;
        this.userName = userName;
        this.userRole = userRole;
    }

    public String getId(){
        return id;
    }

    public String getUserName(){
        return userName;
    }

    public String getUserRole(){
        return userRole;
    }
}
