package com.a4devspirit.a1.data.Product;


public class CategoryProduct {
    private int id;
    private String categoryname;
    private String categoryphoto;
    private String destnumber;
    private String access;
    private String dishcount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoryname() {
        return categoryname;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }

    public String getCategoryphoto() {
        return categoryphoto;
    }

    public void setCategoryphoto(String categoryphoto) {
        this.categoryphoto = categoryphoto;
    }

    public String getDestnumber() {
        return destnumber;
    }

    public void setDestnumber(String destnumber) {
        this.destnumber = destnumber;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getDishcount() {
        return dishcount;
    }

    public void setDishcount(String dishcount) {
        this.dishcount = dishcount;
    }
}
