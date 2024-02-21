import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class WestminsterShoppingManager implements ShoppingManager {
    private static final int MAX_PRODUCTS = 50;
    private static final Scanner scanner = new Scanner(System.in);
    private final List<Product> productList;

    public WestminsterShoppingManager() {
        productList = loadProducts();
    }

    private void console() {
        System.out.println("Welcome to Westminster Shopping Manager!\n");

        do {
            displayMainMenu();

            String option = scanner.nextLine();

            switch (option) {
                case "0" -> userInterface();
                case "1" -> addProduct();
                case "2" -> deleteProduct();
                case "3" -> printProductList();
                case "4" -> saveProducts(productList);
                case "5" -> Exit();
                default -> System.out.println("Entered a wrong option. Enter again...");
            }
        } while (true);
    }

    private void Exit() {
        System.out.print("\nAny unsaved data will be lost. Do you really want to exit? (Y/n) ");
        if (scanner.nextLine().equalsIgnoreCase("Y")) {
            System.out.println("\nProgram is exiting successfully...");
            System.exit(0);
        }
    }

    private void displayMainMenu() {
        System.out.println("*----------------------------------*");
        System.out.println("\t0. User Interface");
        System.out.println("\t1. Add new product");
        System.out.println("\t2. Delete product");
        System.out.println("\t3. Print products list");
        System.out.println("\t4. Save");
        System.out.println("\t5. Exit");
        System.out.println("*----------------------------------*");
        System.out.print("Enter your option: ");
    }

    public void addProduct() {
        if (productList.size() == MAX_PRODUCTS) {
            System.out.println("System has reached it's maximum limit of products");
            return;
        }

        System.out.println("ADD PRODUCT");
        System.out.print("Product ID: ");
        String productId = scanner.nextLine();
        System.out.print("Product Name: ");
        String productName = scanner.nextLine();
        System.out.print("No of Items: ");
        int noOfItems = Integer.parseInt(scanner.nextLine());
        System.out.print("Price(£): ");
        double price = Double.parseDouble(scanner.nextLine());

        System.out.println("\nCATEGORY");
        System.out.println("1. Clothing");
        System.out.println("2. Electronics");
        String choice;

        do {
            System.out.print("Choice: ");
            choice = scanner.nextLine();

            switch (choice) {
                case "1" -> {
                    System.out.println("CLOTHING");
                    System.out.print("Size: ");
                    String size = scanner.nextLine();
                    System.out.print("Colour: ");
                    String color = scanner.nextLine();

                    productList.add(new Clothing(productId, productName, noOfItems, price, size, color));
                }
                case "2" -> {
                    System.out.println("ELECTRONICS");
                    System.out.print("Brand: ");
                    String brand = scanner.nextLine();
                    System.out.print("Warranty: ");
                    String warranty = scanner.nextLine();

                    productList.add(new Electronics(productId, productName, noOfItems, price, brand, warranty));
                }
                default -> System.out.println("Invalid option selected! Enter again.");
            }
        } while (!choice.equals("1") && !choice.equals("2"));

        System.out.println("Product added successfully...");
    }

    public Product getProductById(String productId) {
        for (Product product : productList) {
            if (product.getProductId().equals(productId))
                return product;
        }
        return null;
    }

    public void deleteProduct() {
        System.out.println("ADD PRODUCT");
        System.out.print("Product ID: ");
        String productId = scanner.nextLine();

        Product requiredProduct = getProductById(productId);

        if (requiredProduct == null) {
            System.out.println("Product not found.");
            return;
        }

        System.out.println(requiredProduct);

        productList.remove(requiredProduct);
        System.out.println("Product removed successfully...");

        System.out.println("Products Left: " + productList.size());
    }

    public void printProductList() {
        productList.sort(Comparator.comparing(Product::getProductId));
        for (Product product : productList) {
            System.out.println(product);
        }
    }

    public void save() {
        saveProducts(productList);
    }

    public static void saveProducts(List<Product> products) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("shop.dat"))) {
            for (Product product : products) {
                writer.print(product.getProductId() + "," + product.getProductName()
                        + "," + product.getNoOfItems() + "," + product.getPrice() + ",");
                if (product instanceof Electronics electronics)
                    writer.println("Electronics," + electronics.getBrand() + "," + electronics.getWarranty());
                else if (product instanceof Clothing clothing)
                    writer.println("Clothing," + clothing.getSize() + "," + clothing.getColour());
            }
            System.out.println("Products saved successfully...");
        } catch (IOException e) {
            System.out.println("Error: While saving products");
            System.out.println("Message: " + e.getMessage());
        }
    }

    public static List<Product> loadProducts() {
        List<Product> products = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("shop.dat"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data[4].equals("Electronics")) {
                    products.add(new Electronics(data[0], data[1], Integer.parseInt(data[2]), Double.parseDouble(data[3]),
                            data[5], data[6]));
                } else if (data[4].equals("Clothing")) {
                    products.add(new Clothing(data[0], data[1], Integer.parseInt(data[2]), Double.parseDouble(data[3]),
                            data[5], data[6]));
                }
            }
        } catch (IOException ignored) {
        }
        return products;
    }

    private void userInterface() {
        CountDownLatch latch = new CountDownLatch(1);

        SwingUtilities.invokeLater(() -> {

            try {
                UIManager.setLookAndFeel(new NimbusLookAndFeel());
            } catch (UnsupportedLookAndFeelException e) {
                throw new RuntimeException(e);
            }
            ClientWindow client = new ClientWindow(this);
            client.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            client.setTitle("Westminster Shopping Centre");
            client.setSize(800, 600);

            client.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent windowEvent) {
                    latch.countDown();
                }
            });

            client.setVisible(true);
            client.requestFocus();
        });

        try {
            System.out.println("Waiting for client window to close...");
            latch.await(); // Waits until the window is closed
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public void addOneItem(Product product) {
        product.setNoOfItems(product.getNoOfItems() + 1);
    }

    @Override
    public boolean deleteOneItem(Product product) {
        if (product.getNoOfItems() == 0)
            return false;
        product.setNoOfItems(product.getNoOfItems() - 1);
        return true;
    }

    @Override
    public List<Product> getProductList() {
        return productList;
    }

    public static void main(String[] args) {
        WestminsterShoppingManager manager = new WestminsterShoppingManager();
        manager.console();
    }
}