public class Electronics extends Product {
    private String brand;
    private String warranty;

    public Electronics(String productId, String productName, int noOfItems, double price, String brand, String warranty) {
        super(productId, productName, noOfItems, price);
        this.brand = brand;
        this.warranty = warranty;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setWarranty(String warranty) {
        this.warranty = warranty;
    }

    public String getBrand() {
        return brand;
    }

    public String getWarranty() {
        return warranty;
    }

    @Override
    public String getProductType() {
        return "Electronics";
    }

    @Override
    public String getInfo() {
        return brand + ", " + warranty + " warranty";
    }
}
