import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.image.*;
import javafx.scene.layout.*;

import java.net.URI;
import java.net.http.*;
import com.google.gson.*;

public class posttaskpage extends Application {
    private boolean Urgent = false;
    @Override
    public void start(Stage stage) {
        GridPane gridpane = new GridPane();
        gridpane.setHgap(10);
        gridpane.setVgap(10);
        gridpane.setPadding(new Insets(10));

        //Company Name
        gridpane.add(new Label("Company Name:"), 0, 0);
        TextField cmyName = new TextField();
        gridpane.add(cmyName, 1,0);

        //Customer Name
        gridpane.add(new Label("Name:"), 0, 1);
        TextField Name = new TextField();
        gridpane.add(Name, 1, 1);

        //Contact Number
        gridpane.add(new Label("Contact Number:"), 0, 2);
        TextField Contact = new TextField();
        gridpane.add(Contact, 1, 2);

        //Software
        gridpane.add(new Label("Software:"), 0, 3);
        TextField Software = new TextField();
        gridpane.add(Software, 1, 3);

        //Issue/Request
        gridpane.add(new Label("Issue/Request:"), 0, 4);
        TextField IssueReq = new TextField();
        gridpane.add(IssueReq, 1, 4);

        //PostBy
        Image icon_wrench = new Image("image/icon_wrench.png");
        ImageView wrench = new ImageView(icon_wrench);
        wrench.setFitWidth(10);
        wrench.setFitHeight(10);
        gridpane.add(new Label("Post By:"), 2, 0);
        TextField Post = new TextField();
        Post.setText(UserSession.getInstance().getName());
        Post.setDisable(true);
        gridpane.add(Post, 3, 0);
        Button change = new Button("", wrench);
        gridpane.add(change, 4, 0);
        change.setOnAction(e-> {
            if (Post.isDisable()){
                Post.setDisable(false);
            }
            else{
                Post.setDisable(true);
            }
        });

        //Assigned To
        gridpane.add(new Label("Assigned To:"), 2, 1);
        ComboBox<String> userList = new ComboBox<>();
        loadUsersfromFirebase(userList);
        gridpane.add(userList, 3, 1);

        //Hotline or Email
        ToggleGroup tg = new ToggleGroup();

        RadioButton hotline = new RadioButton("Hotline");
        RadioButton email  = new RadioButton("Email:");
        TextField emailInt = new TextField("-");
        emailInt.setDisable(true);

        hotline.setToggleGroup(tg);
        email.setToggleGroup(tg);
        hotline.setSelected(true);

        gridpane.add(hotline, 2, 2);
        HBox emailBox = new HBox(5);
        emailBox.setAlignment(Pos.CENTER_LEFT);
        emailBox.getChildren().addAll(email, emailInt);
        gridpane.add(emailBox, 3, 2);

        email.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if(isNowSelected){
                emailInt.clear();
                emailInt.setDisable(false);
            }
            else{
                emailInt.setDisable(true);
                emailInt.setText("-");
            }
        });
        
        //Urgent Button
        RadioButton urgent = new RadioButton("Urgent");
        gridpane.add(urgent, 2, 3);
        urgent.setOnAction(e-> {
            if (urgent.isSelected()){
               Urgent = true;
            }
            else{
                Urgent = false;
            }
        });

        //Time
        gridpane.add(new Label("Time:"), 2, 4);
        Label dateTime = new Label();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e->{
            dateTime.setText(LocalDateTime.now().format(formatter));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
        gridpane.add(dateTime, 3, 4);

        //Post Task
        Button postBtn = new Button("Post Task");
        gridpane.add(postBtn, 2, 6, 2, 1);
        postBtn.setOnAction(e ->{
            String company = cmyName.getText();
            String customer = Name.getText();
            String contact = Contact.getText();
            String software = Software.getText();
            String issue = IssueReq.getText();
            String postBy = Post.getText();
            String assignedTo = userList.getValue();
            String method = hotline.isSelected() ? "Hotline" : "Email";
            String emailVal = emailInt.getText();
            String status = "Pending";
            String createTime = LocalDateTime.now().format(formatter);

            new Thread(() ->{
                try{
                    //Post Task to Firestore
                    createTask(company,customer,contact,software,issue,postBy,assignedTo,method,emailVal,Urgent,createTime,status);
                    Platform.runLater(() ->{
                        cmyName.clear();
                        Name.clear();
                        Contact.clear();
                        Software.clear();
                        IssueReq.clear();
                        userList.getSelectionModel().selectFirst();
                        hotline.setSelected(true);
                        email.setSelected(false);
                        urgent.setSelected(false);
                        Alert success = new Alert(Alert.AlertType.INFORMATION);
                        success.setTitle("Success");
                        success.setHeaderText("Task Posted Successfully");
                        success.setContentText("The task has been posted successfully. Time: " + createTime);
                        success.showAndWait();
                    });
                } catch (Exception ex){
                    ex.printStackTrace();

                    Platform.runLater(() ->{
                        Alert error = new Alert(Alert.AlertType.ERROR);
                        error.setTitle("Error");
                        error.setHeaderText("Failed to Post Task");
                        error.setContentText("An error occurred while posting the task. Please try again.");
                        error.showAndWait();
                    });
                }
            }).start();
        });

        Scene scene = new Scene(gridpane, 600, 300);
        stage.setTitle("Add Task");
        stage.setScene(scene);
        stage.show();
    }

    //Assign Task - User List
    public void loadUsersfromFirebase(ComboBox<String> userList){
        new Thread(() ->{
            try{
                String projectId = "task-management-86056";
                String idToken = UserSession.getInstance().getidToken();

                String url = "https://firestore.googleapis.com/v1/projects/" + projectId + "/databases/(default)/documents/users";

                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().header("Authorization", "Bearer " + idToken).build();
        
                HttpClient client = HttpClient.newHttpClient();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                List<String> userName = new ArrayList<>();
                userName.add("Everyone");

                if (response.statusCode() == 200){
                    JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();

                    if (root.has("documents")){
                        JsonArray documents = root.getAsJsonArray("documents");
                        for (JsonElement doc : documents){
                            JsonObject fields = doc.getAsJsonObject().getAsJsonObject("fields");
                    
                            if (fields.has("name")){
                                String name = fields.getAsJsonObject("name").get("stringValue").getAsString();
                                if (name.equals("ADMIN")){
                                    continue;
                                }
                                userName.add(name);
                            }
                        }
                    }
                }
                Platform.runLater(() -> {
                    userList.getItems().addAll(userName);
                    userList.getSelectionModel().selectFirst();
                });
            } catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }

    //Create Task
    public void createTask(String company, String customer, String contact, String software, String issue, String postBy, String assignedTo, String method, String emailVal, boolean urgent, String createTime, String status) throws Exception{
        String projectId = "task-management-86056";
        String idToken = UserSession.getInstance().getidToken();

        String url = "https://firestore.googleapis.com/v1/projects/" + projectId + "/databases/(default)/documents/tasks";

        String json = "{ \"fields\": { " +
                "\"company\": { \"stringValue\": \"" + company + "\" }, " +
                "\"customer\": { \"stringValue\": \"" + customer + "\" }, " +
                "\"contact\": { \"stringValue\": \"" + contact + "\" }, " +
                "\"software\": { \"stringValue\": \"" + software + "\" }, " +
                "\"issue\": { \"stringValue\": \"" + issue + "\" }, " +
                "\"postBy\": { \"stringValue\": \"" + postBy + "\" }, " +
                "\"assignedTo\": { \"stringValue\": \"" + assignedTo + "\" }, " +
                "\"method\": { \"stringValue\": \"" + method + "\" }, " +
                "\"emailVal\": { \"stringValue\": \"" + emailVal + "\" }, " +
                "\"urgent\": { \"booleanValue\": " + urgent + " }, " +
                "\"createTime\": { \"stringValue\": \"" + createTime + "\" }, " +
                "\"status\": { \"stringValue\": \"" + status + "\" }" +
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
    public static void main(String[] args) {
        launch();
    }
}