import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.image.*;
import javafx.scene.layout.*;

import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;

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
        Label createTime1 = new Label("Post At");
        createTime1.setMaxWidth(Double.MAX_VALUE);
        createTime1.setAlignment(Pos.CENTER_RIGHT);
        gridpane.add(createTime1, 1, 7);
        Label createTime2 = new Label();
        gridpane.add(createTime2, 2, 7, 2, 1);
        Button postBtn = new Button("Post Task");
        gridpane.add(postBtn, 2, 6, 2, 1);
        postBtn.setOnAction(e ->{
            createTime2.setText(LocalDateTime.now().format(formatter));
        });

        Scene scene = new Scene(gridpane, 600, 300);
        stage.setTitle("Add Task");
        stage.setScene(scene);
        stage.show();
    }

    public void loadUsersfromFirebase(ComboBox<String> userList){
        new Thread(() ->{
            try{
                Firestore db = FirestoreClient.getFirestore();

                CollectionReference usersRef = db.collection("users");
                ApiFuture<QuerySnapshot> query = usersRef.get();

                List<String> userNames = new ArrayList<>();
                userNames.add("Everyone");

                for (DocumentSnapshot doc : query.get().getDocuments()){
                    String nameValue = doc.getString("name");
                    if (nameValue != null){
                        userNames.add(nameValue);
                    }
                }

                Platform.runLater(() ->{
                    userList.getItems().setAll(userNames);
                    userList.getSelectionModel().selectFirst();
                });
            } catch (Exception e){
                System.out.println("");
            }
        }).start();
    }
    public static void main(String[] args) {
        launch();
    }
}