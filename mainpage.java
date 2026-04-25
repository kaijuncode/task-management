import java.util.Optional;

import com.google.gson.*;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.*;

public class mainpage extends Application{ 
    private String Uid;
    private String idToken;

    public mainpage(String Uid, String idToken) {
        this.Uid = Uid;
        this.idToken = idToken;
    }
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
        VBox container = new VBox();
        container.setSpacing(10);

        int j = 0;
        for (int i = 1; i <= 50; i++){
            GridPane itemGrid = new GridPane();
            itemGrid.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-padding: 10;");
            itemGrid.add(new Label("ID: " + i), 0, 0);
            itemGrid.add(new Label("Pending"), 0, 1);

            container.getChildren().add(itemGrid);
            j++;
        };

        ScrollPane scroll = new ScrollPane();
        scroll.setContent(container);

        scroll.setFitToWidth(true);
        scroll.setPannable(true);

        //Testing Right
        GridPane right = new GridPane();
        right.setHgap(10);
        right.setVgap(10);
        right.setPadding(new Insets(10));

        //UserName
        Label nameLabel = new Label();
        right.add(nameLabel, 0, 0);
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

        //User Status
        String setting = "Available";
        Button setStatus = new Button("Status");
        setStatus.setAlignment(Pos.CENTER);
        right.add(setStatus, 0, 1);
        setStatus.setOnAction(e-> {
            showSetStatusPage(setting);
        });

        //Testing Left
        GridPane left = new GridPane();
        left.setHgap(10);
        left.setVgap(10);
        left.setPadding(new Insets(10));

        left.add(new Label("Testing"), 0, 0);

        //Testing Bottom
        GridPane bottom = new GridPane();
        bottom.setHgap(10);
        bottom.setVgap(10);
        bottom.setAlignment(Pos.CENTER);

        Label pending = new Label("Pending Case Amount: " + j);
        pending.setFont(new Font("Times New Roman", 16));
        bottom.add(pending, 0, 0);

        mainpage.setTop(menu);
        mainpage.setCenter(scroll);
        mainpage.setRight(right);
        mainpage.setLeft(left);
        mainpage.setBottom(bottom);

        Scene scene = new Scene(mainpage, 1000, 600);
        stage.setTitle("Task Management System");
        stage.setScene(scene);
        stage.show();
    }

    public void showSetStatusPage(String setting){
        Stage stage = new Stage();

        GridPane status = new GridPane();
        status.setHgap(10);
        status.setVgap(10);
        status.setPadding(new Insets(5));

        Button busy = new Button("busy");
        status.add(busy, 0, 0);
        busy.setOnAction(e-> {
            stage.close();
        });

        Scene scene = new Scene(status, 300, 200);
        stage.setTitle("Set Status");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}
