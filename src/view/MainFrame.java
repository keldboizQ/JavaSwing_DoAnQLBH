package view;

import javax.swing.*;
import model.Account;

public class MainFrame extends JFrame {

    JMenu menuAdmin, menuProduct, menuCategory, menuSearch, menuBusiness;
    JMenuItem miCategory, miSearch, miSell, miProduct;
    Account account;

    public MainFrame(Account acc) {
        this.account = acc;

        setTitle("Quản lý bán hàng");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JMenuBar bar = new JMenuBar();

        // ===== Menu Admin =====
        menuAdmin = new JMenu("Tài khoản");
        JMenuItem miAccount = new JMenuItem("Quản lý tài khoản");
        menuAdmin.add(miAccount);

        miAccount.addActionListener(evt -> {
            new AccountFrame().setVisible(true);
        });

        // ===== Menu Product =====
        menuProduct = new JMenu("Sản phẩm");
        JMenuItem miCRUD = new JMenuItem("Quản lý sản phẩm");
        menuProduct.add(miCRUD);

        miCRUD.addActionListener(evt -> {
            new ProductCRUDFrame().setVisible(true);
        });

        // ===== Menu Category =====
        menuCategory = new JMenu("Danh mục");
        miCategory = new JMenuItem("Quản lý danh mục");
        menuCategory.add(miCategory);

        miCategory.addActionListener(evt -> {
            new CategoryFrame().setVisible(true);
        });

        // ===== Menu Search =====
        menuSearch = new JMenu("Tra cứu");
        miSearch = new JMenuItem("Tra cứu sản phẩm");
        menuSearch.add(miSearch);

        miSearch.addActionListener(evt -> {
            new SearchFrame().setVisible(true);
        });

        // ===== Menu Business =====
        menuBusiness = new JMenu("Bán hàng");
        miSell = new JMenuItem("Tạo đơn hàng");
        menuBusiness.add(miSell);

        miSell.addActionListener(evt -> {
            new BusinessFrame(account).setVisible(true);
        });

        // ===== Add menu vào bar =====
        bar.add(menuCategory);
        bar.add(menuSearch);
        bar.add(menuBusiness);
        bar.add(menuProduct);
        bar.add(menuAdmin);

        setJMenuBar(bar);

        // ===== Check quyền =====
        checkRole();
    }

    // ✅ Ẩn menu admin nếu không phải ADMIN
    private void checkRole() {
        String role = account.getRoleName();

        if ("Quản lý".equalsIgnoreCase(role)) {
            // ADMIN thấy tất cả → không ẩn gì
            return;
        }

        if ("Nhân viên".equalsIgnoreCase(role)) {
            // STAFF chỉ được:
            // - Bán hàng
            // - Quản lý sản phẩm
            // - Tra cứu

            menuAdmin.setVisible(false);      // ❌ không quản lý tài khoản
            menuCategory.setVisible(false);   // ❌ không quản lý danh mục

            // ✔ menuProduct: quản lý sản phẩm
            // ✔ menuSearch: tra cứu
            // ✔ menuBusiness: bán hàng
        }
    }


}
