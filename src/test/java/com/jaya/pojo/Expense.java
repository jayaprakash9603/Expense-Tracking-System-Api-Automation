package com.jaya.pojo;

/**
 * Example POJO - Expense Request/Response model
 * This is a sample POJO to demonstrate the structure
 */
public class Expense {
    
    private Long id;
    private String description;
    private Double amount;
    private String category;
    private String date;
    private Long userId;
    
    // Constructors
    public Expense() {
    }
    
    public Expense(String description, Double amount, String category, String date, Long userId) {
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.userId = userId;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Double getAmount() {
        return amount;
    }
    
    public void setAmount(Double amount) {
        this.amount = amount;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", category='" + category + '\'' +
                ", date='" + date + '\'' +
                ", userId=" + userId +
                '}';
    }
}
