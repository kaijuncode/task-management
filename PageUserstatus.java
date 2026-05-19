import java.util.*;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PageUserstatus extends Application{
    private final ServiceAdministration as = new ServiceAdministration();
    private ListView<User> statusListRef;
    private Stage statusStage;
    @Override
    public void start(Stage stage){
        BorderPane status = new BorderPane();

        ListView<User> statusList = new ListView<>();
        statusList.setSelectionModel(null);
        statusList.setFocusTraversable(false);
        statusListRef = statusList;

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
                    HBox usernameBox = new HBox();
                    usernameBox.setPrefWidth(60);
                    usernameBox.setAlignment(Pos.CENTER);
                    usernameBox.getChildren().addAll(userName);
                    Label status = new Label(user.getUserStatus());
                    HBox statusBox = new HBox();
                    statusBox.setPrefWidth(60);
                    statusBox.setAlignment(Pos.CENTER);
                    statusBox.getChildren().addAll(status);
                    Label pending = new Label("Pending Task Amount: " + user.getPendingCount());
                    HBox pendingBox = new HBox();
                    pendingBox.setPrefWidth(150);
                    pendingBox.setAlignment(Pos.CENTER);
                    pendingBox.getChildren().addAll(pending);
                    Button editBtn = new Button("Edit");
                    HBox btnBox = new HBox();
                    btnBox.setPrefWidth(50);
                    btnBox.setAlignment(Pos.CENTER);
                    btnBox.getChildren().addAll(editBtn);

                    if (user.getUserStatus().equals("Block")){
                        status.setTextFill(Color.RED);
                    }

                    if (user.getUserStatus().equals("OnSite")){
                        status.setTextFill(Color.GREENYELLOW);
                    }

                    if (user.getUserStatus().equals("Available")){
                        status.setTextFill(Color.GREEN);
                    }

                    if (user.getUserStatus().equals("OnLeave")){
                        status.setTextFill(Color.GREY);
                    }

                    card.add(usernameBox, 0, 0);
                    card.add(statusBox, 1, 0);
                    card.add(pendingBox, 2, 0);
                    card.add(btnBox, 3, 0);
                    setGraphic(card);

                    editBtn.setOnAction(e->{
                        if (UserSession.getInstance().getName().equals("ADMIN")){
                            editStatusWindow(user);
                        }
                        else {
                            Alert warning = new Alert(Alert.AlertType.WARNING);
                            warning.setHeaderText("Access Denied");
                            warning.setContentText("Need ADMIN-ACCESS.");
                            warning.showAndWait();
                        }
                    });
                }
            }
        });
        loadUserStatus(statusList);

        status.setCenter(statusList);
        Scene scene = new Scene(status, 450, 200);
        stage.setTitle("User Status");
        stage.setScene(scene);
        stage.show();
    }

    public void editStatusWindow(User user){
        if (statusStage != null && statusStage.isShowing()){
            Alert statusWarning = new Alert(Alert.AlertType.WARNING);
            statusWarning.setHeaderText("Continue Edit or Close?");
            statusWarning.setContentText("There is still a EditWindow open, pls complete this first.");
            statusWarning.showAndWait();

            statusStage.setIconified(false);
            statusStage.toFront();
            statusStage.requestFocus();
            return;
        }
        statusStage = new Stage();

        statusStage.setOnCloseRequest(e-> {
            statusStage = null;
        });

        VBox statusWindow = new VBox(10);
        statusWindow.setAlignment(Pos.CENTER);
        statusWindow.setPadding(new Insets(10));

        Label title = new Label("Update Status for " + user.getUserName());

        Button available = new Button("Available");
        Button onsite = new Button("OnSite");
        Button block = new Button("Block");
        Button leave = new Button("OnLeave");

        available.setMaxWidth(Double.MAX_VALUE);
        onsite.setMaxWidth(Double.MAX_VALUE);
        block.setMaxWidth(Double.MAX_VALUE);
        leave.setMaxWidth(Double.MAX_VALUE);

        available.setOnAction(e -> editStatus(user, "Available", statusStage));
        onsite.setOnAction(e -> editStatus(user, "OnSite", statusStage));
        block.setOnAction(e -> editStatus(user, "Block", statusStage));
        leave.setOnAction(e -> editStatus(user, "OnLeave", statusStage));

        statusWindow.getChildren().addAll(title, available, onsite, block, leave);

        Scene scene = new Scene(statusWindow, 250, 200);
        statusStage.setTitle("Edit Status");
        statusStage.setScene(scene);
        statusStage.show();
    }

    public void editStatus(User user, String newStatus, Stage window){
        new Thread(()->{
            try{
                as.updateUserStatus(user, newStatus);
                Platform.runLater(()-> {
                    window.close();
                    refreshStatus();
                });
            } catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }

    public void refreshStatus(){
        loadUserStatus(statusListRef);
    }

    public void loadUserStatus(ListView<User> statusList){
        new Thread(() ->{
            try{
                List<User> users = as.getUserStatus();
                Platform.runLater(()-> {
                    statusList.getItems().setAll(users);
                });
            } catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }
}