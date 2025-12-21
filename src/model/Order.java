package model;

import java.util.Date;

public class Order {

    private int id;
    private Date createdAt;
    private double total;
    private int adminId;
    private int customerId;

    public Order() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {           // id_order
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) { // createdAt
        this.createdAt = createdAt;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {  // total
        this.total = total;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) { // id_admin
        this.adminId = adminId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) { // id_customer
        this.customerId = customerId;
    }
}
