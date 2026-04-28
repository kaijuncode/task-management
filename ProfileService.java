import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ProfileService {
    public boolean checkUserProfile(String uid, String idToken) throws Exception {
        String projectId = "task-management-86056";

        String url = "https://firestore.googleapis.com/v1/projects/"
                + projectId + "/databases/(default)/documents/users/" + uid;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Authorization", "Bearer " + idToken)
                .build();
        
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.statusCode() == 200;
    }
    
}
