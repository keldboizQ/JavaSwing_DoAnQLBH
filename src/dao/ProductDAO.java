package dao;

import java.sql.*;
import java.util.*;
import model.Product;
import util.DBConnection;

public class ProductDAO {

    // ===== GET ALL =====
    public List<Product> getAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM PRODUCTS";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===== GET BY CATEGORY =====
    public List<Product> getByCategory(int categoryId) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM PRODUCTS WHERE id_category = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(map(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===== SEARCH =====
    public List<Product> search(String keyword, Integer categoryId, Integer brandId) {
        List<Product> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT * FROM PRODUCTS WHERE 1=1"
        );

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND name_product LIKE ?");
        }
        if (categoryId != null) {
            sql.append(" AND id_category = ?");
        }
        if (brandId != null) {
            sql.append(" AND id_brand = ?");
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            int index = 1;

            if (keyword != null && !keyword.trim().isEmpty()) {
                ps.setString(index++, "%" + keyword + "%");
            }
            if (categoryId != null) {
                ps.setInt(index++, categoryId);
            }
            if (brandId != null) {
                ps.setInt(index++, brandId);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(map(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===== INSERT =====
    public void insert(Product p) {
        String sql = """
            INSERT INTO PRODUCTS
            (name_product, description, price, stock, id_category, id_brand, image_path)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            ps.setDouble(3, p.getPrice());
            ps.setInt(4, p.getStock());
            ps.setInt(5, p.getCategoryId());
            ps.setInt(6, p.getBrandId());
            ps.setString(7, p.getImagePath());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== UPDATE =====
    public void update(Product p) {
        String sql = """
            UPDATE PRODUCTS
            SET name_product=?, description=?, price=?, stock=?,
                id_category=?, id_brand=?, image_path=?
            WHERE id_product=?
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getName());
            ps.setString(2, p.getDescription());
            ps.setDouble(3, p.getPrice());
            ps.setInt(4, p.getStock());
            ps.setInt(5, p.getCategoryId());
            ps.setInt(6, p.getBrandId());
            ps.setString(7, p.getImagePath());
            ps.setInt(8, p.getId());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== DELETE =====
    public void delete(int id) {
        String sql = "DELETE FROM PRODUCTS WHERE id_product = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== UPDATE STOCK =====
    public void updateStock(int productId, int qty) {
        String sql = "UPDATE PRODUCTS SET stock = stock - ? WHERE id_product = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, qty);
            ps.setInt(2, productId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== MAP =====
    private Product map(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id_product"));
        p.setName(rs.getString("name_product"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getDouble("price"));
        p.setStock(rs.getInt("stock"));
        p.setCategoryId(rs.getInt("id_category"));
        p.setBrandId(rs.getInt("id_brand"));
        p.setImagePath(rs.getString("image_path")); // ✅ giữ nguyên
        return p;
    }
}
