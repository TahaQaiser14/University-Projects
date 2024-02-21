public class Clothing extends Product {
    private String size;
    private String colour;

    public Clothing(String productId, String productName, int noOfItems, double price, String size, String colour) {
        super(productId, productName, noOfItems, price);
        this.size = size;
        this.colour = colour;
    }

    public void setSize(String size){
        this.size = size;
    }

    public void setColour(String colour){
        this.colour = colour;
    }

    public String getSize(){
        return size;
    }

    public String getColour(){
        return colour;
    }

    @Override
    public String getProductType() {
        return "Clothing";
    }

    @Override
    public String getInfo() {
        return size + ", " + colour;
    }
}
