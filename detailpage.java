import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.net.URI;
import java.net.http.*;
import com.google.gson.*;

public class detailpage extends Application{
    private Task task;

    public detailpage(Task task){
        this.task = task;
    }
    @Override
    public void start(Stage stage){
        GridPane gridpane = new GridPane();
        gridpane.setHgap(10);
        gridpane.setVgap(10);
        gridpane.setPadding(new Insets(10));

        //Company Name
        gridpane.add(new Label("Company Name:"), 0, 0);
        TextField cmyName = new TextField(task.getCompanyName());
        cmyName.setDisable(true);
        gridpane.add(cmyName, 1,0);

        //Customer Name
        gridpane.add(new Label("Name:"), 0, 1);
        TextField Name = new TextField(task.getCustomerName());
        Name.setDisable(true);
        gridpane.add(Name, 1, 1);

        //Contact Number
        gridpane.add(new Label("Contact Number:"), 0, 2);
        TextField Contact = new TextField(task.getContactNumber());
        Contact.setDisable(true);
        gridpane.add(Contact, 1, 2);

        //Software
        gridpane.add(new Label("Software:"), 0, 3);
        TextField Software = new TextField(task.getSoftware());
        Software.setDisable(true);
        gridpane.add(Software, 1, 3);

        //Issue/Request
        gridpane.add(new Label("Issue/Request:"), 0, 4);
        TextField IssueReq = new TextField(task.getIssue());
        IssueReq.setDisable(true);
        gridpane.add(IssueReq, 1, 4);

        //PostBy
        gridpane.add(new Label("Post By:"), 2, 0);
        TextField Post = new TextField(task.getPostBy());
        Post.setDisable(true);
        gridpane.add(Post, 3, 0);

        //Assigned To
        gridpane.add(new Label("Assigned To:"), 2, 1);
        TextField assignUser = new TextField(task.getAssignedTo());
        assignUser.setDisable(true);
        gridpane.add(assignUser, 3, 1);

        //Hotline or Email
        gridpane.add(new Label("Contact Method:"), 2, 2);
        Label methodLabel = new Label(task.getMethod());
        gridpane.add(methodLabel, 3, 2);
        if (task.getMethod().equals("Email")) {
            methodLabel.setText("Email (" + task.getEmail() + ")");
        }
        
        //Urgent?
        gridpane.add(new Label("Urgent:"), 2, 3);
        gridpane.add(new Label((task.isUrgent() ? "Yes" : "No")),3, 3);

        //Time
        gridpane.add(new Label("Created Time:"), 2, 4);
        gridpane.add(new Label(task.getCreateTime()), 3, 4);

        Scene scene = new Scene(gridpane, 600, 300);
        stage.setTitle("Detail");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
