import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import javafx.util.Duration;
import javafx.beans.binding.Bindings;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.Window;

public class mainpage extends Application{ 
    private String lastUpdatedCache = "";
    private Filtermode currentMode = Filtermode.ALL;
    private Stage postStage;
    private Stage userStatusStage;
    private Stage userRoleStage;
    private Stage aboutStage;
    private Stage updateStatusStage;
    private Stage productStage;
    TaskService ts = new TaskService();

    enum Filtermode{
        ALL,
        MY_TASK,
        COMPLETE
    };
    @Override
    public void start(Stage stage1){
        BorderPane mainpage = new BorderPane();

        //Menu
        //Task Menu Item
        MenuItem post = new MenuItem("Post Task");

        //Administrator Menu Item
        MenuItem status = new MenuItem("User Status");
        MenuItem role = new MenuItem("User Role");
        MenuItem product = new MenuItem("Product List");

        //Help Menu Item
        MenuItem about = new MenuItem("About");
        MenuItem logout = new MenuItem("Logout");
        MenuItem exit = new MenuItem("Exit");

        Menu taskMenu = new Menu("Task");
        taskMenu.getItems().addAll(post);
        Menu adminMenu = new Menu("Administrator Tool");
        adminMenu.getItems().addAll(status, role, product);
        Menu helpMenu = new Menu("Help");
        helpMenu.getItems().addAll(about,logout,exit);

        MenuBar menu = new MenuBar();
        menu.getMenus().addAll(taskMenu, adminMenu, helpMenu);

        //Post Task Page
        posttaskpage posttaskPage = new posttaskpage();
        post.setOnAction(e ->{
            if (postStage == null || !postStage.isShowing()){
                postStage = new Stage();
                posttaskPage.start(postStage);
            }
            else{
                postStage.setIconified(false);
                postStage.show();
                postStage.toFront();
                postStage.requestFocus();
            }
        });

        //User Status Page
        userstatuspage userstatusPage = new userstatuspage();
        status.setOnAction(e-> {
            if (userStatusStage == null || !userStatusStage.isShowing()){
                userStatusStage = new Stage();
                userstatusPage.start(userStatusStage);
            }
            else{
                userStatusStage.setIconified(false);
                userStatusStage.show();
                userStatusStage.toFront();
                userStatusStage.requestFocus();
            }
        });

        //User Role Page
        userrolepage userrolePage = new userrolepage();
        role.setOnAction(e-> {
            if (userRoleStage == null || !userRoleStage.isShowing()){
                userRoleStage = new Stage();
                userrolePage.start(userRoleStage);
            }
            else{
                userRoleStage.setIconified(false);
                userRoleStage.show();
                userRoleStage.toFront();
                userRoleStage.requestFocus();
            }
        });

        productpage productPage = new productpage();
        product.setOnAction(e-> {
            if (productStage == null || !productStage.isShowing()){
                productStage = new Stage();
                productPage.start(productStage);
            }
            else{
                productStage.setIconified(false);
                productStage.show();
                productStage.toFront();
                productStage.requestFocus();
            }
        });

        //About Page
        aboutpage aboutPage = new aboutpage(); 
        about.setOnAction(e ->{
            if (aboutStage == null || !aboutStage.isShowing()){
                aboutStage = new Stage();
                aboutPage.start(aboutStage);
            }
            else{
                aboutStage.setIconified(false);
                aboutStage.show();
                aboutStage.toFront();
                aboutStage.requestFocus();
            }
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
                List<Window> windows = new ArrayList<>(Window.getWindows());
                for (Window window : windows){
                    if (window instanceof Stage){
                        ((Stage) window).close();
                    }
                }
                UserSession.getInstance().clear();
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
        stage1.setOnCloseRequest(e-> {
            e.consume();

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
        ListView<Task> table = new ListView<>();
        table.setSelectionModel(null);
        table.setFocusTraversable(false);

        table.setCellFactory(param -> new ListCell<Task>(){
            @Override
            protected void updateItem(Task task, boolean empty){
                super.updateItem(task, empty);

                if (empty || task == null){
                    setGraphic(null);
                    return;
                } 
                
                else {
                    GridPane card = new GridPane();
                    card.setHgap(10);
                    card.setVgap(5);
                    card.setPadding(new Insets(10));

                    card.setStyle(
                        "-fx-background-color: #f5f5f5;" +
                        "-fx-border-color: #ccc;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;"
                    );

                    Label CompanyName = new Label("Company Name: " + task.getCompanyName());
                    Label CustomerName = new Label("Name: " + task.getCustomerName());
                    Label ContactNumber = new Label("Contact Number: " + task.getContactNumber());
                    Label Software = new Label("Software: " + task.getSoftware());
                    Label Issue = new Label("Issue: " + task.getIssue());
                    Label taskProgress = new Label("Ticket Status: " + task.getProgress());
                    Label Time = new Label(task.getCreateTime());
                    Time.setStyle(
                        "-fx-font-weight: bold;"
                    );
                    //The Person in Charge or Pending
                    Label pic = new Label("Ticket PIC");
                    Label taskStatus = new Label("");
                    taskStatus.setAlignment(Pos.CENTER);
                    if (!task.getAssignedTo().equals("Everyone")) {
                        taskStatus.setText(task.getAssignedTo());
                    }
                    else{
                        taskStatus.setTextFill(Color.RED);
                        taskStatus.setText("Pending");
                    }
                    
                    VBox cardBox1 = new VBox(3);
                    cardBox1.setPrefWidth(170);
                    cardBox1.getChildren().addAll(Time, CustomerName, ContactNumber);
                    VBox cardBox2 = new VBox(3);
                    cardBox2.setPrefWidth(225);
                    cardBox2.getChildren().addAll(CompanyName, Software, Issue);
                    VBox cardBox3 = new VBox(3);
                    cardBox3.setPrefWidth(130);
                    cardBox3.setAlignment(Pos.CENTER);
                    cardBox3.getChildren().addAll(taskProgress, pic, taskStatus);

                    //Urgent - Red Background
                    if (task.isUrgent()){
                        card.setStyle(
                        "-fx-background-color: #ffebee;" +
                        "-fx-border-color: #ccc;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;"
                        );
                    }

                    //Complete - Grey Background
                    if (task.getStatus().equalsIgnoreCase("Complete")){
                        card.setStyle(
                        "-fx-background-color: #90EE90;" +
                        "-fx-border-color: #ccc;" +
                        "-fx-border-radius: 8;" +
                        "-fx-background-radius: 8;"
                        );
                    }

                    card.add(cardBox1, 0, 0);
                    card.add(cardBox2, 1, 0);
                    card.add(cardBox3, 2, 0);
                    setGraphic(card);

                    //See Task Detail
                    setOnMouseClicked(e-> {
                        new detailpage(task).start(new Stage());
                    });
                }
            }
        });

        loadTasks(table, currentMode);
        //Refresh Task List
        Timeline refresh = new Timeline(
        new KeyFrame(Duration.seconds(5), e -> {
            try{
                String lastest = ts.getLastUpdated();

                if (!lastest.equals(lastUpdatedCache)){
                    lastUpdatedCache = lastest;

                    Platform.runLater(() ->{
                        loadTasks(table, currentMode);
                    });
                }
            } catch (Exception ex){
                ex.printStackTrace();
            }
            })
        );
        refresh.setCycleCount(Timeline.INDEFINITE);
        refresh.play();

        //Right Area
        GridPane right = new GridPane();
        VBox rightBox = new VBox();
        rightBox.setSpacing(10);
        rightBox.setAlignment(Pos.CENTER);
        rightBox.setPrefWidth(100);

        Label dateLabel = new Label(LocalDate.now().toString());

        //Clock
        Label clockLabel = new Label();
        clockLabel.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: black;" +
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 12px;"
        );
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        final boolean[] showColon = {true};
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(1), e-> {
                LocalTime now = LocalTime.now();
                String time;
                if (showColon[0]){
                    time = now.format(formatter);
                }
                else{
                    time = now.format(DateTimeFormatter.ofPattern("HH mm ss"));
                }
                clockLabel.setText(time);
                showColon[0] = !showColon[0];
            })
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        //UserName
        Label nameLabel = new Label();
        Label roleLabel = new Label();
        nameLabel.setAlignment(Pos.CENTER);
        new Thread(() ->{
            try{
                String name = UserSession.getInstance().getName();
                String userRole = UserSession.getInstance().getRole();

                Platform.runLater(() ->{
                    nameLabel.setText(name);
                    roleLabel.setText(userRole);
                });
            } catch (Exception e){
                nameLabel.setText("Error");
            }
        }).start();

        //Status
        Label statusLabel = new Label("Loading...");
        statusLabel.setTextFill(Color.GREY);
        statusLabel.setAlignment(Pos.CENTER);

        //Status Refresh
        Timeline statusRefresh = new Timeline(
            new KeyFrame(Duration.seconds(5), e-> {
                new Thread(() -> {
                    try{
                        UserSession session = UserSession.getInstance();
                        ProfileService ps = new ProfileService();

                        String userStatus = ps.getProfileStatus(session.getUid(), session.getidToken());

                        Platform.runLater(()-> {
                            if (userStatus.equalsIgnoreCase("OnLeave")){
                                handleUpdateStatus("Available", stage1);
                            }
                            if (userStatus.equalsIgnoreCase("Available")){
                                statusLabel.setTextFill(Color.GREEN);
                            }
                            if (userStatus.equalsIgnoreCase("OnSite")){
                                statusLabel.setTextFill(Color.GREENYELLOW);
                            }
                            if (userStatus.equalsIgnoreCase("Block")){
                                statusLabel.setTextFill(Color.RED);
                            }
                            statusLabel.setText(userStatus);
                        });
                    } catch (Exception ex){
                        ex.printStackTrace();
                    }
                }).start();
            })
        );
        statusRefresh.setCycleCount(Timeline.INDEFINITE);
        statusRefresh.play();

        Button statusBtn = new Button("Update Status");
        statusBtn.setOnAction(e-> {
            showUpdateStatus();
        });

        nameLabel.setMaxWidth(rightBox.getPrefWidth());
        statusLabel.setMaxWidth(rightBox.getPrefWidth());
        statusBtn.setMaxWidth(rightBox.getPrefWidth());
        rightBox.getChildren().addAll(dateLabel, clockLabel, nameLabel, roleLabel, statusLabel, statusBtn);
        right.add(rightBox, 0, 0);

        //Bottom Area
        GridPane bottom = new GridPane();
        bottom.setHgap(10);
        bottom.setVgap(10);
        bottom.setAlignment(Pos.CENTER);

        //Case Calculation
        Label label = new Label("Pending Task Amount:");
        Label count = new Label();
        count.textProperty().bind(
            Bindings.size(table.getItems()).asString("%d")
        );
        label.setFont(new Font("Times New Roman", 16));
        count.setFont(new Font("Times New Roman", 16));
        bottom.add(label, 0, 0);
        bottom.add(count, 1, 0);

        //Left Area
        GridPane left = new GridPane();
        VBox btnBox = new VBox();
        btnBox.setSpacing(10);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.setPrefWidth(100);

        //Task Type Button
        Button pendingBtn = new Button("Pending Task");
        Button mytaskBtn = new Button("My Task");
        Button completeBtn = new Button("Complete Task");

        pendingBtn.setMaxWidth(btnBox.getPrefWidth());
        mytaskBtn.setMaxWidth(btnBox.getPrefWidth());
        completeBtn.setMaxWidth(btnBox.getPrefWidth());

        btnBox.getChildren().addAll(pendingBtn, mytaskBtn, completeBtn);
        left.add(btnBox, 0, 0);
        
        //Pending Task
        pendingBtn.setOnAction(e-> {
            label.setText("Pending Task Amount:");
            currentMode = Filtermode.ALL;
            loadTasks(table, currentMode);
        });

        //My Task
        mytaskBtn.setOnAction(e-> {
            label.setText("My Task Amount:");
            currentMode = Filtermode.MY_TASK;
            loadTasks(table, currentMode);
        });

        //Complete Task
        completeBtn.setOnAction(e-> {
            label.setText("Complete Task Amount:");
            currentMode = Filtermode.COMPLETE;
            loadTasks(table, currentMode);
        });

        mainpage.setTop(menu);
        mainpage.setCenter(table);
        mainpage.setRight(right);
        mainpage.setLeft(left);
        mainpage.setBottom(bottom);

        Scene scene = new Scene(mainpage, 800, 600);
        stage1.setTitle("Task Management System");
        stage1.setScene(scene);
        stage1.show();
    }

    //Load Task
    public void loadTasks(ListView<Task> table, Filtermode mode){
        new Thread(() ->{
            try{
                //Task Service - Get Task
                TaskService ts = new TaskService();
                List<Task> tasks = ts.getTasks();
                List<Task> filterTasks = new ArrayList<>();

                //Filter Task
                for (Task task : tasks){
                    //Filter Pending Task
                    if (mode == Filtermode.ALL){
                        if (task.getStatus().equalsIgnoreCase("COMPLETE")){
                            continue;
                        }
                    }
                    //Filter My Task
                    if (mode == Filtermode.MY_TASK){
                        if (!task.getAssignedTo().equals(UserSession.getInstance().getName())){
                            continue;
                        }
                    }
                    //Filter Complete Task
                    if (mode == Filtermode.COMPLETE){
                        if (!task.getStatus().equalsIgnoreCase("COMPLETE")){
                            continue;
                        }
                    }
                    filterTasks.add(task);
                }
                //Sort COMPLETE TASK
                if (mode == Filtermode.COMPLETE){
                    filterTasks.sort(Comparator.comparing(Task::getCreateDateTime));
                }
                //Sort MY TASK (Urgent->Pending->Complete)
                else if (mode == Filtermode.MY_TASK){
                    filterTasks.sort(Comparator.comparing((Task task) -> task.isUrgent()&&!task.getStatus().equalsIgnoreCase("Complete")).reversed().thenComparing(Task -> Task.getStatus().equalsIgnoreCase("Complete")).thenComparing(Task::getCreateDateTime));
                }
                //Sort PENDING TASK (Urgent->Pending)
                else{
                    filterTasks.sort(Comparator.comparing(Task::isUrgent).reversed().thenComparing(Task::getCreateDateTime));
                }
                Platform.runLater(() ->{
                    table.getItems().setAll(filterTasks);
                });
            } catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }

    //Update Status Window
    public void showUpdateStatus(){
        if (updateStatusStage != null && updateStatusStage.isShowing()){
            updateStatusStage.setIconified(false);
            updateStatusStage.toFront();
            updateStatusStage.requestFocus();
            return;
        }

        updateStatusStage = new Stage();

        updateStatusStage.setOnCloseRequest(e-> {
            updateStatusStage = null;
        });

        HBox btnBox = new HBox(10);
        btnBox.setAlignment(Pos.CENTER);

        Button avaBtn = new Button("Available");
        Button siteBtn = new Button("OnSite");
        Button blockBtn = new Button("Block");

        btnBox.getChildren().addAll(avaBtn, siteBtn, blockBtn);

        String detectStatus = UserSession.getInstance().getStatus();
        if (detectStatus.equals("Available")){
            avaBtn.setDisable(true);
        }
        if (detectStatus.equals("OnSite")){
            siteBtn.setDisable(true);
        }
        if (detectStatus.equals("Block")){
            blockBtn.setDisable(true);
        }

        avaBtn.setOnAction(e-> {handleUpdateStatus("Available", updateStatusStage);});
        siteBtn.setOnAction(e-> {handleUpdateStatus("OnSite", updateStatusStage);});
        blockBtn.setOnAction(e-> {handleUpdateStatus("Block", updateStatusStage);});

        Scene scene = new Scene(btnBox, 300, 200);
        updateStatusStage.setTitle("Update Status");
        updateStatusStage.setScene(scene);
        updateStatusStage.show();
    }
    public void handleUpdateStatus(String newStatus, Stage stage){
        new Thread(() ->{
            try{
                UserSession session = UserSession.getInstance();
                ProfileService ps = new ProfileService();

                ps.updateStatus(
                    session.getUid(),
                    session.getidToken(),
                    newStatus
                );

                session.setStatus(newStatus);

                Platform.runLater(() ->{
                    stage.close();
                });
            } catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }
}
