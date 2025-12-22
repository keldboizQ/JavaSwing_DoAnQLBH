package view;

import javax.swing.*;
import dao.AccountDAO;
import model.Account;

public class LoginFrame extends JFrame {

    JTextField txtEmail;
    JPasswordField txtPass;
    JButton btnLogin;

    public LoginFrame() {
        setTitle("Đăng nhập");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        txtEmail = new JTextField();
        txtPass = new JPasswordField();
        btnLogin = new JButton("Đăng nhập");

        txtEmail.setBounds(80, 30, 150, 25);
        txtPass.setBounds(80, 70, 150, 25);
        btnLogin.setBounds(80, 110, 150, 30);

        add(txtEmail);
        add(txtPass);
        add(btnLogin);

        btnLogin.addActionListener(e -> login());
    }

    private void login() {

    	String email = txtEmail.getText().trim();
    	String password = new String(txtPass.getPassword()).trim();


        AccountDAO dao = new AccountDAO();
        Account acc = dao.login(email, password);

        if (acc != null) {

            // 👇 phòng tránh roleName bị null
            if (acc.getRoleName() == null) {
                JOptionPane.showMessageDialog(this,
                    "Tài khoản chưa có quyền (roleName = null)");
                return;
            }

            new MainFrame(acc).setVisible(true);
            this.dispose();

        } else {
            JOptionPane.showMessageDialog(this,
                "Sai tài khoản hoặc mật khẩu");
        }
    }


    
    
}
