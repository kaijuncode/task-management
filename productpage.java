import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class productpage extends Application{
    @Override
    public void start(Stage stage){
        BorderPane productPane = new BorderPane();

        ListView<Product> productList = new ListView<>();
        productList.setSelectionModel(null);
        productList.setFocusTraversable(false);

        productList.setCellFactory(param -> new ListCell<Product>(){
            @Override
            protected void updateItem(Product product, boolean empty){
                super.updateItem(product, empty);

                if (empty || product == null){
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

                    Label productLabel = new Label(product.getProductName());
                    HBox productBox = new HBox();
                    productBox.setPrefWidth(80);
                    productBox.setAlignment(Pos.CENTER);
                    productBox.getChildren().addAll(productLabel);

                    card.add(productBox, 0, 0);

                }
            }

        });
        loadProduct(productList);

        productPane.setCenter(productList);
        Scene scene = new Scene(productPane, 300, 200);
        stage.setTitle("Product List");
        stage.setScene(scene);
        stage.show();
    }

    public void loadProduct(ListView<Product> productList){
        new Thread(()->{
            try{
                String projectId = "task-management-86056";
                String idToken = UserSession.getInstance().getidToken();

                String url = "https://firestore.googleapis.com/v1/projects/" + projectId + "/databases/(default)/documents/products";

                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + idToken)
                    .GET()
                    .build();

                HttpClient client = HttpClient.newHttpClient();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                List<Product> products = new ArrayList<>();

                if (response.statusCode() == 200){
                    JsonObject root = JsonParser.parseString(response.body()).getAsJsonObject();

                    if (root.has("documents")){
                        JsonArray documents = root.getAsJsonArray("documents");
                        
                        for (JsonElement doc : documents){
                            JsonObject fields = doc.getAsJsonObject().getAsJsonObject("fields");

                            String fullPath = doc.getAsJsonObject().get("name").getAsString();
                            String id = fullPath.substring(fullPath.lastIndexOf("/") + 1);
                            String productName = getField(fields, "name");

                            Product product = new Product(id, productName);
                            products.add(product);
                        }
                    }
                }
                Platform.runLater(()-> {
                    productList.getItems().setAll(products);
                });
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }).start();
    }

    private String getField(JsonObject fields, String key){
        if (fields.has(key)){
            return fields.getAsJsonObject(key).get("stringValue").getAsString();
        }
        return "";
    }
}