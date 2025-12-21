package view;

import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.*;

import dao.*;
import model.*;

public class BusinessFrame extends JFrame {

    private JComboBox<Product> cboProduct;
    private JTextField txtQty;
    private JTable table;
    private DefaultTableModel model;
    private JLabel lblTotal;
    
    private JTextField txtCusName, txtPhone;
    private JTextArea txtAddress;


    private ProductDAO productDAO = new ProductDAO();
    private OrderDAO orderDAO = new OrderDAO();
    private OrderDetailDAO detailDAO = new OrderDetailDAO();

    private Account account;
    private double totalAmount = 0; // ✅ lưu tổng tiền dạng số

    public BusinessFrame(Account acc) {
        this.account = acc;

        setTitle("Bán hàng");
        setSize(720, 550);
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

        JLabel lblProduct = new JLabel("Sản phẩm:");
        JLabel lblQty = new JLabel("Số lượng:");
        JLabel lblCus = new JLabel("Khách hàng:");
        JLabel lblPhone = new JLabel("SĐT:");
        JLabel lblAddr = new JLabel("Địa chỉ:");

        lblCus.setBounds(20, 90, 100, 25);
        lblPhone.setBounds(350, 90, 100, 25);
        lblAddr.setBounds(20, 340, 100, 25);

        txtCusName = new JTextField();
        txtCusName.setBounds(120, 90, 200, 25);

        txtPhone = new JTextField();
        txtPhone.setBounds(420, 90, 150, 25);

        txtAddress = new JTextArea();
        JScrollPane spAddr = new JScrollPane(txtAddress);
        spAddr.setBounds(120, 340, 300, 60);

        add(lblCus); add(txtCusName);
        add(lblPhone); add(txtPhone);
        add(lblAddr); add(spAddr);


        lblProduct.setBounds(20, 20, 100, 25);
        lblQty.setBounds(20, 60, 100, 25);

        cboProduct = new JComboBox<>();
        cboProduct.setBounds(120, 20, 250, 25);

        txtQty = new JTextField("1");
        txtQty.setBounds(120, 60, 100, 25);

        JButton btnAdd = new JButton("Thêm");
        btnAdd.setBounds(400, 20, 100, 30);

        model = new DefaultTableModel(
            new Object[]{"ID", "Tên", "Giá", "SL", "Thành tiền"}, 0
        );
        table = new JTable(model);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBounds(20, 130, 660, 200);



        lblTotal = new JLabel("Tổng: 0 VNĐ");
        lblTotal.setBounds(20, 420, 300, 30);


        JButton btnPay = new JButton("Thanh toán");
        btnPay.setBounds(520, 420, 150, 30);

        add(lblProduct); add(cboProduct);
        add(lblQty); add(txtQty);
        add(btnAdd);
        add(scroll);
        add(lblTotal);
        add(btnPay);

        btnAdd.addActionListener(e -> addToCart());
        btnPay.addActionListener(e -> checkout());
    }

    // ================= LOAD PRODUCT =================
    private void loadProducts() {
        for (Product p : productDAO.getAll()) {
            cboProduct.addItem(p);
        }
    }

    // ================= CART =================
    private void addToCart() {

        Product p = (Product) cboProduct.getSelectedItem();
        int qty = Integer.parseInt(txtQty.getText());

        if (qty <= 0 || qty > p.getStock()) {
            JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ");
            return;
        }

        double total = qty * p.getPrice();

        model.addRow(new Object[]{
            p.getId(),
            p.getName(),
            formatVND(p.getPrice()), // ✅ hiển thị VNĐ
            qty,
            formatVND(total)         // ✅ hiển thị VNĐ
        });

        calculateTotal();
    }

    private void calculateTotal() {

        totalAmount = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            // cột 4 đang là String "x.xxx.xxx VNĐ" → bỏ VNĐ & dấu chấm
            String val = model.getValueAt(i, 4).toString()
                    .replace(" VNĐ", "")
                    .replace(".", "");
            totalAmount += Double.parseDouble(val);
        }
        lblTotal.setText("Tổng: " + formatVND(totalAmount));
    }

    // ================= CHECKOUT =================
    private void checkout() {
    	
    	String cusName = txtCusName.getText().trim();
    	String phone = txtPhone.getText().trim();
    	String addr = txtAddress.getText().trim();

    	if (cusName.isEmpty() || phone.isEmpty() || addr.isEmpty()) {
    	    JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin khách hàng!");
    	    return;
    	}

    	if (!phone.matches("\\d{9,11}")) {
    	    JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ!");
    	    return;
    	}


        if (model.getRowCount() == 0) return;

        int adminId = account.getId();
        int customerId = 1; // TẠM

        int orderId = orderDAO.insertOrder(adminId, customerId, totalAmount);

        for (int i = 0; i < model.getRowCount(); i++) {

            int productId = (int) model.getValueAt(i, 0);

            String priceStr = model.getValueAt(i, 2).toString()
                    .replace(" VNĐ", "")
                    .replace(".", "");
            double price = Double.parseDouble(priceStr);

            int qty = (int) model.getValueAt(i, 3);

            detailDAO.insert(orderId, productId, price, qty);
            productDAO.updateStock(productId, qty);
        }

        JOptionPane.showMessageDialog(this, "Thanh toán thành công");
        model.setRowCount(0);
        totalAmount = 0;
        lblTotal.setText("Tổng: 0 VNĐ");
        txtCusName.setText("");
        txtPhone.setText("");
        txtAddress.setText("");

    }
}
