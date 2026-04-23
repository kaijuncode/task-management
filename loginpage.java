import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class loginpage extends Application{
    @Override
    public void start(Stage stage){
        GridPane loginpage = new GridPane();
        loginpage.setHgap(10);
        loginpage.setVgap(10);
        loginpage.setAlignment(Pos.CENTER);

        //Username Input
        loginpage.add(new Label("Email:"), 0, 0);
        TextField usernameInt = new TextField();
        loginpage.add(usernameInt, 1, 0);

        //Password Input
        loginpage.add(new Label("Password"), 0, 1);
        PasswordField passwordInt = new PasswordField();
        loginpage.add(passwordInt, 1, 1);

        mainpage otherPage = new mainpage();
        
        //Login Button
        Button loginBtn = new Button("Login");
        loginpage.add(loginBtn, 0, 2, 2, 1);
        loginBtn.setOnAction(event ->{
            String email = usernameInt.getText();
            String password = passwordInt.getText();

            new Thread(() ->{
                try{
                    FirebaseAuthService auth = new FirebaseAuthService();
                    AuthResult result = auth.login(email, password);

                    ProfileService profileService = new ProfileService();
                    boolean hasProfile = profileService.checkUserProfile(result.Uid, result.idToken);

                    Platform.runLater(() ->{
                        stage.close();
                        
                        if (hasProfile){
                            Stage newStage = new Stage();
                            otherPage.start(newStage);
                        } else {
                            showCreateProfilePage(result);
                        }
                    });
                } catch (Exception e){
                    Platform.runLater(() ->{
                        usernameInt.setText("");
                        passwordInt.setText("");

                        Alert loginAlert = new Alert(Alert.AlertType.WARNING);
                        loginAlert.setTitle("Login Failed");
                        loginAlert.setHeaderText("Login Failed");
                        loginAlert.setContentText("Incorrect Email or Password!!!");

                        loginAlert.showAndWait();
                    });
                }
            }).start();
        });

        Scene scene = new Scene(loginpage, 400, 200);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    private void showCreateProfilePage(AuthResult authResult){
        Stage stage = new Stage();

        GridPane profile = new GridPane();
        profile.setHgap(10);
        profile.setVgap(10);
        profile.setAlignment(Pos.CENTER);

        TextField nameInput = new TextField();

        profile.add(new Label("Name:"), 0, 0);
        profile.add(nameInput, 1, 0);

        Button saveBtn = new Button("Save");
        profile.add(saveBtn, 0, 1, 2, 1);

        saveBtn.setOnAction(e ->{
            new Thread(() ->{
                try{
                    createProfile(
                        authResult.Uid,
                        authResult.idToken,
                        authResult.email,
                        nameInput.getText()
                    );

                    Platform.runLater(() ->{
                        stage.close();

                        Stage main = new Stage();
                        new mainpage().start(main);
                    });
                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }).start();
        });

        Scene scene = new Scene(profile, 300, 200);
        stage.setTitle("Create Profile");
        stage.setScene(scene);
        stage.show();
    }

    public void createProfile(String uid, String idToken, String email, String name) throws Exception{
        String projectID = "task-management-86056";
        String url = "https://firestore.googleapis.com/v1/projects/"
                + projectID + "/databases/task/documents/users/" + uid;

        String json = "{ \"fields\": { " +
                "\"email\": { \"stringValue\": \"" + email + "\" }, " +
                "\"name\": { \"stringValue\": \"" + name + "\" } " +
                "} }";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .header("Authorization","Bearer " + idToken)
                .build();

        HttpClient client = HttpClient.newHttpClient();
        client.send(request, HttpResponse.BodyHandlers.ofString());
    }
    public static void main(String[] args) {
        launch();
    }
}
