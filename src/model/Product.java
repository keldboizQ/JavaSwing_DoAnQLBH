package model;

public class Product {

    private int id;
    private String name;
    private String description;
    private double price;
    private int stock;
    private int categoryId;
    private int brandId;

    public Product() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {            // id_product
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {     // name_product
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) { // description
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {   // price
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {       // stock
        this.stock = stock;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) { // id_category
        this.categoryId = categoryId;
    }

    public int getBrandId() {
        return brandId;
    }

    public void setBrandId(int brandId) {   // id_brand
        this.brandId = brandId;
    }
    
    @Override
    public String toString() {
        return name;  // hiển thị tên sản phẩm trên combobox
    }

}
	