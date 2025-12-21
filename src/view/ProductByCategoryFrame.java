package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.util.List;

import dao.CategoryDAO;
import dao.ProductDAO;
import model.Category;
import model.Product;

public class ProductByCategoryFrame extends JFrame {

    private JTable tblCategory, tblProduct;
    private DefaultTableModel modelCategory, modelProduct;

    private CategoryDAO categoryDAO = new CategoryDAO();
    private ProductDAO productDAO = new ProductDAO();

    public ProductByCategoryFrame() {
        setTitle("Sản phẩm theo danh mục");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);

        initUI();
        loadCategories();
    }

    private void initUI() {

        JLabel lblCat = new JLabel("Danh mục");
        lblCat.setBounds(20, 10, 100, 25);
        add(lblCat);

        modelCategory = new DefaultTableModel(
                new Object[]{"ID", "Tên danh mục"}, 0);
        tblCategory = new JTable(modelCategory);
        JScrollPane spCat = new JScrollPane(tblCategory);
        spCat.setBounds(20, 40, 250, 400);
        add(spCat);

        JLabel lblPro = new JLabel("Sản phẩm");
        lblPro.setBounds(300, 10, 100, 25);
        add(lblPro);

        modelProduct = new DefaultTableModel(
                new Object[]{"ID", "Tên", "Giá", "Tồn kho"}, 0);
        tblProduct = new JTable(modelProduct);
        JScrollPane spPro = new JScrollPane(tblProduct);
        spPro.setBounds(300, 40, 460, 400);
        add(spPro);

        // ✅ Click danh mục → load sản phẩm
        tblCategory.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblCategory.getSelectedRow();
                if (row >= 0) {
                    int catId = (int) modelCategory.getValueAt(row, 0);
                    loadProductsByCategory(catId);
                }
            }
        });
    }

    // Load danh mục
    private void loadCategories() {
        modelCategory.setRowCount(0);
        List<Category> list = categoryDAO.getAll();
        for (Category c : list) {
            modelCategory.addRow(new Object[]{
                    c.getId(),
                    c.getName()
            });
        }
    }

    // Load sản phẩm theo danh mục
    private void loadProductsByCategory(int catId) {
        modelProduct.setRowCount(0);
        List<Product> list = productDAO.getByCategory(catId);

        for (Product p : list) {
            modelProduct.addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    p.getPrice(),
                    p.getStock()
            });
        }
    }
}
