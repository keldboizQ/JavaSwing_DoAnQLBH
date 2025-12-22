package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

import dao.AccountDAO;
import model.Account;

public class AccountFrame extends JFrame {

    private JTable table;
    private DefaultTableModel model;
    private JTextField txtId, txtEmail, txtPass;
    private JComboBox<String> cboRole;

    private AccountDAO dao = new AccountDAO();

    // ===== Constructor mặc định =====
    public AccountFrame() {
        setTitle("Quản lý tài khoản");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);

        initUI();
        loadTable();
    }

    // ===== Constructor có check quyền =====
    public AccountFrame(Account acc) {

        if (acc == null || !"ADMIN".equalsIgnoreCase(acc.getRoleName())) {
            JOptionPane.showMessageDialog(this, "Bạn không có quyền!");
            dispose();
            return;
        }

        setTitle("Quản lý tài khoản");
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
        JLabel lblEmail = new JLabel("Email:");
        JLabel lblPass = new JLabel("Mật khẩu:");
        JLabel lblRole = new JLabel("Quyền:");

        lblId.setBounds(20, 20, 80, 25);
        lblEmail.setBounds(20, 60, 80, 25);
        lblPass.setBounds(20, 100, 80, 25);
        lblRole.setBounds(20, 140, 80, 25);

        txtId = new JTextField();
        txtId.setBounds(120, 20, 80, 25);
        txtId.setEnabled(false);

        txtEmail = new JTextField();
        txtEmail.setBounds(120, 60, 200, 25);

        txtPass = new JTextField();
        txtPass.setBounds(120, 100, 200, 25);

        cboRole = new JComboBox<>(new String[]{"ADMIN", "STAFF"});
        cboRole.setBounds(120, 140, 200, 25);

        JButton btnAdd = new JButton("Thêm");
        JButton btnUpdate = new JButton("Sửa quyền");
        JButton btnDelete = new JButton("Xoá");
        JButton btnReset = new JButton("Reset MK");
        btnReset.setBounds(350, 20, 100, 30);
        add(btnReset);

        btnReset.addActionListener(e -> resetPassword());



        btnAdd.setBounds(350, 60, 100, 30);
        btnUpdate.setBounds(350, 100, 100, 30);
        btnDelete.setBounds(350, 140, 100, 30);

        model = new DefaultTableModel(
                new Object[]{"ID", "Email", "Quyền"}, 0
        );
        table = new JTable(model);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(20, 190, 440, 150);

        add(lblId); add(txtId);
        add(lblEmail); add(txtEmail);
        add(lblPass); add(txtPass);
        add(lblRole); add(cboRole);
        add(btnAdd); add(btnUpdate); add(btnDelete);
        add(scroll);

        btnAdd.addActionListener(evt -> add());
        btnUpdate.addActionListener(evt -> update());
        btnDelete.addActionListener(evt -> delete());

        table.getSelectionModel().addListSelectionListener(evt -> {
            int r = table.getSelectedRow();
            if (r >= 0) {
                txtId.setText(model.getValueAt(r, 0).toString());
                txtEmail.setText(model.getValueAt(r, 1).toString());
                cboRole.setSelectedItem(model.getValueAt(r, 2).toString());
            }
        });
    }

    // ================= Load =================
    private void loadTable() {
        model.setRowCount(0);
        List<Account> list = dao.getAll();
        for (Account a : list) {
            model.addRow(new Object[]{
                    a.getId(),
                    a.getEmail(),
                    a.getRoleName()
            });
        }
    }

    // ================= Add =================
    private void add() {
        String email = txtEmail.getText();
        String pass = txtPass.getText();
        String role = cboRole.getSelectedItem().toString();

        if (email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhập đủ email và mật khẩu");
            return;
        }

        int roleId = role.equals("ADMIN") ? 1 : 2;
        dao.insert(email, pass, roleId);
        loadTable();
    }

    // ================= Update =================
    private void update() {
        if (txtId.getText().isEmpty()) return;

        int id = Integer.parseInt(txtId.getText());
        String role = cboRole.getSelectedItem().toString();
        int roleId = role.equals("ADMIN") ? 1 : 2;

        dao.updateRole(id, roleId);
        loadTable();
    }

    // ================= Delete =================
    private void delete() {
        if (txtId.getText().isEmpty()) return;

        int id = Integer.parseInt(txtId.getText());
        dao.delete(id);
        loadTable();
    }
    
    private void resetPassword() {

        if (txtId.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chọn tài khoản cần reset!");
            return;
        }

        String newPass = JOptionPane.showInputDialog(this,
                "Nhập mật khẩu mới:");

        if (newPass == null || newPass.isEmpty()) return;

        int id = Integer.parseInt(txtId.getText());

        dao.resetPassword(id, newPass);

        JOptionPane.showMessageDialog(this,
                "Reset mật khẩu thành công!");
    }


}
