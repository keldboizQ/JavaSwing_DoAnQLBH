package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.*;

import dao.*;
import model.*;

public class ProductByCategoryCRUDFrame extends JFrame {

    private JTable tblCategory, tblProduct;
    private DefaultTableModel modelCategory, modelProduct;

    private JTextField txtId, txtName, txtPrice, txtStock;
    private JTextArea txtDesc;
    private JComboBox<Brand> cboBrand;

    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    private CategoryDAO categoryDAO = new CategoryDAO();
    private ProductDAO productDAO = new ProductDAO();
    private BrandDAO brandDAO = new BrandDAO();

    private int currentCatId = -1;

    private NumberFormat vnd = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public ProductByCategoryCRUDFrame() {
        setTitle("Quản lý sản phẩm theo danh mục");
        setSize(1050, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);

        initUI();
        loadCategories();
        loadBrands();
    }

    private void initUI() {

        // ===== DANH MỤC =====
        JLabel lblCat = new JLabel("Danh mục");
        lblCat.setBounds(20, 10, 100, 25);
        add(lblCat);

        modelCategory = new DefaultTableModel(
                new Object[]{"ID", "Tên danh mục"}, 0);
        tblCategory = new JTable(modelCategory);
        JScrollPane spCat = new JScrollPane(tblCategory);
        spCat.setBounds(20, 40, 250, 500);
        add(spCat);

        // ===== SẢN PHẨM =====
        JLabel lblPro = new JLabel("Sản phẩm");
        lblPro.setBounds(300, 10, 100, 25);
        add(lblPro);

        modelProduct = new DefaultTableModel(
                new Object[]{"ID", "Tên", "Giá", "Tồn kho", "Hãng"}, 0);
        tblProduct = new JTable(modelProduct);
        JScrollPane spPro = new JScrollPane(tblProduct);
        spPro.setBounds(300, 40, 720, 250);
        add(spPro);

        // ===== FORM =====
        int x = 300, y = 310;

        addLabel("ID:", x, y);
        txtId = addText(x + 100, y, 100, false);

        addLabel("Tên:", x, y += 35);
        txtName = addText(x + 100, y, 250, true);

        addLabel("Giá:", x, y += 35);
        txtPrice = addText(x + 100, y, 150, true);

        addLabel("Tồn kho:", x, y += 35);
        txtStock = addText(x + 100, y, 100, true);

        addLabel("Hãng:", x, y += 35);
        cboBrand = new JComboBox<>();
        cboBrand.setBounds(x + 100, y, 200, 25);
        add(cboBrand);

        addLabel("Mô tả:", x, y += 35);
        txtDesc = new JTextArea();
        JScrollPane spDesc = new JScrollPane(txtDesc);
        spDesc.setBounds(x + 100, y, 300, 70);
        add(spDesc);

        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xoá");
        btnClear = new JButton("Mới");

        btnAdd.setBounds(650, 310, 120, 30);
        btnUpdate.setBounds(650, 350, 120, 30);
        btnDelete.setBounds(650, 390, 120, 30);
        btnClear.setBounds(650, 430, 120, 30);

        add(btnAdd); add(btnUpdate);
        add(btnDelete); add(btnClear);

        // ===== EVENTS =====
        tblCategory.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblCategory.getSelectedRow();
                if (row >= 0) {
                    currentCatId = (int) modelCategory.getValueAt(row, 0);
                    loadProductsByCategory(currentCatId);
                    clearForm();
                }
            }
        });

        tblProduct.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblProduct.getSelectedRow();
                if (row >= 0) fillFormFromTable(row);
            }
        });

        btnAdd.addActionListener(e -> addProduct());
        btnUpdate.addActionListener(e -> updateProduct());
        btnDelete.addActionListener(e -> deleteProduct());
        btnClear.addActionListener(e -> clearForm());
    }

    // ===== UI helper =====
    private void addLabel(String text, int x, int y) {
        JLabel lb = new JLabel(text);
        lb.setBounds(x, y, 90, 25);
        add(lb);
    }

    private JTextField addText(int x, int y, int w, boolean en) {
        JTextField t = new JTextField();
        t.setBounds(x, y, w, 25);
        t.setEnabled(en);
        add(t);
        return t;
    }

    // ===== LOAD =====
    private void loadCategories() {
        modelCategory.setRowCount(0);
        for (Category c : categoryDAO.getAll()) {
            modelCategory.addRow(new Object[]{c.getId(), c.getName()});
        }
    }

    private void loadBrands() {
        cboBrand.removeAllItems();
        for (Brand b : brandDAO.getAll()) {
            cboBrand.addItem(b);
        }
    }

    private void loadProductsByCategory(int catId) {
        modelProduct.setRowCount(0);
        for (Product p : productDAO.getByCategory(catId)) {
            String brandName = getBrandName(p.getBrandId());
            modelProduct.addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    vnd.format(p.getPrice()),
                    p.getStock(),
                    brandName
            });
        }
    }

    private String getBrandName(int brandId) {
        for (int i = 0; i < cboBrand.getItemCount(); i++) {
            Brand b = cboBrand.getItemAt(i);
            if (b.getId() == brandId) return b.getName();
        }
        return "";
    }

    // ===== FORM =====
    private void fillFormFromTable(int row) {
        txtId.setText(modelProduct.getValueAt(row, 0).toString());
        txtName.setText(modelProduct.getValueAt(row, 1).toString());

        String priceStr = modelProduct.getValueAt(row, 2).toString()
                .replaceAll("[^0-9]", "");
        txtPrice.setText(priceStr);

        txtStock.setText(modelProduct.getValueAt(row, 3).toString());

        String brandName = modelProduct.getValueAt(row, 4).toString();
        selectBrandByName(brandName);

        // load description từ DB
        int id = Integer.parseInt(txtId.getText());
        Product p = productDAO.getAll().stream()
                .filter(x -> x.getId() == id)
                .findFirst().orElse(null);
        if (p != null) txtDesc.setText(p.getDescription());
    }

    private void selectBrandByName(String name) {
        for (int i = 0; i < cboBrand.getItemCount(); i++) {
            if (cboBrand.getItemAt(i).getName().equals(name)) {
                cboBrand.setSelectedIndex(i);
                break;
            }
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtPrice.setText("");
        txtStock.setText("");
        txtDesc.setText("");
        if (cboBrand.getItemCount() > 0) cboBrand.setSelectedIndex(0);
        tblProduct.clearSelection();
    }

    // ===== VALIDATE =====
    private boolean validateForm() {
        if (currentCatId == -1) {
            show("Vui lòng chọn danh mục trước!");
            return false;
        }
        if (txtName.getText().trim().isEmpty()) {
            show("Tên sản phẩm không được rỗng");
            return false;
        }
        try {
            double price = Double.parseDouble(txtPrice.getText());
            if (price <= 0) throw new Exception();
        } catch (Exception e) {
            show("Giá phải là số > 0");
            return false;
        }
        try {
            int stock = Integer.parseInt(txtStock.getText());
            if (stock < 0) throw new Exception();
        } catch (Exception e) {
            show("Tồn kho phải là số >= 0");
            return false;
        }
        if (cboBrand.getSelectedItem() == null) {
            show("Chọn hãng!");
            return false;
        }
        return true;
    }

    // ===== CRUD =====
    private void addProduct() {
        if (!validateForm()) return;

        Brand b = (Brand) cboBrand.getSelectedItem();

        Product p = new Product();
        p.setName(txtName.getText().trim());
        p.setDescription(txtDesc.getText().trim());
        p.setPrice(Double.parseDouble(txtPrice.getText()));
        p.setStock(Integer.parseInt(txtStock.getText()));
        p.setCategoryId(currentCatId);
        p.setBrandId(b.getId());

        productDAO.insert(p);
        loadProductsByCategory(currentCatId);
        clearForm();
    }

    private void updateProduct() {
        if (txtId.getText().isEmpty()) return;
        if (!validateForm()) return;

        Brand b = (Brand) cboBrand.getSelectedItem();

        Product p = new Product();
        p.setId(Integer.parseInt(txtId.getText()));
        p.setName(txtName.getText().trim());
        p.setDescription(txtDesc.getText().trim());
        p.setPrice(Double.parseDouble(txtPrice.getText()));
        p.setStock(Integer.parseInt(txtStock.getText()));
        p.setCategoryId(currentCatId);
        p.setBrandId(b.getId());

        productDAO.update(p);
        loadProductsByCategory(currentCatId);
    }

    private void deleteProduct() {
        if (txtId.getText().isEmpty()) return;

        int id = Integer.parseInt(txtId.getText());
        if (JOptionPane.showConfirmDialog(this,
                "Xoá sản phẩm này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {

            productDAO.delete(id);
            loadProductsByCategory(currentCatId);
            clearForm();
        }
    }

    private void show(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }
}
