package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import util.DBConnection;

public class OrderDetailDAO {

    public void insert(int orderId, int productId, double price, int qty) {

        String sql = """
            INSERT INTO ORDERDETAILS
            (id_order, id_product, price, quantity)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ps.setInt(2, productId);
            ps.setDouble(3, price);
            ps.setInt(4, qty);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
