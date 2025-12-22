package view;

import javax.swing.*;
import dao.AccountDAO;

public class ResetPasswordFrame extends JFrame {

    private JTextField txtId;
    private JPasswordField txtPass;

    private AccountDAO dao = new AccountDAO();

    public ResetPasswordFrame() {

        setTitle("Reset mật khẩu");
        setSize(300, 180);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JLabel lblId = new JLabel("ID:");
        JLabel lblPass = new JLabel("Mật khẩu mới:");

        lblId.setBounds(20, 20, 100, 25);
        lblPass.setBounds(20, 60, 100, 25);

        txtId = new JTextField();
        txtId.setBounds(120, 20, 140, 25);

        txtPass = new JPasswordField();
        txtPass.setBounds(120, 60, 140, 25);

        JButton btnReset = new JButton("Reset");
        btnReset.setBounds(90, 100, 100, 30);

        add(lblId); add(txtId);
        add(lblPass); add(txtPass);
        add(btnReset);

        btnReset.addActionListener(e -> reset());
    }

    private void reset() {
        if (txtId.getText().isEmpty()) return;

        int id = Integer.parseInt(txtId.getText());
        String pass = new String(txtPass.getPassword());

        if (pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhập mật khẩu mới");
            return;
        }

        dao.resetPassword(id, pass);
        JOptionPane.showMessageDialog(this, "Reset mật khẩu thành công!");
        dispose();
    }
}
