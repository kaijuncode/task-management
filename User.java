public class User {
    private String id;
    private String userName;
    private String userStatus;
    
    public User(String id, String userName, String userStatus){
        this.id = id;
        this.userName = userName;
        this.userStatus = userStatus;
    }

    public String getId(){
        return id;
    }

    public String getUserName(){
        return userName;
    }

    public String getUserStatus(){
        return userStatus;
    }
}
