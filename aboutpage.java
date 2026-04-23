import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class aboutpage extends Application{
    @Override
    public void start(Stage stage){
        GridPane aboutpage = new GridPane();
        aboutpage.setHgap(10);
        aboutpage.setVgap(10);
        aboutpage.setPadding(new Insets(10));

        aboutpage.add(new Label("HIHIHIHI"), 0, 0);

        Scene scene = new Scene(aboutpage, 400, 200);
        stage.setTitle("About");
        stage.setScene(scene);
        stage.show();
    }
    
    public static void main(String[] args) {
        launch();
    }
}
