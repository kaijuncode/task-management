import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.*;

public class productpage extends Application{
    private ListView<Product> productListRef;
    private Stage createStage;
    @Override
    public void start(Stage stage){
        BorderPane productPane = new BorderPane();

        //Center Area - Product List
        ListView<Product> productListing = new ListView<>();
        productListing.setSelectionModel(null);
        productListing.setFocusTraversable(false);
        productListRef = productListing;

        productListing.setCellFactory(param -> new ListCell<Product>(){
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
                    card.setAlignment(Pos.CENTER);

                    card.setStyle(
                        "-fx-background-color: #f7f7f7;"+
                        "-fx-background-radius: 8;"+
                        "-fx-border-color: black;"+
                        "-fx-border-radius: 8"
                    );

                    Label productLabel = new Label(product.getProductName());
                    HBox productBox = new HBox();
                    productBox.setAlignment(Pos.CENTER);
                    productBox.getChildren().addAll(productLabel);

                    card.add(productBox, 0, 0);
                    setGraphic(card);

                }
            }
        });
        loadProduct(productListing);

        //Bottom Area - Add New Product
        GridPane bottom = new GridPane();
        bottom.setPadding(new Insets(5));
        bottom.setAlignment(Pos.CENTER);

        Button addBtn = new Button("Add New Product");
        bottom.add(addBtn, 0, 0);

        addBtn.setOnAction(e-> {
            if (UserSession.getInstance().getName().equalsIgnoreCase("ADMIN")){
                createNewProduct();
            }
            else{
                Alert warning = new Alert(Alert.AlertType.WARNING);
                warning.setHeaderText("Access Denied");
                warning.setContentText("Need ADMIN-ACCESS.");
                warning.showAndWait();
            }
        });

        productPane.setCenter(productListing);
        productPane.setBottom(bottom);
        Scene scene = new Scene(productPane, 250, 200);
        stage.setTitle("Product List");
        stage.setScene(scene);
        stage.show();
    }

    public void loadProduct(ListView<Product> productList){
        AdministrationService as = new AdministrationService();
        new Thread(()->{
            try{
                List<Product> products = as.getProduct();
                Platform.runLater(()-> {
                    productList.getItems().setAll(products);
                });
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }).start();
    }

    public void createNewProduct(){
        AdministrationService as = new AdministrationService();
        createStage = new Stage();

        VBox createBox = new VBox(5);
        createBox.setAlignment(Pos.CENTER_LEFT);
        createBox.setPadding(new Insets(10));

        Label createLabel = new Label("Product Name:");
        TextField createText = new TextField();
        Button createBtn = new Button("Add");
        createBtn.setOnAction(e->{
            Alert createAlert = new Alert(Alert.AlertType.CONFIRMATION);
            createAlert.setHeaderText("Add New Product");
            createAlert.setContentText("Confirm Add New Product: '" + createText.getText() + "'?");
            Optional<ButtonType> result = createAlert.showAndWait();
            
            if (result.isPresent() && result.get() == ButtonType.OK){
                new Thread(()->{
                    try{
                        String newProduct = createText.getText().trim();
                        if (newProduct.isEmpty()){
                            Platform.runLater(()->{
                                Alert empty = new Alert(Alert.AlertType.WARNING);
                                empty.setHeaderText("Cannot Empty");
                                empty.setContentText("Pls enter the product you want to add!");
                                empty.showAndWait();
                            });
                            return;
                        }
                        if (as.productExists(newProduct)){
                            Platform.runLater(()->{
                                Alert empty = new Alert(Alert.AlertType.WARNING);
                                empty.setHeaderText("Product Exist");
                                empty.setContentText("This product already exist!");
                                empty.showAndWait();
                            });
                            return;
                        }
                        as.addProduct(newProduct);
                        Platform.runLater(()->{
                            createStage.close();
                            refreshProduct();
                        });
                    } catch(Exception ex){
                        ex.printStackTrace();
                    }
                }).start();
            }
        });
        createBox.getChildren().addAll(createLabel, createText, createBtn);

        Scene scene = new Scene(createBox, 250, 100);
        createStage.setTitle("Add Product");
        createStage.setScene(scene);
        createStage.show();
    }

    public void refreshProduct(){
        loadProduct(productListRef);
    }
}