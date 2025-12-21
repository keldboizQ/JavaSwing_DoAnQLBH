package model;

public class Brand {

    private int id;
    private String name;

    public Brand() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {       // id_brand
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) { // name_brand
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name;
    }

}
