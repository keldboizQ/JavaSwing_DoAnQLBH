package view;

import java.awt.Image;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import dao.*;
import model.*;

public class BusinessFrame extends JFrame {

    private JPanel productPanel;
    private Product selectedProduct;

    private JTextField txtQty;
    private JTable table;
    private DefaultTableModel model;
    private JLabel lblTotal;
    private JLabel lblImage;

    private JTextField txtCusName, txtPhone;
    private JTextArea txtAddress;

    private ProductDAO productDAO = new ProductDAO();
    private OrderDAO orderDAO = new OrderDAO();
    private OrderDetailDAO detailDAO = new OrderDetailDAO();

    private Account account;
    private double totalAmount = 0;

    public BusinessFrame(Account acc) {
        this.account = acc;

        setTitle("Bán hàng");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(null);

        initUI();
        loadProducts();
    }

    // ================= FORMAT VNĐ =================
    private String formatVND(double amount) {
        NumberFormat vn = NumberFormat.getInstance(new Locale("vi", "VN"));
        return vn.format(amount) + " VNĐ";
    }

    // ================= UI =================
    private void initUI() {

        JLabel lblQty = new JLabel("Số lượng:");
        lblQty.setBounds(20, 160, 100, 25);

        txtQty = new JTextField("1");
        txtQty.setBounds(120, 160, 100, 25);

        JButton btnAdd = new JButton("Thêm vào giỏ");
        btnAdd.setBounds(240, 155, 150, 35);

        // ===== PRODUCT CARD PANEL =====
        productPanel = new JPanel();
        productPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 10));

        JScrollPane spProduct = new JScrollPane(productPanel);
        spProduct.setBounds(20, 20, 520, 120);
        add(spProduct);

        // ===== IMAGE =====
        lblImage = new JLabel();
        lblImage.setBounds(580, 20, 260, 180);
        lblImage.setBorder(BorderFactory.createTitledBorder("Ảnh sản phẩm"));
        add(lblImage);

        // ===== CUSTOMER =====
        JLabel lblCus = new JLabel("Khách hàng:");
        lblCus.setBounds(20, 210, 100, 25);
        txtCusName = new JTextField();
        txtCusName.setBounds(120, 210, 200, 25);

        JLabel lblPhone = new JLabel("SĐT:");
        lblPhone.setBounds(350, 210, 50, 25);
        txtPhone = new JTextField();
        txtPhone.setBounds(400, 210, 140, 25);

        JLabel lblAddr = new JLabel("Địa chỉ:");
        lblAddr.setBounds(20, 510, 100, 25);
        txtAddress = new JTextArea();
        JScrollPane spAddr = new JScrollPane(txtAddress);
        spAddr.setBounds(120, 510, 300, 60);

        // ===== TABLE =====
        model = new DefaultTableModel(
                new Object[]{"ID", "Tên", "Giá", "SL", "Thành tiền"}, 0
        );
        table = new JTable(model);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(20, 250, 820, 240);

        lblTotal = new JLabel("Tổng: 0 VNĐ");
        lblTotal.setBounds(20, 580, 300, 30);

        JButton btnPay = new JButton("Thanh toán");
        btnPay.setBounds(690, 580, 150, 30);

        add(lblQty);
        add(txtQty);
        add(btnAdd);

        add(lblCus);
        add(txtCusName);
        add(lblPhone);
        add(txtPhone);
        add(lblAddr);
        add(spAddr);

        add(scroll);
        add(lblTotal);
        add(btnPay);

        btnAdd.addActionListener(e -> addToCart());
        btnPay.addActionListener(e -> checkout());
    }

    // ================= LOAD PRODUCTS =================
    private void loadProducts() {
        productPanel.removeAll();

        for (Product p : productDAO.getAll()) {
            ProductCardPanel card = new ProductCardPanel(p, () -> {
                selectedProduct = p;
                showImage(p);
            });
            productPanel.add(card);
        }

        productPanel.revalidate();
        productPanel.repaint();
    }

    // ================= SHOW IMAGE =================
    private void showImage(Product p) {
        lblImage.setIcon(null);
        if (p == null) return;

        String path = p.getImagePath();
        if (path == null || path.isEmpty()) return;

        ImageIcon icon = new ImageIcon(path);
        if (icon.getIconWidth() <= 0) return;

        Image img = icon.getImage().getScaledInstance(
                lblImage.getWidth() - 10,
                lblImage.getHeight() - 20,
                Image.SCALE_SMOOTH
        );
        lblImage.setIcon(new ImageIcon(img));
    }

    // ================= CART =================
    private void addToCart() {

        if (selectedProduct == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm");
            return;
        }

        int qty;
        try {
            qty = Integer.parseInt(txtQty.getText());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ");
            return;
        }

        if (qty <= 0 || qty > selectedProduct.getStock()) {
            JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ");
            return;
        }

        double total = qty * selectedProduct.getPrice();

        model.addRow(new Object[]{
                selectedProduct.getId(),
                selectedProduct.getName(),
                formatVND(selectedProduct.getPrice()),
                qty,
                formatVND(total)
        });

        calculateTotal();
    }

    private void calculateTotal() {
        totalAmount = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            String val = model.getValueAt(i, 4).toString()
                    .replace(" VNĐ", "")
                    .replace(".", "");
            totalAmount += Double.parseDouble(val);
        }
        lblTotal.setText("Tổng: " + formatVND(totalAmount));
    }

    // ================= CHECKOUT =================
    private void checkout() {

        if (model.getRowCount() == 0) return;

        if (txtCusName.getText().isEmpty()
                || txtPhone.getText().isEmpty()
                || txtAddress.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nhập đủ thông tin khách hàng");
            return;
        }

        int orderId = orderDAO.insertOrder(
                account.getId(), 1, totalAmount
        );

        for (int i = 0; i < model.getRowCount(); i++) {

            int productId = (int) model.getValueAt(i, 0);
            int qty = (int) model.getValueAt(i, 3);

            String priceStr = model.getValueAt(i, 2).toString()
                    .replace(" VNĐ", "")
                    .replace(".", "");
            double price = Double.parseDouble(priceStr);

            detailDAO.insert(orderId, productId, price, qty);
            productDAO.updateStock(productId, qty);
        }

        JOptionPane.showMessageDialog(this, "Thanh toán thành công");
        model.setRowCount(0);
        lblTotal.setText("Tổng: 0 VNĐ");
    }
}
