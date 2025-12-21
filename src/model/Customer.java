package model;

public class Customer {

    private int id;
    private String name;
    private String phone;
    private String address;

    public Customer() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {           // id_customer
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {    // name_customer
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {  // phone
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) { // address
        this.address = address;
    }
}
