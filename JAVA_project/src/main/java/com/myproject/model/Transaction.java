package main.java.com.myproject.model;

import java.util.Date;

public class Transaction {
    private Date date;
    private String description;
    private double amount;
    private String category;
    private TransactionType type;

    public enum TransactionType {
        EXPENSE,
        INCOME
    }

    public Transaction(Date date, String description, double amount) {
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.type = amount < 0 ? TransactionType.EXPENSE : TransactionType.INCOME;
    }

    public Transaction(Date date, String description, double amount, String category) {
        this.date = date;
        this.description = description;
        this.amount = amount;
        this.category = category;
        this.type = amount < 0 ? TransactionType.EXPENSE : TransactionType.INCOME;
    }

    public Transaction(Date date, String description, double amount, String category, TransactionType type) {
        this.date = date;
        this.description = description;
        this.amount = Math.abs(amount) * (type == TransactionType.EXPENSE ? -1 : 1);
        this.category = category;
        this.type = type;
    }

    // Getters and Setters
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
        // Update type based on amount sign
        this.type = amount < 0 ? TransactionType.EXPENSE : TransactionType.INCOME;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
        // Ensure amount sign matches type
        if ((type == TransactionType.EXPENSE && amount > 0) ||
                (type == TransactionType.INCOME && amount < 0)) {
            this.amount = -this.amount;
        }
    }

    public boolean isExpense() {
        return type == TransactionType.EXPENSE;
    }

    public boolean isIncome() {
        return type == TransactionType.INCOME;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "date=" + date +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", category='" + category + '\'' +
                ", type=" + type +
                '}';
    }
}