package main.java.com.myproject.service;

import main.java.com.myproject.model.MonthlyExpense;
import main.java.com.myproject.model.Transaction;
import main.java.com.myproject.model.Transaction.TransactionType;
import main.java.com.myproject.util.CsvUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TransactionService {
    private List<Transaction> transactions;
    private SimpleDateFormat dateFormat;

    public TransactionService() {
        transactions = new ArrayList<>();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public boolean importTransactionsFromFile(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            // Skip header
            line = reader.readLine();

            // Read transactions
            while ((line = reader.readLine()) != null) {
                Transaction transaction = CsvUtil.parseTransactionFromCsvLine(line);
                if (transaction != null) {
                    addTransaction(transaction);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void addManualTransaction(String dateStr, String description, double amount, String category, TransactionType type) {
        try {
            Date date = dateFormat.parse(dateStr);
            Transaction transaction = new Transaction(date, description, amount, category, type);
            addTransaction(transaction);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public int getCurrentMonthTransactionCount() {
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth = cal.get(Calendar.MONTH);

        int count = 0;
        for (Transaction transaction : transactions) {
            cal.setTime(transaction.getDate());
            if (cal.get(Calendar.YEAR) == currentYear &&
                    cal.get(Calendar.MONTH) == currentMonth) {
                count++;
            }
        }
        return count;
    }

    public List<Transaction> getAllTransactions() {
        return new ArrayList<>(transactions);
    }

    public List<MonthlyExpense> getMonthlyExpensesForYear(int year) {
        List<MonthlyExpense> monthlyExpenses = new ArrayList<>();

        // Initialize all 12 months with zero amounts
        for (int month = 0; month < 12; month++) {
            monthlyExpenses.add(new MonthlyExpense(year, month, 0.0));
        }

        // Calculate totals for each month in the specified year
        Calendar cal = Calendar.getInstance();
        for (Transaction transaction : transactions) {
            // Only consider EXPENSE transactions
            if (!transaction.isExpense()) continue;

            cal.setTime(transaction.getDate());
            int transYear = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);

            if (transYear == year) {
                MonthlyExpense monthlyExpense = monthlyExpenses.get(month);
                // Use absolute value since expense amounts are negative
                monthlyExpense.setAmount(monthlyExpense.getAmount() + Math.abs(transaction.getAmount()));
            }
        }

        return monthlyExpenses;
    }
}