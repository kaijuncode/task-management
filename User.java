public class User {
    private String id;
    private String userName;
    private String userStatus;
    private int pendingCount;
    
    public User(String id, String userName, String userStatus){
        this.id = id;
        this.userName = userName;
        this.userStatus = userStatus;
    }

    public void setPendingCount(int count){
        this.pendingCount = count;
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

    public int getPendingCount(){
        return pendingCount;
    }
}
