public abstract class Product {
    private final String productId;
    private final String productName;
    private int noOfItems;
    private double price;

    public Product(String productId, String productName, int noOfItems, double price) {
        this.productId = productId;
        this.productName = productName;
        this.noOfItems = noOfItems;
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getNoOfItems() {
        return noOfItems;
    }

    public double getPrice() {
        return price;
    }

    public void setNoOfItems(int noOfItems) {
        this.noOfItems = noOfItems;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public abstract String getProductType();

    public abstract String getInfo();

    @Override
    public String toString() {
        return "Product ID      " + productId + "\n" +
                "Name            " + productName + "\n" +
                "Category        " + getProductType() + "\n" +
                "Stock           " + noOfItems + "\n" +
                "Price(£)        " + price + "\n" +
                "Info            " + getInfo() + "\n\n";
    }
}
