import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ServiceAdministration {
    private final String projectId = "task-management-86056";

    //Existing Product (Product Page)
    public boolean productExists(String productName) throws Exception{
        List<Product> productExistList = getProduct();

        for (Product product : productExistList){
            String cleanProductName = product.getProductName().replaceAll("\\s", "");
            if (cleanProductName.equalsIgnoreCase(productName)){
                return true;
            }
        }
        return false;
    }

    //Product List (Product Page)
    public List<Product> getProduct() throws Exception{
        String idToken = UserSession.getInstance().getidToken();

        String url = "https://firestore.googleapis.com/v1/projects/" + projectId + "/databases/(default)/documents/products";

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Authorization", "Bearer " + idToken)
            .GET()
            .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<Product> products = new ArrayList<>();

        if (response.statusCode() == 200){
            JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();

            if (root.has("documents")){
                JsonArray documents = root.getAsJsonArray("documents");
                        
                for (JsonElement doc : documents){
                    JsonObject fields = doc.getAsJsonObject().getAsJsonObject("fields");

                    String fullPath = doc.getAsJsonObject().get("name").getAsString();
                    String id = fullPath.substring(fullPath.lastIndexOf("/") + 1);
                    String productName = getField(fields, "name");

                    Product product = new Product(id, productName);
                    products.add(product);
                }
            }
        }
        return products;
    }

    //User Role List (User Role Page)
    public List<UserRole> getUserRole() throws Exception{
        String idToken = UserSession.getInstance().getidToken();

        String url = "https://firestore.googleapis.com/v1/projects/" + projectId + "/databases/(default)/documents/users";

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Authorization", "Bearer " + idToken)
            .GET()
            .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<UserRole> users = new ArrayList<>();

        if (response.statusCode() == 200){
            JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();

            if (root.has("documents")){
                JsonArray documents = root.getAsJsonArray("documents");
                        
                for (JsonElement doc : documents){
                    JsonObject fields = doc.getAsJsonObject().getAsJsonObject("fields");

                    String fullPath = doc.getAsJsonObject().get("name").getAsString();
                    String id = fullPath.substring(fullPath.lastIndexOf("/") + 1);
                    String userName = getField(fields, "name");
                    String userRole = getField(fields, "role");

                    if (userName.equals("ADMIN")){
                        continue;
                    }

                    UserRole user = new UserRole(id, userName, userRole);
                    users.add(user);
                }
            }
        }
        return users;
    }

    //Update User Role (User Role Page)
    public void updateUserRole(UserRole userRole, String newRole) throws Exception{
        String idToken = UserSession.getInstance().getidToken();

        String url = "https://firestore.googleapis.com/v1/projects/"
                + projectId + "/databases/(default)/documents/users/" + userRole.getId()
                + "?updateMask.fieldPaths=role"; 

        String json = "{ \"fields\": { " +
                "\"role\": { \"stringValue\": \"" + newRole + "\" } " +
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

    //User Status List (User Status Page)
    public List<User> getUserStatus() throws Exception{
        String idToken = UserSession.getInstance().getidToken();

        String url = "https://firestore.googleapis.com/v1/projects/" + projectId + "/databases/(default)/documents/users";

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Authorization", "Bearer " + idToken)
            .GET()
            .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        List<User> users = new ArrayList<>();

        String taskUrl = "https://firestore.googleapis.com/v1/projects/" + projectId + "/databases/(default)/documents/tasks";
        HttpRequest taskRequest = HttpRequest.newBuilder()
            .uri(URI.create(taskUrl))
            .header("Authorization", "Bearer " + idToken)
            .GET()
            .build();

        HttpClient taskClient = HttpClient.newHttpClient();
        HttpResponse<String> taskResponse = taskClient.send(taskRequest, HttpResponse.BodyHandlers.ofString());

        //Count Pending Task Amount
        Map<String, Integer> pendingMap = new HashMap<>();
        if (taskResponse.statusCode() == 200){
            JsonObject taskRoot = JsonParser.parseString(taskResponse.body()).getAsJsonObject();

            if (taskRoot.has("documents")){
                JsonArray taskDocuments = taskRoot.getAsJsonArray("documents");

                for(JsonElement doc : taskDocuments){
                    JsonObject taskFields = doc.getAsJsonObject().getAsJsonObject("fields");

                    String assignedTo = getField(taskFields, "assignedTo");
                    String status = getField(taskFields, "status");

                    if (!status.equalsIgnoreCase("Complete") && !assignedTo.equalsIgnoreCase("Everyone")){
                        pendingMap.put(assignedTo, pendingMap.getOrDefault(assignedTo, 0) + 1);
                    }
                }
            }
        }

        if (response.statusCode() == 200){
            JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();

            if (root.has("documents")){
                JsonArray documents = root.getAsJsonArray("documents");
                        
                for (JsonElement doc : documents){
                    JsonObject fields = doc.getAsJsonObject().getAsJsonObject("fields");

                    String fullPath = doc.getAsJsonObject().get("name").getAsString();
                    String id = fullPath.substring(fullPath.lastIndexOf("/") + 1);
                    String userName = getField(fields, "name");
                    String userStatus = getField(fields, "status");

                    if (userName.equals("ADMIN")){
                        continue;
                    }

                    User user = new User(id, userName, userStatus);
                    user.setPendingCount(pendingMap.getOrDefault(userName, 0));
                    users.add(user);
                }
            }
        }
        return users;
    }

    //Update User Status (User Status Page)
    public void updateUserStatus(User user, String newStatus) throws Exception{
        String idToken = UserSession.getInstance().getidToken();

        String url = "https://firestore.googleapis.com/v1/projects/"
                + projectId + "/databases/(default)/documents/users/" + user.getId()
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

        System.out.println(response.body());
    }

    //Add New Product (Product Page)
    public void addProduct(String newProduct) throws Exception{
        String idToken = UserSession.getInstance().getidToken();

        String url = "https://firestore.googleapis.com/v1/projects/" + projectId + "/databases/(default)/documents/products";

        String json = "{ \"fields\": { " +
                "\"name\": { \"stringValue\": \"" + newProduct + "\" } " +
                "} }";
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + idToken)
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
        throw new RuntimeException("Create Task Failed: " + response.body());
        }
    }

    private String getField(JsonObject fields, String key){
        if (fields.has(key)){
            return fields.getAsJsonObject(key).get("stringValue").getAsString();
        }
        return "";
    }
}
