import java.util.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class PageUserrole extends Application{
    private final ServiceAdministration as = new ServiceAdministration();
    private ListView<UserRole> roleListRef;
    private Stage userRoleStage;
    @Override
    public void start(Stage stage){
        BorderPane role = new BorderPane();

        ListView<UserRole> roleList = new ListView<>();
        roleList.setSelectionModel(null);
        roleList.setFocusTraversable(false);
        roleListRef = roleList;

        roleList.setCellFactory(param -> new ListCell<UserRole>(){
            @Override
            protected void updateItem(UserRole user, boolean empty){
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
                    usernameBox.setPrefWidth(80);
                    usernameBox.setAlignment(Pos.CENTER);
                    usernameBox.getChildren().addAll(userName);
                    Label role = new Label(user.getUserRole());
                    HBox roleBox = new HBox();
                    roleBox.setPrefWidth(80);
                    roleBox.setAlignment(Pos.CENTER);
                    roleBox.getChildren().addAll(role);
                    Button editBtn = new Button("Edit");
                    HBox btnBox = new HBox();
                    btnBox.setPrefWidth(50);
                    btnBox.setAlignment(Pos.CENTER);
                    btnBox.getChildren().addAll(editBtn);

                    card.add(usernameBox, 0, 0);
                    card.add(roleBox, 1, 0);
                    card.add(btnBox, 2, 0);
                    setGraphic(card);

                    editBtn.setOnAction(e->{
                        if (UserSession.getInstance().getName().equals("ADMIN")){
                            editRoleWindow(user);
                        }
                        else {
                            Alert warning = new Alert(Alert.AlertType.WARNING);
                            warning.setHeaderText("Access Denied");
                            warning.setContentText("Need ADMIN-ACCESS");
                            warning.showAndWait();
                        }
                    });
                }
            }
        });
        loadUserRole(roleList);

        role.setCenter(roleList);
        Scene scene = new Scene(role, 300, 200);
        stage.setTitle("User Role");
        stage.setScene(scene);
        stage.show();
    }

    public void editRoleWindow(UserRole user){
        if (userRoleStage != null && userRoleStage.isShowing()){
            Alert roleWarning = new Alert(Alert.AlertType.WARNING);
            roleWarning.setTitle("Warning");
            roleWarning.setHeaderText("Continue Edit or Close?");
            roleWarning.setContentText("There is still a EditWindow open, pls complete this first.");
            roleWarning.showAndWait();

            userRoleStage.setIconified(false);
            userRoleStage.toFront();
            userRoleStage.requestFocus();
            return;
        }

        userRoleStage = new Stage();

        userRoleStage.setOnCloseRequest(e-> {
            userRoleStage = null;
        });

        VBox roleWindow = new VBox(10);
        roleWindow.setAlignment(Pos.CENTER);
        roleWindow.setPadding(new Insets(10));

        Label title = new Label("Update Role for " + user.getUserName());

        Button admin = new Button("Admin");
        Button support = new Button("Support");

        admin.setMaxWidth(Double.MAX_VALUE);
        support.setMaxWidth(Double.MAX_VALUE);

        admin.setOnAction(e -> editRole(user, "Admin", userRoleStage));
        support.setOnAction(e -> editRole(user, "Support", userRoleStage));

        roleWindow.getChildren().addAll(title, admin, support);

        Scene scene = new Scene(roleWindow, 250, 200);
        userRoleStage.setTitle("Edit Role");
        userRoleStage.setScene(scene);
        userRoleStage.show();
    }

    public void editRole(UserRole userRole, String newRole, Stage window){
        new Thread(()->{
            try{
                as.updateUserRole(userRole, newRole);
                Platform.runLater(()->{
                    window.close();
                    refreshRole();
                });
            } catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }

    public void refreshRole(){
        loadUserRole(roleListRef);
    }

    public void loadUserRole(ListView<UserRole> roleList){
        new Thread(() ->{
            try{
                List<UserRole> users = as.getUserRole();
                Platform.runLater(()-> {
                    roleList.getItems().setAll(users);
                });
            } catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }
}