package model;

public class Role {

    private int id;
    private String name;

    public Role() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {        // id_role
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) { // name_role
        this.name = name;
    }
}
