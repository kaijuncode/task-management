import java.util.*;
import java.time.*;
import javafx.util.Duration;

import com.google.gson.*;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.*;

public class mainpage extends Application{ 
    @Override
    public void start(Stage stage){
        BorderPane mainpage = new BorderPane();

        //Menu
        //Task Menu Item
        MenuItem post = new MenuItem("Post Task");
        MenuItem transfer = new MenuItem("Transfer Task");

        //Help Menu Item
        MenuItem about = new MenuItem("About");
        MenuItem logout = new MenuItem("Logout");
        MenuItem exit = new MenuItem("Exit");

        Menu taskMenu = new Menu("Task");
        taskMenu.getItems().addAll(post, transfer);
        Menu helpMenu = new Menu("Help");
        helpMenu.getItems().addAll(about,logout,exit);

        MenuBar menu = new MenuBar();
        menu.getMenus().addAll(taskMenu, helpMenu);

        //Post Task Page
        posttaskpage posttaskPage = new posttaskpage();
        post.setOnAction(e ->{
            Stage newStage = new Stage();
            posttaskPage.start(newStage);
        });

        //About Page
        aboutpage aboutPage = new aboutpage(); 
        about.setOnAction(e ->{
            Stage newStage = new Stage();
            aboutPage.start(newStage);
        });

        //Logout
        loginpage loginPage = new loginpage();
        logout.setOnAction(e ->{
            Alert LOGOUT = new Alert(Alert.AlertType.CONFIRMATION);
            LOGOUT.setTitle("LOGOUT");
            LOGOUT.setHeaderText("Logout?");
            LOGOUT.setContentText("Click OK will go back Login Page.");

            Optional<ButtonType> result = LOGOUT.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK){
                stage.close();
                Stage newStage = new Stage();
                loginPage.start(newStage);
            }
        });

        //EXIT
        exit.setOnAction(e ->{
            Alert EXIT = new Alert(Alert.AlertType.CONFIRMATION);
            EXIT.setTitle("EXIT");
            EXIT.setHeaderText("Exit?");
            EXIT.setContentText("Click OK will close this application.");

            Optional<ButtonType> result = EXIT.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK){
                System.exit(0);
            }
        });

        //Center Area - Pending Case
        ListView<Task> table = new ListView<>();
        table.setSelectionModel(null);
        table.setFocusTraversable(false);

        table.setCellFactory(param -> new ListCell<Task>(){
            @Override
            protected void updateItem(Task task, boolean empty){
                super.updateItem(task, empty);

                if (empty || task == null){
                    setGraphic(null);
                    return;
                } 
                
                else {
                    GridPane card = new GridPane();
                    card.setHgap(10);
                    card.setVgap(5);
                    card.setPadding(new Insets(10));

                    card.setStyle(
                        "-fx-background-color: #f5f5f5;" +
                        "-fx-border-color: #ccc;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;"
                    );

                    card.add(new Label("Company Name: " + task.getCompanyName()), 0, 0);
                    card.add(new Label("Name: " + task.getCustomerName()), 0, 1);
                    card.add(new Label("Contact Number: " + task.getContactNumber()), 1, 0);
                    card.add(new Label("Software: " + task.getSoftware()), 1, 1);

                    if (task.isUrgent()){
                        card.setStyle("-fx-background-color: #ffebee;");
                    }

                    setGraphic(card);

                    setOnMouseClicked(e-> {
                        new detailpage(task).start(new Stage());
                    });
                }
            }
        });

        loadTasks(table);
        Timeline refresh = new Timeline(
        new KeyFrame(Duration.seconds(5), e -> {
            loadTasks(table);
            })
        );
        refresh.setCycleCount(Timeline.INDEFINITE);
        refresh.play();

        //Right Area
        GridPane right = new GridPane();
        VBox rightBox = new VBox();
        rightBox.setSpacing(10);
        rightBox.setAlignment(Pos.CENTER);
        rightBox.setPrefWidth(100);

        //UserName
        Label nameLabel = new Label();
        nameLabel.setAlignment(Pos.CENTER);
        new Thread(() ->{
            try{
                String name = UserSession.getInstance().getName();

                Platform.runLater(() ->{
                    nameLabel.setText(name);
                });
            } catch (Exception e){
                nameLabel.setText("Error");
            }
        }).start();

        //Status
        Label statusLabel = new Label("Available");
        statusLabel.setAlignment(Pos.CENTER);
        Button statusBtn = new Button("Update Status");

        nameLabel.setMaxWidth(rightBox.getPrefWidth());
        statusLabel.setMaxWidth(rightBox.getPrefWidth());
        statusBtn.setMaxWidth(rightBox.getPrefWidth());
        rightBox.getChildren().addAll(nameLabel, statusLabel, statusBtn);
        right.add(rightBox, 0, 0);

        //Left Area
        GridPane left = new GridPane();
        VBox btnBox = new VBox();
        btnBox.setSpacing(10);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPrefWidth(100);

        //Task Type Button
        Button pendingBtn = new Button("Pending Task");
        Button urgentBtn = new Button("Urgent Task");
        Button mytaskBtn = new Button("My Task");

        pendingBtn.setMaxWidth(btnBox.getPrefWidth());
        urgentBtn.setMaxWidth(btnBox.getPrefWidth());
        mytaskBtn.setMaxWidth(btnBox.getPrefWidth());

        btnBox.getChildren().addAll(pendingBtn, urgentBtn, mytaskBtn);
        left.add(btnBox, 0, 0);
        
        pendingBtn.setOnAction(e-> {
            mainpage.setCenter(table);
        });

        urgentBtn.setOnAction(e-> {
            //mainpage.setCenter(scroll1);
        });

        mytaskBtn.setOnAction(e-> {
            //mainpage.setCenter(mytaskTable);
        });

        //Bottom Area
        GridPane bottom = new GridPane();
        bottom.setHgap(10);
        bottom.setVgap(10);
        bottom.setAlignment(Pos.CENTER);

        //Case Calculation
        Label pending = new Label("Pending Case Amount: ");
        pending.setFont(new Font("Times New Roman", 16));
        bottom.add(pending, 0, 0);

        mainpage.setTop(menu);
        mainpage.setCenter(table);
        mainpage.setRight(right);
        mainpage.setLeft(left);
        mainpage.setBottom(bottom);

        Scene scene = new Scene(mainpage, 800, 600);
        stage.setTitle("Task Management System");
        stage.setScene(scene);
        stage.show();
    }

    public void loadTasks(ListView<Task> table){
        new Thread(() ->{
            try{
                String projectId = "task-management-86056";
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

                            Task task = new Task(companyName, customerName, contactNumber, software, issue, postBy, assignedTo, method, email, urgent, createTime, status);
                            tasks.add(task);
                        }
                    }
                }
                Platform.runLater(() ->{
                    table.getItems().setAll(tasks);
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
