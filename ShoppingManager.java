import java.util.List;

public interface ShoppingManager {
    Product getProductById(String productId);

    void addOneItem(Product product);

    boolean deleteOneItem(Product product);

    List<Product> getProductList();
}
