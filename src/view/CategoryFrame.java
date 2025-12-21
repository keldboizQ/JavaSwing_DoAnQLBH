package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.util.List;

import dao.CategoryDAO;
import model.Category;

public class CategoryFrame extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtId, txtName;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear;

    private CategoryDAO categoryDAO = new CategoryDAO();

    public CategoryFrame() {
        setTitle("Quản lý Danh mục");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);

        initUI();
        loadTable();
    }

    // ================= UI =================
    private void initUI() {

        JLabel lblId = new JLabel("ID:");
        JLabel lblName = new JLabel("Tên danh mục:");

        lblId.setBounds(20, 20, 100, 25);
        lblName.setBounds(20, 60, 100, 25);

        txtId = new JTextField();
        txtName = new JTextField();

        txtId.setBounds(130, 20, 100, 25);
        txtId.setEnabled(false);
        txtName.setBounds(130, 60, 200, 25);

        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Sửa");
        btnDelete = new JButton("Xoá");
        btnClear = new JButton("Làm mới");

        btnAdd.setBounds(20, 100, 80, 30);
        btnUpdate.setBounds(110, 100, 80, 30);
        btnDelete.setBounds(200, 100, 80, 30);
        btnClear.setBounds(290, 100, 80, 30);

        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Tên danh mục"}, 0
        );
        table = new JTable(tableModel);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(20, 150, 440, 180);

        add(lblId); add(txtId);
        add(lblName); add(txtName);
        add(btnAdd); add(btnUpdate);
        add(btnDelete); add(btnClear);
        add(scroll);

        // ========= EVENTS =========
        btnAdd.addActionListener(e -> addCategory());
        btnUpdate.addActionListener(e -> updateCategory());
        btnDelete.addActionListener(e -> deleteCategory());
        btnClear.addActionListener(e -> clearForm());

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                txtId.setText(tableModel.getValueAt(row, 0).toString());
                txtName.setText(tableModel.getValueAt(row, 1).toString());
            }
        });
    }

    // ================= LOAD DATA =================
    private void loadTable() {
        tableModel.setRowCount(0);
        List<Category> list = categoryDAO.getAll();

        for (Category c : list) {
            tableModel.addRow(new Object[]{
                    c.getId(),
                    c.getName()
            });
        }
    }

    // ================= CRUD =================
    private void addCategory() {
        String name = txtName.getText();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhập tên danh mục");
            return;
        }

        Category c = new Category();
        c.setName(name);

        categoryDAO.insert(c);
        loadTable();
        clearForm();
    }

    private void updateCategory() {
        if (txtId.getText().isEmpty()) return;

        Category c = new Category();
        c.setId(Integer.parseInt(txtId.getText()));
        c.setName(txtName.getText());

        categoryDAO.update(c);
        loadTable();
    }

    private void deleteCategory() {
        if (txtId.getText().isEmpty()) return;

        int id = Integer.parseInt(txtId.getText());

        if (JOptionPane.showConfirmDialog(this,
                "Xoá danh mục này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION) {

            categoryDAO.delete(id);
            loadTable();
            clearForm();
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        table.clearSelection();
    }
}
