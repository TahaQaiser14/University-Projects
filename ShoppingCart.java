import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.util.*;

public class ShoppingCart extends JFrame {
    static class MultiLineCellRenderer extends JPanel implements TableCellRenderer {
        private final JLabel[] labels;

        public MultiLineCellRenderer() {
            super(new GridLayout(3, 1));
            labels = new JLabel[3];
            for (int i = 0; i < 3; i++) {
                labels[i] = new JLabel();
                labels[i].setHorizontalAlignment(SwingConstants.CENTER);
                add(labels[i]);
            }
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setForeground(Color.BLACK);
            setBackground(Color.WHITE);

            setFont(table.getFont());
            setBorder(new EmptyBorder(1, 2, 1, 2));

            for (int i = 0; i < 3; i++) {
                labels[i].setText(((String[]) value)[i]);
            }
            return this;
        }
    }

    private final Map<Product, Integer> productList;
    private final JTable cartTable;
    private final JTextPane textPane;

    public ShoppingCart() {
        productList = new HashMap<>();

        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setTitle("Shopping Cart");
        setSize(700, 500);

        setLayout(new BorderLayout(10, 10));

        cartTable = new JTable();

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setVerticalAlignment(SwingConstants.CENTER);
        cellRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        cartTable.setDefaultRenderer(Object.class, cellRenderer);
        ((DefaultTableCellRenderer) cartTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        cartTable.getTableHeader().setFont(cartTable.getTableHeader().getFont().deriveFont(Font.BOLD));
        cartTable.getTableHeader().setEnabled(false);
        cartTable.setEnabled(false);
        cartTable.setShowGrid(true);
        cartTable.setRowHeight(60);
        add(new JScrollPane(cartTable), BorderLayout.CENTER);

        textPane = new JTextPane();
        textPane.setEditable(false);
        add(textPane, BorderLayout.SOUTH);
    }

    public void addProduct(Product product) {
        productList.put(product, 1 + productList.getOrDefault(product, 0));
    }

    public void removeProduct(Product product) {
        productList.remove(product);
    }

    public double calculateTotalCost() {
        double totalCost = 0.0;
        for (Product product : productList.keySet()) {
            totalCost += product.getPrice() * productList.get(product);
        }
        return totalCost;
    }

    public int productsCount() {
        return productList.size();
    }

    public void updateCartUI() {
        Vector<Vector<Object>> tableContent = new Vector<>();
        for (Product product : productList.keySet())
            tableContent.add(getObjectVector(product));

        Vector<String> columnNames = new Vector<>(Arrays.asList("Product", "Quantity", "Price"));

        cartTable.setModel(new DefaultTableModel(tableContent, columnNames));
        cartTable.getColumnModel().getColumn(0).setCellRenderer(new MultiLineCellRenderer());

        StyledDocument doc = new DefaultStyledDocument();
        textPane.setDocument(doc);

        int electronicsCount = 0;
        int clothingCount = 0;
        for (Product product : productList.keySet()) {
            if (product instanceof Electronics)
                electronicsCount += productList.get(product);
            else if (product instanceof Clothing)
                clothingCount += productList.get(product);
        }

        double total = calculateTotalCost();
        ClientWindow.addTextWithStyle(doc, String.format("%100s\t%.2f £\n\n", "Total", total), false);

        if (electronicsCount >= 3 || clothingCount >= 3) {
            double discount = total * 0.2;
            ClientWindow.addTextWithStyle(doc, String.format("%72s\t%.2f £\n", "Three Items in same Category Discount (20%)", -discount), false);

            double finalTotal = total - discount;
            ClientWindow.addTextWithStyle(doc, String.format("%97s\t%.2f £\n", "Final Total", finalTotal), true);
        }
    }

    private Vector<Object> getObjectVector(Product product) {
        Vector<Object> row = new Vector<>();
        row.add(new String[]{product.getProductId(), product.getProductName(), product.getInfo()});
        row.add(productList.get(product));
        row.add(product.getPrice() + " £");
        return row;
    }
}
