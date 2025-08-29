package com.example.soyyinmanage.models;

public class Batch {
    private String id;
    private String timeOfBatch;
    private int quantity;
    private int initialQuantity;
    private String description;


    public Batch() {
    }

    public Batch(String id, String timeOfBatch, int quantity, int initialQuantity, String description) {
        this.id = id;
        this.timeOfBatch = timeOfBatch;
        this.quantity = quantity;
        this.initialQuantity = initialQuantity;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimeOfBatch() {
        return timeOfBatch;
    }

    public void setTimeOfBatch(String timeOfBatch) {
        this.timeOfBatch = timeOfBatch;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getInitialQuantity() {
        return initialQuantity;
    }

    public void setInitialQuantity(int initialQuantity) {
        this.initialQuantity = initialQuantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
