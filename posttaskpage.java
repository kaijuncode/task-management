import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class posttaskpage extends Application {
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
        gridpane.add(new Label("Post By:"), 2, 0);
        TextField Post = new TextField();
        gridpane.add(Post, 3, 0);

        //Assigned To
        gridpane.add(new Label("Assigned To:"), 2, 1);
        TextField Assign = new TextField();
        gridpane.add(Assign, 3, 1);

        //Urgent Button
        gridpane.add(new Label("Urgent?"), 2, 2);
        ToggleButton urgentBtn = new ToggleButton("If Urgent Click This");
        gridpane.add(urgentBtn, 3, 2);
        urgentBtn.setOnAction(e -> {
            if (urgentBtn.isSelected()){
                urgentBtn.setText("Urgent!!!");
                urgentBtn.setTextFill(Color.YELLOW);
                urgentBtn.setStyle("-fx-background-color: red;");
            }
            else {
                urgentBtn.setText("If Urgent Click This");
                urgentBtn.setTextFill(Color.BLACK);
                urgentBtn.setStyle("");
            }
        });

        //Time
        gridpane.add(new Label("Time:"), 2, 3);
        Label dateTime = new Label();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e->{
            dateTime.setText(LocalDateTime.now().format(formatter));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
        gridpane.add(dateTime, 3, 3);

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

    public static void main(String[] args) {
        launch();
    }
}