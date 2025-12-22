package dao;

import java.sql.*;
import java.util.*;
import model.Account;
import util.DBConnection;
import util.PasswordUtil;

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
            "VALUES (?, HASHBYTES('SHA2_256', ? + CAST(? AS NVARCHAR(36))), ?, ?)";

        UUID salt = UUID.randomUUID();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);
            ps.setString(3, salt.toString());
            ps.setString(4, salt.toString());
            ps.setInt(5, roleId);

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

            if (!rs.next()) {
                System.out.println("❌ Không tìm thấy account với email: " + email);
                return null;
            }

            System.out.println("✅ Tìm thấy account: " + rs.getString("email"));

            byte[] dbHash = rs.getBytes("passwordHash");
            String salt = rs.getString("Salt");

            byte[] inputHash = PasswordUtil.hash(password, salt);

            System.out.println("DB Hash  : " + Arrays.toString(dbHash));
            System.out.println("Salt     : " + salt);
            System.out.println("InputHash: " + Arrays.toString(inputHash));

            if (Arrays.equals(dbHash, inputHash)) {
                Account acc = new Account();
                acc.setId(rs.getInt("id_account"));
                acc.setEmail(rs.getString("email"));
                acc.setRoleName(rs.getString("name_role"));
                return acc;
            } else {
                System.out.println("❌ Hash không khớp!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    // ===== Reset mật khẩu =====
    public void resetPassword(int id, String newPassword) {

        String sql =
            "UPDATE ACCOUNTS " +
            "SET passwordHash = HASHBYTES('SHA2_256', ? + CAST(? AS NVARCHAR(36))), " +
            "    Salt = ? " +
            "WHERE id_account = ?";

        UUID salt = UUID.randomUUID();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setString(2, salt.toString());
            ps.setString(3, salt.toString());
            ps.setInt(4, id);

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

    // ===== Xoá =====
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
}
