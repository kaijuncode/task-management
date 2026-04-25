public class UserSession {
    private static UserSession instance;

    private String Uid;
    private String idToken;
    private String name;
    private String email;

    private UserSession(){}

    public static UserSession getInstance(){
        if (instance == null){
            instance = new UserSession();
        }
        return instance;
    }

    public String getUid(){
        return Uid;
    }

    public String getidToken(){
        return idToken;
    }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    public void setUid(String uid){
        this.Uid = uid;
    }

    public void setidToken(String idToken){
        this.idToken = idToken;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void clear(){
        Uid = null;
        idToken = null;
        name = null;
        email = null;
    }
}
