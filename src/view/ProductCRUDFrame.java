package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import dao.*;
import model.*;

public class ProductCRUDFrame extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField txtId, txtName, txtDesc, txtPrice, txtStock, txtNewBrand;
    private JComboBox<Category> cboCategory;
    private JComboBox<Brand> cboBrand;

    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnAddBrand;

    private ProductDAO productDAO = new ProductDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();
    private BrandDAO brandDAO = new BrandDAO();

    private NumberFormat vnd = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public ProductCRUDFrame() {
        setTitle("Quản lý Sản phẩm");
        setSize(900, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);

        initUI();
        loadCategories();
        loadBrands();
        loadTable();
    }

    // ================= UI =================
    private void initUI() {

        JLabel lblId = new JLabel("ID:");
        JLabel lblName = new JLabel("Tên:");
        JLabel lblDesc = new JLabel("Mô tả:");
        JLabel lblPrice = new JLabel("Giá:");
        JLabel lblStock = new JLabel("Tồn kho:");
        JLabel lblCat = new JLabel("Danh mục:");
        JLabel lblBrand = new JLabel("Hãng:");
        JLabel lblNewBrand = new JLabel("Hãng mới:");

        lblId.setBounds(20, 20, 100, 25);
        lblName.setBounds(20, 50, 100, 25);
        lblDesc.setBounds(20, 80, 100, 25);
        lblPrice.setBounds(20, 110, 100, 25);
        lblStock.setBounds(20, 140, 100, 25);
        lblCat.setBounds(20, 170, 100, 25);
        lblBrand.setBounds(20, 200, 100, 25);
        lblNewBrand.setBounds(20, 230, 100, 25);

        txtId = new JTextField();
        txtId.setEnabled(false);
        txtName = new JTextField();
        txtDesc = new JTextField();
        txtPrice = new JTextField();
        txtStock = new JTextField();
        txtNewBrand = new JTextField();

        txtId.setBounds(120, 20, 150, 25);
        txtName.setBounds(120, 50, 200, 25);
        txtDesc.setBounds(120, 80, 300, 25);
        txtPrice.setBounds(120, 110, 150, 25);
        txtStock.setBounds(120, 140, 150, 25);
        txtNewBrand.setBounds(120, 230, 200, 25);

        cboCategory = new JComboBox<>();
        cboBrand = new JComboBox<>();

        cboCategory.setBounds(120, 170, 200, 25);
        cboBrand.setBounds(120, 200, 200, 25);

        btnAddBrand = new JButton("Thêm hãng");
        btnAddBrand.setBounds(330, 230, 100, 25);

        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xoá");
        btnClear = new JButton("Làm mới");

        btnAdd.setBounds(20, 270, 80, 30);
        btnUpdate.setBounds(110, 270, 80, 30);
        btnDelete.setBounds(200, 270, 80, 30);
        btnClear.setBounds(290, 270, 90, 30);

        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Tên", "Mô tả", "Giá", "Tồn", "Danh mục", "Hãng"}, 0
        );
        table = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(450, 20, 420, 420);

        add(lblId); add(txtId);
        add(lblName); add(txtName);
        add(lblDesc); add(txtDesc);
        add(lblPrice); add(txtPrice);
        add(lblStock); add(txtStock);
        add(lblCat); add(cboCategory);
        add(lblBrand); add(cboBrand);
        add(lblNewBrand); add(txtNewBrand); add(btnAddBrand);

        add(btnAdd); add(btnUpdate); add(btnDelete); add(btnClear);
        add(scroll);

        // EVENTS
        btnAdd.addActionListener(e -> addProduct());
        btnUpdate.addActionListener(e -> updateProduct());
        btnDelete.addActionListener(e -> deleteProduct());
        btnClear.addActionListener(e -> clearForm());
        btnAddBrand.addActionListener(e -> addNewBrand());

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                txtId.setText(tableModel.getValueAt(row, 0).toString());
                txtName.setText(tableModel.getValueAt(row, 1).toString());
                txtDesc.setText(tableModel.getValueAt(row, 2).toString());
                txtPrice.setText(tableModel.getValueAt(row, 3).toString().replaceAll("[^0-9]", ""));
                txtStock.setText(tableModel.getValueAt(row, 4).toString());

                selectCombo(cboCategory, tableModel.getValueAt(row, 5).toString());
                selectCombo(cboBrand, tableModel.getValueAt(row, 6).toString());
            }
        });
    }

    // ================= LOAD =================
    private void loadCategories() {
        cboCategory.removeAllItems();
        for (Category c : categoryDAO.getAll()) {
            cboCategory.addItem(c);
        }
    }

    private void loadBrands() {
        cboBrand.removeAllItems();
        for (Brand b : brandDAO.getAll()) {
            cboBrand.addItem(b);
        }
    }

    private void loadTable() {
        tableModel.setRowCount(0);
        for (Product p : productDAO.getAll()) {
            tableModel.addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    p.getDescription(),
                    vnd.format(p.getPrice()),
                    p.getStock(),
                    getCategoryName(p.getCategoryId()),
                    getBrandName(p.getBrandId())
            });
        }
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
        if (JOptionPane.showConfirmDialog(this, "Xoá sản phẩm này?",
                "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            productDAO.delete(id);
            loadTable();
            clearForm();
        }
    }

    // ================= BRAND QUICK ADD =================
    private void addNewBrand() {
        String name = txtNewBrand.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhập tên hãng mới");
            return;
        }
        Brand b = new Brand();
        b.setName(name);
        brandDAO.insert(b);
        loadBrands();
        txtNewBrand.setText("");
        JOptionPane.showMessageDialog(this, "Đã thêm hãng mới");
    }

    // ================= UTIL =================
    private Product getForm() {
        try {
            String name = txtName.getText().trim();
            String desc = txtDesc.getText().trim();
            double price = Double.parseDouble(txtPrice.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());
            Category c = (Category) cboCategory.getSelectedItem();
            Brand b = (Brand) cboBrand.getSelectedItem();

            if (name.isEmpty() || desc.isEmpty() || c == null || b == null) {
                JOptionPane.showMessageDialog(this, "Nhập đầy đủ thông tin");
                return null;
            }

            Product p = new Product();
            p.setName(name);
            p.setDescription(desc);
            p.setPrice(price);
            p.setStock(stock);
            p.setCategoryId(c.getId());
            p.setBrandId(b.getId());
            return p;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Giá và tồn kho phải là số");
            return null;
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtDesc.setText("");
        txtPrice.setText("");
        txtStock.setText("");
        txtNewBrand.setText("");
        table.clearSelection();
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
        for (Category c : categoryDAO.getAll()) {
            if (c.getId() == id) return c.getName();
        }
        return "";
    }

    private String getBrandName(int id) {
        for (Brand b : brandDAO.getAll()) {
            if (b.getId() == id) return b.getName();
        }
        return "";
    }
}
