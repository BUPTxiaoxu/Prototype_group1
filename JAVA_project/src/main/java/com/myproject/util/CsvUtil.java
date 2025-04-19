//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package main.java.com.myproject.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import main.java.com.myproject.model.Transaction;
import main.java.com.myproject.model.Transaction.TransactionType;

public class CsvUtil {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public CsvUtil() {
    }

    public static void exportTransactionsToCSV(List<Transaction> transactions, File file) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));

        try {
            writer.write("Date,Type,Description,Amount,Category");
            writer.newLine();
            Iterator var3 = transactions.iterator();

            while(var3.hasNext()) {
                Transaction transaction = (Transaction)var3.next();
                StringBuilder line = new StringBuilder();
                line.append(DATE_FORMAT.format(transaction.getDate())).append(",");
                line.append(transaction.isExpense() ? "Expense" : "Income").append(",");
                line.append(escapeField(transaction.getDescription())).append(",");
                line.append(String.format("%.2f", Math.abs(transaction.getAmount()))).append(",");
                line.append(escapeField(transaction.getCategory() != null ? transaction.getCategory() : ""));
                writer.write(line.toString());
                writer.newLine();
            }
        } catch (Throwable var7) {
            try {
                writer.close();
            } catch (Throwable var6) {
                var7.addSuppressed(var6);
            }

            throw var7;
        }

        writer.close();
    }

    public static Transaction parseTransactionFromCsvLine(String line) {
        String[] parts = line.split(",");
        if (parts.length < 4) {
            return null;
        } else {
            try {
                String dateStr = parts[0].trim();
                String typeStr = parts[1].trim();
                String description = parts[2].trim();
                String amountStr = parts[3].trim();
                String category = parts.length > 4 ? parts[4].trim() : "Other";
                Date date = DATE_FORMAT.parse(dateStr);
                Transaction.TransactionType type = typeStr.equalsIgnoreCase("Expense") ? TransactionType.EXPENSE : TransactionType.INCOME;
                double amount = Double.parseDouble(amountStr);
                if (type == TransactionType.EXPENSE) {
                    amount = -amount;
                }

                return new Transaction(date, description, amount, category, type);
            } catch (NumberFormatException | ParseException var11) {
                return null;
            }
        }
    }

    private static String escapeField(String field) {
        if (field == null) {
            return "";
        } else {
            return !field.contains(",") && !field.contains("\"") && !field.contains("\n") ? field : "\"" + field.replace("\"", "\"\"") + "\"";
        }
    }
}