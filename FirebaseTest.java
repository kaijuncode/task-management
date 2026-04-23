import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class FirebaseTest {
    public static void main(String[] args) throws Exception {
        String projectId = "task-management-86056";

        String url = "https://firestore.googleapis.com/v1/projects/"
                + projectId + "/databases/task/documents/testCollection";

        HttpClient client = HttpClient.newHttpClient();

        String json = "{ \"fields\": { \"name\": { \"stringValue\": \"Hello\" } } }";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());
    }
}