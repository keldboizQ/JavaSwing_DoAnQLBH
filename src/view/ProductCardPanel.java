package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import model.Product;

public class ProductCardPanel extends JPanel {

    private Product product;

    public ProductCardPanel(Product product, Runnable onClick) {
        this.product = product;

        // 🔥 CARD TO HƠN
        setPreferredSize(new Dimension(240, 200));
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setBackground(Color.WHITE);

        // ===== IMAGE =====
        JLabel lblImg = new JLabel();
        lblImg.setHorizontalAlignment(JLabel.CENTER);
        lblImg.setPreferredSize(new Dimension(210, 130));

        if (product.getImagePath() != null && !product.getImagePath().isEmpty()) {
            ImageIcon icon = new ImageIcon(product.getImagePath());
            if (icon.getIconWidth() > 0) {
                Image img = icon.getImage().getScaledInstance(
                        200, 140, Image.SCALE_SMOOTH
                );
                lblImg.setIcon(new ImageIcon(img));
            }
        }

        // ===== INFO =====
        JLabel lblName = new JLabel(product.getName());
        lblName.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel lblPrice = new JLabel("💰 Giá: " + (int) product.getPrice() + " VNĐ");
        lblPrice.setFont(new Font("Arial", Font.PLAIN, 13));

        JLabel lblStock = new JLabel("📦 Tồn kho: " + product.getStock());
        lblStock.setFont(new Font("Arial", Font.PLAIN, 13));

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        info.setBackground(Color.WHITE);

        info.add(lblName);
        info.add(Box.createVerticalStrut(5));
        info.add(lblPrice);
        info.add(lblStock);

        add(lblImg, BorderLayout.NORTH);
        add(info, BorderLayout.CENTER);

        // ===== CLICK =====
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onClick.run();
            }
        });
    }

    public Product getProduct() {
        return product;
    }
}
