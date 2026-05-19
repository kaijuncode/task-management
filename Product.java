public class Product {
    private String id;
    private String productName;

    public Product(String id, String productName){
        this.id = id;
        this.productName = productName;
    }

    public String getId(){
        return id;
    }
    
    public String getProductName(){
        return productName;
    }

    @Override
    public String toString(){
        return productName;
    }
}
