import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class loginpage extends Application{
    public String username = "KaiJun";
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
                    String result = auth.login(email, password);

                    Platform.runLater(() ->{
                        stage.close();
                        Stage newStage = new Stage();
                        otherPage.start(newStage);
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
    public static void main(String[] args) {
        launch();
    }
}
