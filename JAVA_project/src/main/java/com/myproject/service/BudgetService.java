package main.java.com.myproject.service;

import main.java.com.myproject.model.Budget;

import java.util.Calendar;
import java.util.Date;

public class BudgetService {
    private Budget currentBudget;

    public BudgetService() {
        // Initialize with zero budget
        Calendar cal = Calendar.getInstance();
        // Set to first day of current month
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = cal.getTime();

        // Set to last day of current month
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date endDate = cal.getTime();

        currentBudget = new Budget(0.0, startDate, endDate);
    }

    public void setMonthlyBudget(double amount) {
        Calendar cal = Calendar.getInstance();
        // Set to first day of current month
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date startDate = cal.getTime();

        // Set to last day of current month
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date endDate = cal.getTime();

        currentBudget = new Budget(amount, startDate, endDate);
    }

    public Budget getCurrentBudget() {
        return currentBudget;
    }

    public double getBudgetAmount() {
        return currentBudget.getAmount();
    }

    public boolean isOverBudget(double totalSpent) {
        return totalSpent > currentBudget.getAmount() && currentBudget.getAmount() > 0;
    }

    public double getRemainingBudget(double totalSpent) {
        return currentBudget.getAmount() - totalSpent;
    }
}