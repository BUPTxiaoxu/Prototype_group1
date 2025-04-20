package main.java.com.myproject.model;

import java.util.Date;

public class Budget {
    private double amount;
    private Date startDate;
    private Date endDate;

    public Budget(double amount, Date startDate, Date endDate) {
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "Budget{" +
                "amount=" + amount +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}