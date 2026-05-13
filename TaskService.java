import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TaskService {
    private final String projectId = "task-management-86056";

    //Get Lastest Update of TASK
    public String getLastUpdated() throws Exception{
        String idToken = UserSession.getInstance().getidToken();

        String url = "https://firestore.googleapis.com/v1/projects/" + projectId + "/databases/(default)/documents/system/meta";

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Authorization", "Bearer " + idToken)
            .GET()
            .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200){
            throw new Exception("Failed to get lastUpdated");
        }

        JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();

        return root.getAsJsonObject("fields")
            .getAsJsonObject("lastUpdated")
            .get("timestampValue")
            .getAsString();
    }

    //Catch TASK
    public List<Task> getTasks() throws Exception {
        String idToken = UserSession.getInstance().getidToken();

        String url = "https://firestore.googleapis.com/v1/projects/" + projectId + "/databases/(default)/documents/tasks";

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Authorization", "Bearer " + idToken)
            .GET()
            .build();
                
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Task> tasks = new ArrayList<>();

        if (response.statusCode() == 200){
            JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();

            if (root.has("documents")){
                JsonArray documents = root.getAsJsonArray("documents");

                for (JsonElement doc : documents){
                    JsonObject fields = doc.getAsJsonObject().getAsJsonObject("fields");

                    String fullPath = doc.getAsJsonObject().get("name").getAsString();
                    String id = fullPath.substring(fullPath.lastIndexOf("/") + 1);
                    String companyName = getField(fields, "company");
                    String customerName = getField(fields, "customer");
                    String contactNumber = getField(fields, "contact");
                    String software = getField(fields, "software");
                    String issue = getField(fields, "issue");
                    String postBy = getField(fields, "postBy");
                    String assignedTo = getField(fields, "assignedTo");
                    String method = getField(fields, "method");
                    String email = getField(fields, "emailVal");
                    boolean urgent = fields.has("urgent") && fields.getAsJsonObject("urgent").get("booleanValue").getAsBoolean();
                    String createTime = getField(fields, "createTime");
                    String status = getField(fields, "status");

                    Task task = new Task(id, companyName, customerName, contactNumber, software, issue, postBy, assignedTo, method, email, urgent, createTime, status);
                    tasks.add(task);
                }
            }
        }
        return tasks;
    }

    private String getField(JsonObject fields, String key){
        if (fields.has(key)){
            return fields.getAsJsonObject(key).get("stringValue").getAsString();
        }
        return "";
    }

    //Current User Accept Task
    public void acceptTask(Task task) throws Exception{
        String idToken = UserSession.getInstance().getidToken();
        String currentUser = UserSession.getInstance().getName();

        String url = "https://firestore.googleapis.com/v1/projects/" + projectId + "/databases/(default)/documents/tasks/" + task.getId() + "?updateMask.fieldPaths=assignedTo";

        String json = "{ \"fields\": { " +
            "\"assignedTo\": { \"stringValue\": \"" + currentUser + "\" } " +
            "} }";

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + idToken)
            .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());
    }

    //Assign Pending Task to Someone
    public void assignTask(Task task, String newAssign) throws Exception{
        String idToken = UserSession.getInstance().getidToken();

        String url = "https://firestore.googleapis.com/v1/projects/" + projectId + "/databases/(default)/documents/tasks/" + task.getId() + "?updateMask.fieldPaths=assignedTo";

        String json = "{ \"fields\": { " +
            "\"assignedTo\": { \"stringValue\": \""+ newAssign +"\" } " +
            "} }";

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + idToken)
            .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());
    }

    //Transfer Task to Someone from Current User
    public void transferTask(Task task, String newTransfer) throws Exception{
        String idToken = UserSession.getInstance().getidToken();

        String url = "https://firestore.googleapis.com/v1/projects/" + projectId + "/databases/(default)/documents/tasks/" + task.getId() + "?updateMask.fieldPaths=assignedTo";

        String json = "{ \"fields\": { " +
            "\"assignedTo\": { \"stringValue\": \""+ newTransfer +"\" } " +
            "} }";

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + idToken)
            .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());
    }

    //Complete Task
    public void doneTask(Task task) throws Exception{
        String idToken = UserSession.getInstance().getidToken();

        String url = "https://firestore.googleapis.com/v1/projects/" + projectId + "/databases/(default)/documents/tasks/" + task.getId() + "?updateMask.fieldPaths=status";

        String json = "{ \"fields\": { " +
            "\"status\": { \"stringValue\": \"Complete\" } " +
            "} }";

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + idToken)
            .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());
    }
}
