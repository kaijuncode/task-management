import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

    public String getProfileStatus(String uid, String idToken) throws Exception {
        String projectID = "task-management-86056";

        String url = "https://firestore.googleapis.com/v1/projects/"
                + projectID + "/databases/(default)/documents/users/" + uid;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Authorization", "Bearer " + idToken)
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> respond = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (respond.statusCode() == 200){
            return extractStatus(respond.body());
        }
        else{
            throw new RuntimeException();
        }
    }

    public String extractStatus(String json){
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();

        return root.getAsJsonObject("fields")
                .getAsJsonObject("status")
                .get("stringValue")
                .getAsString();
    }

    public void updateStatus(String uid, String idToken, String newStatus) throws Exception {
        String projectID = "task-management-86056";

        String url = "https://firestore.googleapis.com/v1/projects/"
                + projectID + "/databases/(default)/documents/users/" + uid
                + "?updateMask.fieldPaths=status"; 

        String json = "{ \"fields\": { " +
                "\"status\": { \"stringValue\": \"" + newStatus + "\" } " +
                "} }";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + idToken)
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("Update Status Response: " + response.body());
    }

    
}
