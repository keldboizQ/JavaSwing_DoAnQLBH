package dao;

import java.util.Arrays;
import java.sql.*;
import java.util.*;
import model.Account;
import util.DBConnection;

public class AccountDAO {

    // ===== Lấy tất cả account =====
    public List<Account> getAll() {
        List<Account> list = new ArrayList<>();

        String sql =
            "SELECT a.id_account, a.email, r.name_role " +
            "FROM ACCOUNTS a " +
            "JOIN ROLES r ON a.id_role = r.id_role";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Account acc = new Account();
                acc.setId(rs.getInt("id_account"));
                acc.setEmail(rs.getString("email"));
                acc.setRoleName(rs.getString("name_role"));
                list.add(acc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===== Thêm account =====
    public void insert(String email, String password, int roleId) {

    	String sql =
    		    "INSERT INTO ACCOUNTS(email, passwordHash, Salt, id_role) " +
    		    "VALUES (?, HASHBYTES('SHA2_256', CONCAT(?, ?)), ?, ?)";


        UUID salt = UUID.randomUUID();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);             // email
            ps.setString(2, password);          // password plain
            ps.setString(3, salt.toString());   // salt dùng để hash
            ps.setString(4, salt.toString());   // lưu salt
            ps.setInt(5, roleId);               // role id

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== Cập nhật role =====
    public void updateRole(int id, int roleId) {

        String sql = "UPDATE ACCOUNTS SET id_role=? WHERE id_account=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, roleId);
            ps.setInt(2, id);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== Xoá account =====
    public void delete(int id) {

        String sql = "DELETE FROM ACCOUNTS WHERE id_account=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
 // ===== Đăng nhập =====
    public Account login(String email, String password) {

        String sql =
            "SELECT a.id_account, a.email, a.passwordHash, a.Salt, r.name_role " +
            "FROM ACCOUNTS a " +
            "JOIN ROLES r ON a.id_role = r.id_role " +
            "WHERE a.email = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                byte[] dbHash = rs.getBytes("passwordHash");
                String salt = rs.getString("Salt");

                // Hash password người dùng nhập
                String sqlCheck =
                	    "SELECT HASHBYTES('SHA2_256', CONCAT(?, ?))";


                try (PreparedStatement ps2 = con.prepareStatement(sqlCheck)) {
                	ps2.setString(1, password);
                	ps2.setString(2, salt);
                    ResultSet rs2 = ps2.executeQuery();

                    if (rs2.next()) {
                        byte[] inputHash = rs2.getBytes(1);

                        if (Arrays.equals(dbHash, inputHash)) {
                            Account acc = new Account();
                            acc.setId(rs.getInt("id_account"));
                            acc.setEmail(rs.getString("email"));
                            acc.setRoleName(rs.getString("name_role"));
                            return acc;
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
