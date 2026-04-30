import java.util.*;
import java.time.*;
import javafx.util.Duration;
import javafx.beans.binding.Bindings;

import com.google.gson.*;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.*;

public class mainpage extends Application{ 
    private Filtermode currentMode = Filtermode.ALL;

    enum Filtermode{
        ALL,
        MY_TASK,
        COMPLETE
    };
    @Override
    public void start(Stage stage){
        BorderPane mainpage = new BorderPane();

        //Menu
        //Task Menu Item
        MenuItem post = new MenuItem("Post Task");

        //Administrator Menu Item
        MenuItem status = new MenuItem("User Status");
        MenuItem addUser = new MenuItem("Add User");

        //Help Menu Item
        MenuItem about = new MenuItem("About");
        MenuItem logout = new MenuItem("Logout");
        MenuItem exit = new MenuItem("Exit");

        Menu taskMenu = new Menu("Task");
        taskMenu.getItems().addAll(post);
        Menu adminMenu = new Menu("Administrator Tool");
        adminMenu.getItems().addAll(status, addUser);
        Menu helpMenu = new Menu("Help");
        helpMenu.getItems().addAll(about,logout,exit);

        MenuBar menu = new MenuBar();
        menu.getMenus().addAll(taskMenu, adminMenu, helpMenu);

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

                    Label CompanyName = new Label("Company Name: " + task.getCompanyName());
                    Label CustomerName = new Label("Name: " + task.getCustomerName());
                    Label ContactNumber = new Label("Contact Number: " + task.getContactNumber());
                    Label Software = new Label("Software: " + task.getSoftware());
                    Label Time = new Label(task.getCreateTime());
                    //The Person in Charge or Pending
                    Label pic = new Label("The Person in Charge");
                    Label taskStatus = new Label("");
                    taskStatus.setAlignment(Pos.CENTER);
                    if (!task.getAssignedTo().equals("Everyone")) {
                        taskStatus.setText(task.getAssignedTo());
                    }
                    else{
                        taskStatus.setTextFill(Color.RED);
                        taskStatus.setText("Pending");
                    }
                    
                    VBox cardBox1 = new VBox(3);
                    cardBox1.setPrefWidth(200);
                    cardBox1.getChildren().addAll(CompanyName, CustomerName);
                    VBox cardBox2 = new VBox(3);
                    cardBox2.setPrefWidth(170);
                    cardBox2.getChildren().addAll(ContactNumber, Software);
                    VBox cardBox3 = new VBox(3);
                    cardBox3.setPrefWidth(150);
                    cardBox3.setAlignment(Pos.CENTER);
                    cardBox3.getChildren().addAll(pic, taskStatus);

                    //Urgent - Red Background
                    if (task.isUrgent()){
                        card.setStyle(
                        "-fx-background-color: #ffebee;" +
                        "-fx-border-color: #ccc;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;"
                        );
                    }

                    //Complete - Grey Background
                    if (task.getStatus().equalsIgnoreCase("Complete")){
                        card.setStyle(
                        "-fx-background-color: #90EE90;" +
                        "-fx-border-color: #ccc;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;"
                        );
                    }

                    card.add(Time, 0, 0);
                    card.add(cardBox1, 0, 1);
                    card.add(cardBox2, 1, 1);
                    card.add(cardBox3, 2, 1);
                    setGraphic(card);

                    //See Task Detail
                    setOnMouseClicked(e-> {
                        new detailpage(task).start(new Stage());
                    });
                }
            }
        });

        loadTasks(table, currentMode);
        //Refesh Task List
        Timeline refresh = new Timeline(
        new KeyFrame(Duration.seconds(5), e -> {
            loadTasks(table, currentMode);
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
        Label statusLabel = new Label("Loading...");
        statusLabel.setTextFill(Color.GREY);
        statusLabel.setAlignment(Pos.CENTER);
        Timeline statusRefresh = new Timeline(
            new KeyFrame(Duration.seconds(3), e-> {
                new Thread(() -> {
                    try{
                        UserSession session = UserSession.getInstance();
                        ProfileService ps = new ProfileService();

                        String userStatus = ps.getProfileStatus(session.getUid(), session.getidToken());

                        Platform.runLater(()-> {
                            if (userStatus.equalsIgnoreCase("Available")){
                                statusLabel.setTextFill(Color.GREEN);
                            }
                            if (userStatus.equalsIgnoreCase("OnSite")){
                                statusLabel.setTextFill(Color.GREENYELLOW);
                            }
                            if (userStatus.equalsIgnoreCase("Block")){
                                statusLabel.setTextFill(Color.RED);
                            }
                            statusLabel.setText(userStatus);
                        });
                    } catch (Exception ex){
                        ex.printStackTrace();
                    }
                }).start();
            })
        );
        statusRefresh.setCycleCount(Timeline.INDEFINITE);
        statusRefresh.play();

        Button statusBtn = new Button("Update Status");
        statusBtn.setOnAction(e-> {
            showUpdateStatus();
        });

        nameLabel.setMaxWidth(rightBox.getPrefWidth());
        statusLabel.setMaxWidth(rightBox.getPrefWidth());
        statusBtn.setMaxWidth(rightBox.getPrefWidth());
        rightBox.getChildren().addAll(nameLabel, statusLabel, statusBtn);
        right.add(rightBox, 0, 0);

        //Bottom Area
        GridPane bottom = new GridPane();
        bottom.setHgap(10);
        bottom.setVgap(10);
        bottom.setAlignment(Pos.CENTER);

        //Case Calculation
        Label label = new Label("Pending Task Amount:");
        Label count = new Label();
        count.textProperty().bind(
            Bindings.size(table.getItems()).asString("%d")
        );
        label.setFont(new Font("Times New Roman", 16));
        count.setFont(new Font("Times New Roman", 16));
        bottom.add(label, 0, 0);
        bottom.add(count, 1, 0);

        //Left Area
        GridPane left = new GridPane();
        VBox btnBox = new VBox();
        btnBox.setSpacing(10);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPrefWidth(100);

        //Task Type Button
        Button pendingBtn = new Button("Pending Task");
        Button mytaskBtn = new Button("My Task");
        Button completeBtn = new Button("Complete Task");

        pendingBtn.setMaxWidth(btnBox.getPrefWidth());
        mytaskBtn.setMaxWidth(btnBox.getPrefWidth());
        completeBtn.setMaxWidth(btnBox.getPrefWidth());

        btnBox.getChildren().addAll(pendingBtn, mytaskBtn, completeBtn);
        left.add(btnBox, 0, 0);
        
        //Pending Task
        pendingBtn.setOnAction(e-> {
            label.setText("Pending Task Amount:");
            currentMode = Filtermode.ALL;
            loadTasks(table, currentMode);
        });

        //My Task
        mytaskBtn.setOnAction(e-> {
            label.setText("My Pending Task Amount:");
            currentMode = Filtermode.MY_TASK;
            loadTasks(table, currentMode);
        });

        //Complete Task
        completeBtn.setOnAction(e-> {
            label.setText("Complete Task Amount:");
            currentMode = Filtermode.COMPLETE;
            loadTasks(table, currentMode);
        });

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

    public void loadTasks(ListView<Task> table, Filtermode mode){
        new Thread(() ->{
            try{
                String projectId = "task-management-86056";
                String idToken = UserSession.getInstance().getidToken();
                String currentUser = UserSession.getInstance().getName();

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

                            //Filter Task (My Pending Task)
                            if (mode == Filtermode.MY_TASK){
                                if (status.equalsIgnoreCase("Complete")){
                                    continue;
                                }
                                if (!assignedTo.equalsIgnoreCase(currentUser)){
                                    continue;
                                }
                            }

                            //Filter Task (Complete Task)
                            if (mode == Filtermode.COMPLETE){
                                if (!status.equalsIgnoreCase("Complete")){
                                    continue;
                                }
                            }

                            //Filter Task (All Pending Task)
                            if (mode == Filtermode.ALL){
                                if (status.equalsIgnoreCase("Complete")){
                                    continue;
                                }
                            }

                            tasks.sort(Comparator.comparing(Task::getCreateDateTime));
                            Task task = new Task(id, companyName, customerName, contactNumber, software, issue, postBy, assignedTo, method, email, urgent, createTime, status);
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

    public void showUpdateStatus(){
        Stage stage = new Stage();

        HBox btnBox = new HBox(10);
        btnBox.setAlignment(Pos.CENTER);

        Button avaBtn = new Button("Available");
        Button siteBtn = new Button("OnSite");
        Button blockBtn = new Button("Block");

        btnBox.getChildren().addAll(avaBtn, siteBtn, blockBtn);

        avaBtn.setOnAction(e-> {handleUpdateStatus("Available", stage);});
        siteBtn.setOnAction(e-> {handleUpdateStatus("OnSite", stage);});
        blockBtn.setOnAction(e-> {handleUpdateStatus("Block", stage);});

        Scene scene = new Scene(btnBox, 300, 200);
        stage.setTitle("Update Status");
        stage.setScene(scene);
        stage.show();
    }

    private void handleUpdateStatus(String newStatus, Stage stage){
        new Thread(() ->{
            try{
                UserSession session = UserSession.getInstance();
                ProfileService ps = new ProfileService();

                ps.updateStatus(
                    session.getUid(),
                    session.getidToken(),
                    newStatus
                );

                session.setStatus(newStatus);

                Platform.runLater(() ->{
                    stage.close();
                });
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        launch();
    }
}
