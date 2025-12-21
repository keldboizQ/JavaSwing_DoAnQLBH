package view;

import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

import dao.ProductDAO;
import dao.CategoryDAO;
import dao.BrandDAO;
import model.Product;
import model.Category;
import model.Brand;

public class SearchFrame extends JFrame {

    private JTextField txtKeyword;
    private JComboBox<Category> cboCategory;
    private JComboBox<Brand> cboBrand;
    private JTable table;
    private DefaultTableModel tableModel;

    private ProductDAO productDAO = new ProductDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();
    private BrandDAO brandDAO = new BrandDAO();
    
    private NumberFormat vnFormat =
            NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));


    public SearchFrame() {
        setTitle("Tra cứu sản phẩm");
        setSize(700, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);

        initUI();
        loadCombo();
        search();
    }

    // ================= UI =================
    private void initUI() {

        JLabel lblKey = new JLabel("Tên SP:");
        JLabel lblCat = new JLabel("Danh mục:");
        JLabel lblBrand = new JLabel("Thương hiệu:");

        lblKey.setBounds(20, 20, 80, 25);
        lblCat.setBounds(20, 60, 80, 25);
        lblBrand.setBounds(20, 100, 80, 25);

        txtKeyword = new JTextField();
        txtKeyword.setBounds(110, 20, 200, 25);

        cboCategory = new JComboBox<>();
        cboCategory.setBounds(110, 60, 200, 25);

        cboBrand = new JComboBox<>();
        cboBrand.setBounds(110, 100, 200, 25);

        JButton btnSearch = new JButton("Tìm");
        btnSearch.setBounds(330, 20, 100, 30);

        tableModel = new DefaultTableModel(
            new Object[]{"ID", "Tên", "Giá", "Tồn kho"}, 0
        );
        table = new JTable(tableModel);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(20, 150, 650, 240);

        add(lblKey); add(txtKeyword);
        add(lblCat); add(cboCategory);
        add(lblBrand); add(cboBrand);
        add(btnSearch);
        add(scroll);

        btnSearch.addActionListener(e -> search());
    }

    // ================= LOAD COMBO =================
    private void loadCombo() {

        cboCategory.addItem(null);
        for (Category c : categoryDAO.getAll()) {
            cboCategory.addItem(c);
        }

        cboBrand.addItem(null);
        for (Brand b : brandDAO.getAll()) {
            cboBrand.addItem(b);
        }
    }

    // ================= SEARCH =================
    private void search() {

        tableModel.setRowCount(0);

        String keyword = txtKeyword.getText();
        Category c = (Category) cboCategory.getSelectedItem();
        Brand b = (Brand) cboBrand.getSelectedItem();

        Integer categoryId = (c == null) ? null : c.getId();
        Integer brandId = (b == null) ? null : b.getId();

        List<Product> list = productDAO.search(
                keyword, categoryId, brandId
        );

        for (Product p : list) {
            tableModel.addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    vnFormat.format(p.getPrice()),
                    p.getStock()
            });
        }
    }
}
