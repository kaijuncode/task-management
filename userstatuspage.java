import java.net.URI;
import java.net.http.*;
import java.util.*;
import com.google.gson.*;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;

public class userstatuspage extends Application{
    @Override
    public void start(Stage stage){
        GridPane status = new GridPane();

        ListView<User> statusList = new ListView<>();
        statusList.setSelectionModel(null);
        statusList.setFocusTraversable(false);

        statusList.setCellFactory(param -> new ListCell<User>(){
            @Override
            protected void updateItem(User user, boolean empty){
                super.updateItem(user, empty);

                if (empty || user == null){
                    setGraphic(null);
                    return;
                }
                else{
                    GridPane card = new GridPane();
                    card.setHgap(10);
                    card.setVgap(5);
                    card.setPadding(new Insets(10));

                    card.setStyle(
                        "-fx-background-color: #f7f7f7;"+
                        "-fx-background-radius: 8;"+
                        "-fx-border-color: black;"+
                        "-fx-border-radius: 8"
                    );

                    Label userName = new Label(user.getUserName());
                    Label status = new Label(user.getUserStatus());

                    if (user.getUserStatus().equals("Block")){
                        status.setTextFill(Color.RED);
                    }

                    if (user.getUserStatus().equals("OnSite")){
                        status.setTextFill(Color.GREENYELLOW);
                    }

                    if (user.getUserStatus().equals("Available")){
                        status.setTextFill(Color.GREEN);
                    }

                    card.add(userName, 0, 0);
                    card.add(status, 1, 0);
                    setGraphic(card);
                }
            }
        });
        loadUserStatus(statusList);

        status.add(statusList, 0, 0);
        Scene scene = new Scene(status, 200, 200);
        stage.setTitle("User Status");
        stage.setScene(scene);
        stage.show();
    }

    public void loadUserStatus(ListView<User> statusList){
        new Thread(() ->{
            try{
                String projectId = "task-management-86056";
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
                            users.add(user);
                        }
                    }
                }
                Platform.runLater(()-> {
                    statusList.getItems().setAll(users);
                });
            } catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }

    private String getField(JsonObject fields, String key){
        if (fields.has(key)){
            return fields.getAsJsonObject(key).get("stringValue").getAsString();
        }
        return "";
    }

    public static void main(String[] args) {
        launch();
    }
}