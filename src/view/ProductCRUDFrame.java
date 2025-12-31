package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.NumberFormat;
import java.util.Locale;

import dao.*;
import model.*;
import util.ImageUtil;

public class ProductCRUDFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    private JTextField txtId, txtName, txtPrice, txtStock;
    private JTextArea txtDesc;
    private JComboBox<Category> cboCategory;
    private JComboBox<Brand> cboBrand;

    private JLabel lblImage;
    private String imagePath;

    private ProductDAO productDAO = new ProductDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();
    private BrandDAO brandDAO = new BrandDAO();

    private NumberFormat vnd =
            NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public ProductCRUDFrame() {
        setTitle("Quản lý sản phẩm");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);

        initUI();
        loadCategory();
        loadBrand();
        loadTable();
    }

    // ================= UI =================
    private void initUI() {

        int x = 20, y = 20;

        addLabel("ID:", x, y);
        txtId = addText(x + 100, y, 120, false);

        addLabel("Tên:", x, y += 35);
        txtName = addText(x + 100, y, 250, true);

        addLabel("Giá:", x, y += 35);
        txtPrice = addText(x + 100, y, 150, true);

        addLabel("Tồn kho:", x, y += 35);
        txtStock = addText(x + 100, y, 150, true);

        addLabel("Danh mục:", x, y += 35);
        cboCategory = new JComboBox<>();
        cboCategory.setBounds(x + 100, y, 200, 25);
        add(cboCategory);

        addLabel("Hãng:", x, y += 35);
        cboBrand = new JComboBox<>();
        cboBrand.setBounds(x + 100, y, 200, 25);
        add(cboBrand);

        addLabel("Mô tả:", x, y += 35);
        txtDesc = new JTextArea();
        JScrollPane spDesc = new JScrollPane(txtDesc);
        spDesc.setBounds(x + 100, y, 300, 70);
        add(spDesc);

        // ===== IMAGE =====
        lblImage = new JLabel();
        lblImage.setBounds(20, 320, 250, 180);
        lblImage.setBorder(BorderFactory.createTitledBorder("Ảnh sản phẩm"));
        add(lblImage);

        JButton btnChooseImage = new JButton("Chọn ảnh");
        btnChooseImage.setBounds(300, 350, 120, 30);
        add(btnChooseImage);

        // ===== BUTTON =====
        JButton btnAdd = new JButton("Thêm");
        JButton btnUpdate = new JButton("Sửa");
        JButton btnDelete = new JButton("Xoá");
        JButton btnClear = new JButton("Mới");

        btnAdd.setBounds(20, 520, 80, 30);
        btnUpdate.setBounds(110, 520, 80, 30);
        btnDelete.setBounds(200, 520, 80, 30);
        btnClear.setBounds(290, 520, 80, 30);

        add(btnAdd); add(btnUpdate);
        add(btnDelete); add(btnClear);

        // ===== TABLE =====
        model = new DefaultTableModel(
                new Object[]{"ID", "Tên", "Giá", "Tồn", "Danh mục", "Hãng"}, 0
        );
        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(450, 20, 520, 530);
        add(scroll);

        // ================= EVENTS =================
        btnChooseImage.addActionListener(e -> chooseImage());
        btnAdd.addActionListener(e -> addProduct());
        btnUpdate.addActionListener(e -> updateProduct());
        btnDelete.addActionListener(e -> deleteProduct());
        btnClear.addActionListener(e -> clearForm());

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                fillForm(row);
            }
        });
    }

    // ================= HELPER =================
    private void addLabel(String text, int x, int y) {
        JLabel lb = new JLabel(text);
        lb.setBounds(x, y, 90, 25);
        add(lb);
    }

    private JTextField addText(int x, int y, int w, boolean enable) {
        JTextField t = new JTextField();
        t.setBounds(x, y, w, 25);
        t.setEnabled(enable);
        add(t);
        return t;
    }

    // ================= LOAD =================
    private void loadCategory() {
        cboCategory.removeAllItems();
        for (Category c : categoryDAO.getAll()) cboCategory.addItem(c);
    }

    private void loadBrand() {
        cboBrand.removeAllItems();
        for (Brand b : brandDAO.getAll()) cboBrand.addItem(b);
    }

    private void loadTable() {
        model.setRowCount(0);
        for (Product p : productDAO.getAll()) {
            model.addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    vnd.format(p.getPrice()),
                    p.getStock(),
                    getCategoryName(p.getCategoryId()),
                    getBrandName(p.getBrandId())
            });
        }
    }

    // ================= IMAGE =================
    private void chooseImage() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            imagePath = ImageUtil.saveProductImage(
                    file, "sp_" + System.currentTimeMillis()
            );
            showImage(imagePath);
        }
    }

    private void showImage(String path) {
        if (path == null) {
            lblImage.setIcon(null);
            return;
        }
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(
                lblImage.getWidth(),
                lblImage.getHeight(),
                Image.SCALE_SMOOTH
        );
        lblImage.setIcon(new ImageIcon(img));
    }

    // ================= FORM =================
    private void fillForm(int row) {
        txtId.setText(model.getValueAt(row, 0).toString());
        txtName.setText(model.getValueAt(row, 1).toString());

        String price = model.getValueAt(row, 2).toString()
                .replaceAll("[^0-9]", "");
        txtPrice.setText(price);

        txtStock.setText(model.getValueAt(row, 3).toString());

        selectCombo(cboCategory, model.getValueAt(row, 4).toString());
        selectCombo(cboBrand, model.getValueAt(row, 5).toString());

        int id = Integer.parseInt(txtId.getText());
        Product p = productDAO.getAll().stream()
                .filter(x -> x.getId() == id)
                .findFirst().orElse(null);

        if (p != null) {
            txtDesc.setText(p.getDescription());
            imagePath = p.getImagePath();
            showImage(imagePath);
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtPrice.setText("");
        txtStock.setText("");
        txtDesc.setText("");
        imagePath = null;
        lblImage.setIcon(null);
        table.clearSelection();
    }

    // ================= CRUD =================
    private void addProduct() {
        Product p = getForm();
        if (p == null) return;
        productDAO.insert(p);
        loadTable();
        clearForm();
    }

    private void updateProduct() {
        if (txtId.getText().isEmpty()) return;
        Product p = getForm();
        if (p == null) return;
        p.setId(Integer.parseInt(txtId.getText()));
        productDAO.update(p);
        loadTable();
    }

    private void deleteProduct() {
        if (txtId.getText().isEmpty()) return;
        int id = Integer.parseInt(txtId.getText());
        if (JOptionPane.showConfirmDialog(this,
                "Xoá sản phẩm này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

            productDAO.delete(id);
            loadTable();
            clearForm();
        }
    }

    private Product getForm() {
        try {
            Product p = new Product();
            p.setName(txtName.getText().trim());
            p.setDescription(txtDesc.getText().trim());
            p.setPrice(Double.parseDouble(txtPrice.getText()));
            p.setStock(Integer.parseInt(txtStock.getText()));
            p.setCategoryId(((Category) cboCategory.getSelectedItem()).getId());
            p.setBrandId(((Brand) cboBrand.getSelectedItem()).getId());
            p.setImagePath(imagePath);
            return p;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ");
            return null;
        }
    }

    private void selectCombo(JComboBox<?> cbo, String name) {
        for (int i = 0; i < cbo.getItemCount(); i++) {
            if (cbo.getItemAt(i).toString().equals(name)) {
                cbo.setSelectedIndex(i);
                break;
            }
        }
    }

    private String getCategoryName(int id) {
        for (Category c : categoryDAO.getAll())
            if (c.getId() == id) return c.getName();
        return "";
    }

    private String getBrandName(int id) {
        for (Brand b : brandDAO.getAll())
            if (b.getId() == id) return b.getName();
        return "";
    }
}
