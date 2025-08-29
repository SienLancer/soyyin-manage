package com.example.soyyinmanage.models;

public class Tofu {
    private String id;
    private int quantity;
    private String date;
    private String typeOfSale;
    private String comment;
    private String batch_id;

    public Tofu(String id, int quantity, String date, String typeOfSale, String comment, String batch_id) {
        this.id = id;
        this.quantity = quantity;
        this.date = date;
        this.typeOfSale = typeOfSale;
        this.comment = comment;
        this.batch_id = batch_id;
    }

    public Tofu() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTypeOfSale() {
        return typeOfSale;
    }

    public void setTypeOfSale(String typeOfSale) {
        this.typeOfSale = typeOfSale;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getBatch_id() {
        return batch_id;
    }

    public void setBatch_id(String batch_id) {
        this.batch_id = batch_id;
    }
}
