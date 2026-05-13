import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.layout.*;

import java.net.URI;
import java.net.http.*;
import com.google.gson.*;

public class detailpage extends Application{
    private Task task;
    private Stage transferStage;
    private Stage assignStage;

    public detailpage(Task task){
        this.task = task;
    }
    @Override
    public void start(Stage stage){
        TaskService ts = new TaskService();
        //To Update Last Updated Time
        posttaskpage ptp = new posttaskpage();

        GridPane gridpane = new GridPane();
        gridpane.setHgap(10);
        gridpane.setVgap(10);
        gridpane.setPadding(new Insets(10));

        //Company Name
        gridpane.add(new Label("Company Name:"), 0, 0);
        Label cmyName = new Label(task.getCompanyName());
        cmyName.setWrapText(true);
        cmyName.setMaxWidth(200);
        cmyName.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 3;" +
            "-fx-border-color: black;" +
            "-fx-border-radius: 5;"+
            "-fx-padding: 4 8 4 8"
        );
        gridpane.add(cmyName, 1,0);

        //Customer Name
        gridpane.add(new Label("Name:"), 0, 1);
        Label Name = new Label(task.getCustomerName());
        Name.setMaxWidth(200);
        Name.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 3;" +
            "-fx-border-color: black;" +
            "-fx-border-radius: 5;"+
            "-fx-padding: 4 8 4 8"
        );
        gridpane.add(Name, 1, 1);

        //Contact Number
        gridpane.add(new Label("Contact Number:"), 0, 2);
        Label Contact = new Label(task.getContactNumber());
        Contact.setMaxWidth(200);
        Contact.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 3;" +
            "-fx-border-color: black;" +
            "-fx-border-radius: 5;"+
            "-fx-padding: 4 8 4 8"
        );
        gridpane.add(Contact, 1, 2);

        //Software
        gridpane.add(new Label("Software:"), 0, 3);
        Label Software = new Label(task.getSoftware());
        Software.setMaxWidth(200);
        Software.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 3;" +
            "-fx-border-color: black;" +
            "-fx-border-radius: 5;"+
            "-fx-padding: 4 8 4 8"
        );
        gridpane.add(Software, 1, 3);

        //Issue/Request
        gridpane.add(new Label("Issue/Request:"), 0, 4);
        Label IssueReq = new Label(task.getIssue());
        IssueReq.setWrapText(true);
        IssueReq.setMaxWidth(200);
        IssueReq.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 3;" +
            "-fx-border-color: black;" +
            "-fx-border-radius: 5;"+
            "-fx-padding: 4 8 4 8"
        );
        gridpane.add(IssueReq, 1, 4);

        //PostBy
        gridpane.add(new Label("Post By:"), 2, 0);
        Label Post = new Label(task.getPostBy());
        Post.setMaxWidth(200);
        Post.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 3;" +
            "-fx-border-color: black;" +
            "-fx-border-radius: 5;"+
            "-fx-padding: 4 8 4 8"
        );
        gridpane.add(Post, 3, 0);

        //Assigned To
        gridpane.add(new Label("Assigned To:"), 2, 1);
        Label assignUser = new Label(task.getAssignedTo());
        assignUser.setMaxWidth(200);
        assignUser.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 3;" +
            "-fx-border-color: black;" +
            "-fx-border-radius: 5;"+
            "-fx-padding: 4 8 4 8"
        );
        gridpane.add(assignUser, 3, 1);

        //Hotline or Email
        gridpane.add(new Label("Contact Method:"), 2, 2);
        Label methodLabel = new Label(task.getMethod());
        methodLabel.setMaxWidth(200);
        methodLabel.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 3;" +
            "-fx-border-color: black;" +
            "-fx-border-radius: 5;"+
            "-fx-padding: 4 8 4 8"
        );
        gridpane.add(methodLabel, 3, 2);
        if (task.getMethod().equals("Email")) {
            methodLabel.setText("Email (" + task.getEmail() + ")");
        }
        
        //Urgent?
        gridpane.add(new Label("Urgent:"), 2, 3);
        Label urgent = new Label(task.isUrgent() ? "Yes" : "No");
        urgent.setMaxWidth(200);
        urgent.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 3;" +
            "-fx-border-color: black;" +
            "-fx-border-radius: 5;"+
            "-fx-padding: 4 8 4 8"
        );
        gridpane.add(urgent, 3, 3);

        //Time
        gridpane.add(new Label("Created Time:"), 2, 4);
        Label createTime = new Label(task.getCreateTime());
        createTime.setMaxWidth(200);
        createTime.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 3;" +
            "-fx-border-color: black;" +
            "-fx-border-radius: 5;"+
            "-fx-padding: 4 8 4 8"
        );
        gridpane.add(createTime, 3, 4);

        //Box for Accept and Assign Button
        HBox btnBox = new HBox(10);

        //Accept Task
        Button accept = new Button("Accept");
        accept.setVisible(false);
        if (task.getAssignedTo().equals("Everyone") && UserSession.getInstance().getRole().equalsIgnoreCase("support")) {
            accept.setVisible(true);
        }
        accept.setOnAction(e-> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Accept Task?");
            confirm.setHeaderText("Confirm Accept This Task?");
            confirm.setContentText("This task will assign to you.");

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK){
                try{
                    ts.acceptTask(task);
                    ptp.updateLastUpdated();
                } catch (Exception ex){
                    ex.printStackTrace();
                }
                stage.close();
            }
        });

        //Assign Task
        Button assign = new Button("Assign");
        assign.setVisible(false);
        if (task.getAssignedTo().equals("Everyone")) {
            assign.setVisible(true);
        }
        assign.setOnAction(e-> {
            assignTask(task);
            stage.close();
        });

        btnBox.setAlignment(Pos.CENTER_RIGHT);
        btnBox.getChildren().addAll(assign, accept);
        gridpane.add(btnBox, 1, 5);

        //Box for Transder and Complete Task
        HBox btnBox2 = new HBox(10);

        //Transfer Task
        Button transfer = new Button("Transfer");
        transfer.setVisible(false);
        if (task.getAssignedTo().equals(UserSession.getInstance().getName()) && !task.getStatus().equalsIgnoreCase("Complete")) {
            transfer.setVisible(true);
        }
        transfer.setOnAction(e-> {
            stage.close();
            transferTask(task);
        });

        //Complete Task
        Button done = new Button("Done");
        done.setVisible(false);
        if (task.getAssignedTo().equals(UserSession.getInstance().getName()) && !task.getStatus().equalsIgnoreCase("Complete")) {
            done.setVisible(true);
        }
        done.setOnAction(e-> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Complete Task?");
            confirm.setHeaderText("Confirm Complete This Task?");
            confirm.setContentText("This task will be marked as complete.");

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK){
                try{
                    ts.doneTask(task);
                    ptp.updateLastUpdated();
                } catch (Exception ex){
                    ex.printStackTrace();
                }
                stage.close();
            }
        });

        btnBox2.getChildren().addAll(done, transfer);
        gridpane.add(btnBox2, 2, 5);

        Scene scene = new Scene(gridpane, 700, 300);
        stage.setTitle("Detail");
        stage.setScene(scene);
        stage.show();
    }

    //Assign Pending Task to Someone
    public void assignTask(Task task){
        posttaskpage post = new posttaskpage();
        TaskService ts = new TaskService();

        assignStage = new Stage();

        assignStage.setOnCloseRequest(e-> {
            assignStage = null;
        });
        
        GridPane assignPane = new GridPane();
        assignPane.setHgap(10);
        assignPane.setVgap(10);
        assignPane.setAlignment(Pos.CENTER);

        HBox taskassignBox = new HBox();
        taskassignBox.setPrefWidth(300);
        taskassignBox.setAlignment(Pos.CENTER);
        Label taskAssign = new Label("Task " + task.getCompanyName() + " assign to?");
        taskassignBox.getChildren().addAll(taskAssign);

        HBox assignBox = new HBox(10);
        assignBox.setPrefWidth(300); 
        assignBox.setAlignment(Pos.CENTER);
        Label assignLabel = new Label("Assign To:");
        ComboBox<String> userList = new ComboBox<>();
        post.loadUsersfromFirebase(userList);
        assignBox.getChildren().addAll(assignLabel, userList);

        HBox btnBox = new HBox();
        btnBox.setPrefWidth(300); 
        btnBox.setAlignment(Pos.CENTER);
        Button assignBtn = new Button("Assign");
        btnBox.getChildren().addAll(assignBtn);

        assignPane.add(taskassignBox, 0, 0);
        assignPane.add(assignBox, 0, 1);
        assignPane.add(btnBox, 0, 2);

        assignBtn.setOnAction(e-> {
            String newAssign = userList.getValue();
            if (newAssign.equals("Everyone")){
                Alert warning = new Alert(Alert.AlertType.WARNING);
                warning.setHeaderText("Cannot Assign to Everyone Again");
                warning.setContentText("Need to assign to someone.");
                warning.showAndWait();
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setHeaderText("Confirm Assign?");
            confirm.setContentText("Confirm assign to " + newAssign + "?");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() != ButtonType.OK){
                return;
            }
            new Thread(()-> {
                try{
                    ts.assignTask(task, newAssign);

                    Platform.runLater(()->{
                        posttaskpage ptp = new posttaskpage();
                        new Thread(()-> {
                            try{
                                ptp.updateLastUpdated();
                            } catch (Exception ex){
                                ex.printStackTrace();
                            }
                        }).start();
                        assignStage.close();
                    });

                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }).start();
        });
        
        Scene scene = new Scene(assignPane, 300, 200);
        assignStage.setTitle("Assign Task");
        assignStage.setScene(scene);
        assignStage.show();
    }

    //Transfer Task to Others
    public void transferTask(Task task){
        TaskService ts = new TaskService();
        transferStage = new Stage();

        transferStage.setOnCloseRequest(e-> {
            transferStage = null;
        });

        GridPane transferPane = new GridPane();
        transferPane.setHgap(10);
        transferPane.setVgap(10);
        transferPane.setAlignment(Pos.CENTER);

        HBox transfertoBox = new HBox();
        transfertoBox.setPrefWidth(300);
        transfertoBox.setAlignment(Pos.CENTER);
        Label transferTo = new Label("Task " + task.getCompanyName() + " transfer to?");
        transfertoBox.getChildren().addAll(transferTo);

        HBox transferBox = new HBox(10);
        transferBox.setPrefWidth(300);
        transferBox.setAlignment(Pos.CENTER);
        Label transferLabel = new Label("Transfer To:");
        ComboBox<String> userList = new ComboBox<>();
        loadUserForTransfer(userList);
        transferBox.getChildren().addAll(transferLabel, userList);

        HBox btnBox = new HBox();
        btnBox.setPrefWidth(300);
        btnBox.setAlignment(Pos.CENTER);
        Button transferBtn = new Button("Transfer");
        btnBox.getChildren().addAll(transferBtn);

        transferPane.add(transfertoBox, 0, 0);
        transferPane.add(transferBox, 0, 1);
        transferPane.add(btnBox, 0, 2);

        transferBtn.setOnAction(e-> {
            String newTransfer = userList.getValue();
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setHeaderText("Confirm Transfer?");
            confirm.setContentText("Confirm transfer to " + newTransfer + "?");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() != ButtonType.OK){
                return;
            }
            new Thread(()-> {
                try{
                    ts.transferTask(task, newTransfer);
                    Platform.runLater(()->{
                        posttaskpage ptp = new posttaskpage();
                        new Thread(()-> {
                            try{
                                ptp.updateLastUpdated();
                            } catch (Exception ex){
                                ex.printStackTrace();
                            }
                        }).start();
                        transferStage.close();
                    });

                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }).start();
        });

        Scene scene = new Scene(transferPane, 300, 200);
        transferStage.setTitle("Transfer Task");
        transferStage.setScene(scene);
        transferStage.show();
    }

    //User List for Transfer Task
    public void loadUserForTransfer(ComboBox<String> userList){
        new Thread(()-> {
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
                                if (name.equals("ADMIN") || name.equals(UserSession.getInstance().getName())){
                                    continue;
                                }
                                String status = fields.getAsJsonObject("status").get("stringValue").getAsString();
                                if (status.equalsIgnoreCase("OnSite") || status.equalsIgnoreCase("Block")){
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
}
