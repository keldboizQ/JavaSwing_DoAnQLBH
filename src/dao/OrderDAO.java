package dao;

import java.sql.*;
import util.DBConnection;

public class OrderDAO {

    public int insertOrder(int adminId, int customerId, double total) {

        String sql = """
            INSERT INTO ORDERS (Total, id_admin, id_customer)
            VALUES (?, ?, ?)
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDouble(1, total);
            ps.setInt(2, adminId);
            ps.setInt(3, customerId);

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();

            if (rs.next()) {
                return rs.getInt(1); // id_order
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
}
