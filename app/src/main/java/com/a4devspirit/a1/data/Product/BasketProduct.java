package com.a4devspirit.a1.data.Product;

/**
 * Created by 1 on 02.07.2017.
 */

public class BasketProduct {
    private int id;
    private String name;
    private String price;
    private int count;
    private String destnumber;
    private int sum;
    public BasketProduct(int id, String name, String price, int count, String destnumber){
        this.id = id;
        this.count = count;
        this.name = name;
        this.price = price;
        this.destnumber = destnumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getDestnumber() {
        return destnumber;
    }

    public void setDestnumber(String destnumber) {
        this.destnumber = destnumber;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }
}
