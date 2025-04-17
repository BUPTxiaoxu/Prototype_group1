//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package main.java.com.myproject.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import main.java.com.myproject.model.Transaction;

public class AssetsService {
    private TransactionService transactionService;

    public AssetsService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public double getMonthlyIncome(int year, int month) {
        double total = 0.0;
        List<Transaction> transactions = this.transactionService.getAllTransactions();
        Calendar cal = Calendar.getInstance();
        Iterator var7 = transactions.iterator();

        while(var7.hasNext()) {
            Transaction transaction = (Transaction)var7.next();
            cal.setTime(transaction.getDate());
            int transYear = cal.get(1);
            int transMonth = cal.get(2);
            if (transYear == year && transMonth == month && transaction.isIncome()) {
                total += transaction.getAmount();
            }
        }

        return total;
    }

    public double getMonthlyExpenses(int year, int month) {
        double total = 0.0;
        List<Transaction> transactions = this.transactionService.getAllTransactions();
        Calendar cal = Calendar.getInstance();
        Iterator var7 = transactions.iterator();

        while(var7.hasNext()) {
            Transaction transaction = (Transaction)var7.next();
            cal.setTime(transaction.getDate());
            int transYear = cal.get(1);
            int transMonth = cal.get(2);
            if (transYear == year && transMonth == month && transaction.isExpense()) {
                total += Math.abs(transaction.getAmount());
            }
        }

        return total;
    }

    public double calculateTotalAssets() {
        double total = 0.0;
        List<Transaction> transactions = this.transactionService.getAllTransactions();
        Iterator var4 = transactions.iterator();

        while(var4.hasNext()) {
            Transaction transaction = (Transaction)var4.next();
            if (transaction.isIncome()) {
                total += transaction.getAmount();
            } else if (transaction.isExpense()) {
                total -= Math.abs(transaction.getAmount());
            }
        }

        return Math.max(0.0, total);
    }

    public List<MonthlyAssetData> getMonthlyAssetData() {
        List<MonthlyAssetData> result = new ArrayList();
        Calendar currentCal = Calendar.getInstance();
        int currentYear = currentCal.get(1);
        int currentMonth = currentCal.get(2);
        double runningAssetValue = 0.0;

        for(int i = 11; i >= 0; --i) {
            int month = currentMonth - i;
            int year = currentYear;
            if (month < 0) {
                month += 12;
                --year;
            }

            double monthlyIncome = this.getMonthlyIncome(year, month);
            double monthlyExpenses = this.getMonthlyExpenses(year, month);
            double monthlyNet = monthlyIncome - monthlyExpenses;
            runningAssetValue += monthlyNet;
            if (runningAssetValue < 0.0) {
                runningAssetValue = 0.0;
            }

            result.add(new MonthlyAssetData(year, month, runningAssetValue));
        }

        return result;
    }

    public static class MonthlyAssetData {
        private int year;
        private int month;
        private double assetValue;

        public MonthlyAssetData(int year, int month, double assetValue) {
            this.year = year;
            this.month = month;
            this.assetValue = assetValue;
        }

        public int getYear() {
            return this.year;
        }

        public int getMonth() {
            return this.month;
        }

        public double getAssetValue() {
            return this.assetValue;
        }
    }
}