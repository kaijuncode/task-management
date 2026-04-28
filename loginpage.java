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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
        
        //Login Button
        Button loginBtn = new Button("Login");
        loginpage.add(loginBtn, 0, 2, 2, 1);
        loginBtn.setOnAction(event ->{
            loginBtn.setDisable(true);
            usernameInt.setDisable(true);
            passwordInt.setDisable(true);
            String email = usernameInt.getText();
            String password = passwordInt.getText();

            new Thread(() ->{
                try{
                    FirebaseAuthService auth = new FirebaseAuthService();
                    AuthResult result = auth.login(email, password);

                    String name = null;
                    try {
                        name = getProfileName(result.Uid, result.idToken);
                    } catch (Exception e) {
                        name = null;
                    }

                    UserSession session = UserSession.getInstance();
                    session.setUid(result.Uid);
                    session.setidToken(result.idToken);
                    session.setEmail(result.email);

                    if (name != null){
                        session.setName(name);
                        Platform.runLater(() ->{
                            stage.close();
                            mainpage otherPage = new mainpage();
                            Stage newStage = new Stage();
                            otherPage.start(newStage);
                        });
                    }
                    else{
                        Platform.runLater(() ->{
                            stage.close();
                            showCreateProfilePage(result);
                        });
                    }
                } catch (Exception e){
                    Platform.runLater(() ->{
                        usernameInt.setText("");
                        passwordInt.setText("");
                        usernameInt.setDisable(false);
                        passwordInt.setDisable(false);
                        loginBtn.setDisable(false);

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

        UserSession session = UserSession.getInstance();
        session.setUid(authResult.Uid);
        session.setidToken(authResult.idToken);
        session.setEmail(authResult.email);

        saveBtn.setOnAction(e ->{
            nameInput.setDisable(true);
            saveBtn.setDisable(true);
            if (nameInput.getText().isEmpty()){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Validation Error");
                alert.setHeaderText("Name Required");
                alert.setContentText("Please enter your name to create profile.");

                alert.showAndWait();
                nameInput.setDisable(false);
                saveBtn.setDisable(false);
                return;
            }
            new Thread(() ->{
                try{
                    createProfile(
                        authResult.Uid,
                        authResult.idToken,
                        authResult.email,
                        nameInput.getText()
                    );

                    session.setName(nameInput.getText());
                    Platform.runLater(() ->{
                        stage.close();

                        mainpage otherPage = new mainpage();
                        Stage main = new Stage();
                        otherPage.start(main);
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
                + projectID + "/databases/(default)/documents/users/" + uid;

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

    public String getProfileName(String uid, String idToken) throws Exception {
        String projectID = "task-management-86056";

        String url = "https://firestore.googleapis.com/v1/projects/"
                + projectID + "/databases/(default)/documents/users/" + uid;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Authorization", "Bearer " + idToken)
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> respond = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (respond.statusCode() == 200){
            return extractName(respond.body());
        }
        else{
            throw new RuntimeException();
        }
    }

    public String extractName(String json){
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();

        return root.getAsJsonObject("fields")
                .getAsJsonObject("name")
                .get("stringValue")
                .getAsString();
    }
    public static void main(String[] args) {
        launch();
    }
}
