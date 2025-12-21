package model;

public class Category {

    private int id;
    private String name;

    public Category() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {          // id_category
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {   // name_category
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name;
    }

}
