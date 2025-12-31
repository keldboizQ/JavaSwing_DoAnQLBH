package view;

import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.awt.Image;

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

    private JLabel lblImage;

    private ProductDAO productDAO = new ProductDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();
    private BrandDAO brandDAO = new BrandDAO();

    private NumberFormat vnFormat =
            NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public SearchFrame() {
        setTitle("Tra cứu sản phẩm");
        setSize(900, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);

        initUI();
        loadCombo();
        search();
    }

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
        scroll.setBounds(20, 150, 520, 240);

        lblImage = new JLabel("Không có ảnh", SwingConstants.CENTER);
        lblImage.setBorder(BorderFactory.createEtchedBorder());
        lblImage.setBounds(560, 150, 300, 240);

        add(lblKey); add(txtKeyword);
        add(lblCat); add(cboCategory);
        add(lblBrand); add(cboBrand);
        add(btnSearch);
        add(scroll);
        add(lblImage);

        btnSearch.addActionListener(e -> search());

        table.getSelectionModel().addListSelectionListener(e -> showImage());
    }

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

    private void search() {

        tableModel.setRowCount(0);
        lblImage.setIcon(null);
        lblImage.setText("Không có ảnh");

        String keyword = txtKeyword.getText();
        Category c = (Category) cboCategory.getSelectedItem();
        Brand b = (Brand) cboBrand.getSelectedItem();

        Integer categoryId = (c == null) ? null : c.getId();
        Integer brandId = (b == null) ? null : b.getId();

        List<Product> list = productDAO.search(keyword, categoryId, brandId);

        for (Product p : list) {
            tableModel.addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    vnFormat.format(p.getPrice()),
                    p.getStock()
            });
        }
    }

    private void showImage() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        int id = (int) tableModel.getValueAt(row, 0);

        Product p = productDAO.getAll().stream()
                .filter(x -> x.getId() == id)
                .findFirst().orElse(null);

        if (p == null || p.getImagePath() == null || p.getImagePath().isEmpty()) {
            lblImage.setIcon(null);
            lblImage.setText("Không có ảnh");
            return;
        }

        try {
            ImageIcon icon = new ImageIcon(p.getImagePath());
            Image img = icon.getImage().getScaledInstance(
                    lblImage.getWidth(),
                    lblImage.getHeight(),
                    Image.SCALE_SMOOTH
            );
            lblImage.setText("");
            lblImage.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            lblImage.setText("Không load được ảnh");
            lblImage.setIcon(null);
        }
    }
}
