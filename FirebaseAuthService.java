import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONObject;

class AuthResult{
        String Uid;
        String idToken;
        String email;

        public AuthResult(String uid, String idToken, String email) {
            this.Uid = uid;
            this.idToken = idToken;
            this.email = email;
    }
}
public class FirebaseAuthService {
    private static final String API_KEY = "AIzaSyCF5p3akP5tDysrpnrYzUaU1tU_vQ-t91U";
    private static final String AUTH_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + API_KEY; 

    public AuthResult login(String email, String password) throws Exception {
        String jsonPayload = String.format(
            "{\"email\":\"%s\",\"password\":\"%s\",\"returnSecureToken\":true}", 
            email, password
        );

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(AUTH_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject json = new JSONObject(response.body());

            String uid = json.getString("localId");
            String idToken = json.getString("idToken");
            String userEmail = json.getString("email");

            return new AuthResult(uid, idToken, userEmail);

        } else {
            System.out.println(response.body());
            throw new RuntimeException("Login Failed: " + response.body());
        }
    }
}