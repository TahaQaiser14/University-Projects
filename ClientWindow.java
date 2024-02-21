import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Objects;
import java.util.Vector;

public class ClientWindow extends JFrame {
    public ClientWindow(ShoppingManager manager) throws HeadlessException {
        ShoppingCart cart = new ShoppingCart();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                cart.dispose();
            }
        });

        setLayout(new BorderLayout(10, 10));

        JPanel panel1 = new JPanel(new BorderLayout(10, 10));
        add(panel1, BorderLayout.NORTH);

        JPanel panel2 = new JPanel(new FlowLayout());
        panel1.add(panel2, BorderLayout.CENTER);

        panel2.add(new JLabel("Select Product Category"));
        JComboBox<String> categorySelector = new JComboBox<>(new String[]{"All", "Electronics", "Clothing"});
        panel2.add(categorySelector);

        JButton shoppingCart = new JButton("Shopping Cart");
        panel1.add(shoppingCart, BorderLayout.EAST);

        JPanel panel3 = new JPanel(new BorderLayout(10, 10));
        add(panel3, BorderLayout.CENTER);

        Vector<Vector<Object>> tableContent = new Vector<>();
        for (Product product : manager.getProductList())
            tableContent.add(getObjectVector(product));

        Vector<String> columnNames = new Vector<>(Arrays.asList("Product ID", "Name", "Category", "Price(£)", "Info"));
        DefaultTableModel model = new DefaultTableModel(tableContent, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable productsTable = new JTable(model);
        panel3.add(new JScrollPane(productsTable), BorderLayout.CENTER);

        JTextPane productDetails = new JTextPane();
        productDetails.setEditable(false);
        productDetails.setSize(productDetails.getWidth(), 100);
        panel3.add(productDetails, BorderLayout.SOUTH);

        updateProductDetails(manager, productsTable, productDetails);

        JPanel panel4 = new JPanel(new FlowLayout());
        add(panel4, BorderLayout.SOUTH);

        JButton addToShoppingCart = new JButton("Add to Shopping Cart");
        panel4.add(addToShoppingCart);

        shoppingCart.addActionListener(e -> {
            cart.updateCartUI();
            cart.setVisible(true);
            cart.requestFocus();
        });
        shoppingCart.setBorder(new EmptyBorder(10, 10, 10, 10));

        addToShoppingCart.addActionListener(e -> {
            if (productsTable.getSelectedRowCount() == 1) {
                String productId = (String) productsTable.getValueAt(productsTable.getSelectedRow(), 0);
                Product product = manager.getProductById(productId);

                if (manager.deleteOneItem(product)) {
                    cart.addProduct(product);

                    shoppingCart.setText("Shopping Cart (" + cart.productsCount() + ")");
                    updateProductDetails(manager, productsTable, productDetails);
                    cart.updateCartUI();
                } else
                    JOptionPane.showMessageDialog(this, "Selected product is no more available!", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No product selected!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        productsTable.setRowHeight(25);
        ((DefaultTableCellRenderer) productsTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        productsTable.getTableHeader().setFont(productsTable.getTableHeader().getFont().deriveFont(Font.BOLD));
        productsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component renderer = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                String productId = (String) table.getValueAt(row, 0);
                Product product = manager.getProductById(productId);
                if (product.getNoOfItems() < 3) {
                    renderer.setBackground(Color.RED);
                } else {
                    renderer.setBackground(Color.LIGHT_GRAY);
                }

                if (isSelected) {
                    renderer.setBackground(renderer.getBackground().darker());
                }

                return renderer;
            }

            @Override
            public int getHorizontalAlignment() {
                return JLabel.CENTER;
            }
        });
        productsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productsTable.setAutoCreateRowSorter(true);
        productsTable.getSelectionModel().addListSelectionListener(e -> updateProductDetails(manager, productsTable, productDetails));

        categorySelector.addActionListener(e -> {
            Vector<Vector<Object>> dataVector = new Vector<>();
            String command = (String) ((JComboBox<?>) e.getSource()).getSelectedItem();
            switch (Objects.requireNonNull(command)) {
                case "All" -> {
                    for (Product product : manager.getProductList())
                        dataVector.add(getObjectVector(product));
                }
                case "Electronics" -> {
                    for (Product product : manager.getProductList())
                        if (product.getProductType().equals("Electronics"))
                            dataVector.add(getObjectVector(product));
                }
                case "Clothing" -> {
                    for (Product product : manager.getProductList())
                        if (product.getProductType().equals("Clothing"))
                            dataVector.add(getObjectVector(product));
                }
            }

            model.setDataVector(dataVector, columnNames);
            updateProductDetails(manager, productsTable, productDetails);
        });
    }

    private void updateProductDetails(ShoppingManager manager, JTable productsTable, JTextPane productDetails) {
        StyledDocument doc = new DefaultStyledDocument();
        addTextWithStyle(doc, "Selected Product - Details\n\n", true);

        if (productsTable.getSelectedRowCount() == 1) {
            String productId = (String) productsTable.getValueAt(productsTable.getSelectedRow(), 0);
            Product product = manager.getProductById(productId);

            addTextWithStyle(doc, "Product ID: " + product.getProductId() + "\n", false);
            addTextWithStyle(doc, "Category: " + product.getProductType() + "\n", false);
            addTextWithStyle(doc, "Name: " + product.getProductName() + "\n", false);

            if (product instanceof Clothing clothing) {
                addTextWithStyle(doc, "Size: " + clothing.getSize() + "\n", false);
                addTextWithStyle(doc, "Colour: " + clothing.getColour() + "\n", false);
            } else if (product instanceof Electronics electronics) {
                addTextWithStyle(doc, "Brand: " + electronics.getBrand() + "\n", false);
                addTextWithStyle(doc, "Warranty: " + electronics.getWarranty() + "\n", false);
            }

            addTextWithStyle(doc, "Items Available: " + product.getNoOfItems() + "\n", false);
        } else {
            addTextWithStyle(doc, "Product ID: \n", false);
            addTextWithStyle(doc, "Category: \n", false);
            addTextWithStyle(doc, "Name: \n", false);
            addTextWithStyle(doc, "\n\n", false);
            addTextWithStyle(doc, "Items Available: \n", false);
        }

        productDetails.setDocument(doc);
    }

    public static void addTextWithStyle(StyledDocument doc, String text, boolean bold) {
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setBold(style, bold);
        try {
            doc.insertString(doc.getLength(), text, style);
        } catch (BadLocationException ignored) {
        }
    }

    private static Vector<Object> getObjectVector(Product product) {
        Vector<Object> row = new Vector<>();
        row.add(product.getProductId());
        row.add(product.getProductName());
        row.add(product.getProductType());
        row.add(product.getPrice());
        row.add(product.getInfo());
        return row;
    }
}
