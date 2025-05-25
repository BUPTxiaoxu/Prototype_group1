package main.java.com.myproject.service;

import main.java.com.myproject.model.Transaction;
import main.java.com.myproject.util.StreamingUtil;
import main.java.com.myproject.util.StreamingUtil.StreamCallback;

import javax.swing.*;
import java.io.*;
import java.util.Calendar;
import java.util.Map;

public class AIService {
    private TransactionService transactionService;
    private BudgetService budgetService;
    private StatisticService statisticService;

    // DeepSeek API 配置
    private static final String API_ENDPOINT = "https://api.deepseek.com/v1/chat/completions";
    private static final String MODEL = "deepseek-coder";
    private static final String API_KEY = "sk-0139a7ea12f94732bb4e6227adc64a70";

    // 调试模式开关
    private static final boolean DEBUG_MODE = true;

    public AIService(TransactionService transactionService, BudgetService budgetService, StatisticService statisticService) {
        this.transactionService = transactionService;
        this.budgetService = budgetService;
        this.statisticService = statisticService;
    }

    /**
     * 获取AI财务建议
     */
    public String getFinancialAdvice() {
        try {
            String prompt = buildFinancialAdvicePrompt();

            // 调用 DeepSeek API
            return processQuery(prompt);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred while getting AI advice: " + e.getMessage();
        }
    }

    /**
     * 获取流式财务建议
     * @param callback 流式回调接口
     */
    public void getStreamingFinancialAdvice(StreamCallback callback) {
        try {
            String prompt = buildFinancialAdvicePrompt();

            // 调用流式 API
            processStreamingQuery(prompt, callback);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onError("Error occurred while getting AI advice: " + e.getMessage());
        }
    }

    /**
     * 构建财务建议的提示语
     */
    private String buildFinancialAdvicePrompt() {
        // Get current month financial statistics
        StatisticService.CategoryStatistics stats = statisticService.getCurrentMonthStatistics();

        // 检查预算设置状态
        boolean hasBudget = stats.getBudget() > 0;
        StringBuilder dataBuilder = new StringBuilder();

        if (!hasBudget) {
            dataBuilder.append("IMPORTANT NOTE: No monthly budget has been set.\n\n");
        }

        // Build financial data summary
        dataBuilder.append("Current Month Financial Analysis:\n");
        dataBuilder.append("Total Income: ¥").append(String.format("%.2f", stats.getTotalIncome())).append("\n");
        dataBuilder.append("Total Expenses: ¥").append(String.format("%.2f", stats.getTotalExpenses())).append("\n");

        if (hasBudget) {
            dataBuilder.append("Budget: ¥").append(String.format("%.2f", stats.getBudget())).append("\n");
            dataBuilder.append("Remaining Budget: ¥").append(String.format("%.2f", stats.getRemaining())).append("\n");
            dataBuilder.append("Budget Usage: ").append(String.format("%.1f", (stats.getTotalExpenses() / stats.getBudget()) * 100)).append("%\n\n");
        } else {
            dataBuilder.append("Budget: Not set\n");
            dataBuilder.append("Note: Without a budget, it's difficult to provide accurate financial advice.\n\n");
        }

        // Add expense category details
        dataBuilder.append("Expense Categories:\n");
        for (Map.Entry<String, Double> entry : stats.getExpenseByCategory().entrySet()) {
            if (entry.getValue() > 0) {
                dataBuilder.append("- ").append(entry.getKey()).append(": ¥")
                        .append(String.format("%.2f", entry.getValue()));
                if (hasBudget) {
                    double percentage = (entry.getValue() / stats.getTotalExpenses()) * 100;
                    dataBuilder.append(" (").append(String.format("%.1f", percentage)).append("%)");
                }
                dataBuilder.append("\n");
            }
        }

        dataBuilder.append("\nIncome Sources:\n");
        for (Map.Entry<String, Double> entry : stats.getIncomeByCategory().entrySet()) {
            if (entry.getValue() > 0) {
                dataBuilder.append("- ").append(entry.getKey()).append(": ¥")
                        .append(String.format("%.2f", entry.getValue())).append("\n");
            }
        }

        // Build prompt for DeepSeek API
        String prompt;
        if (hasBudget) {
            prompt = "As a professional financial advisor, please provide practical financial advice based on the following data:\n\n" +
                    dataBuilder.toString() +
                    "\nPlease provide specific recommendations on:\n" +
                    "1. Budget Management: Analyze current budget usage and suggest improvements\n" +
                    "2. Expense Optimization: Identify areas for cost reduction\n" +
                    "3. Savings Plan: Suggest a reasonable savings strategy\n" +
                    "4. Investment Recommendations: Advise on investments based on financial status\n" +
                    "Please respond in English with concise and practical advice.";
        } else {
            prompt = "As a professional financial advisor, please provide practical financial advice based on the following data:\n\n" +
                    dataBuilder.toString() +
                    "\nNOTE: The user has not set a monthly budget. Please provide advice on:\n" +
                    "1. Establishing a budget framework\n" +
                    "2. Expense analysis and optimization\n" +
                    "3. Basic savings strategies\n" +
                    "4. General financial health recommendations\n" +
                    "Please respond in English with concise and practical advice, emphasizing the importance of budget planning.";
        }

        return prompt;
    }

    /**
     * 处理普通查询，返回完整响应
     */
    public String processQuery(String userMessage) {
        try {
            String requestBody = buildRequestBody(userMessage, false);

            if (DEBUG_MODE) {
                System.out.println("发送请求到: " + API_ENDPOINT);
                System.out.println("请求体: " + requestBody);
            }

            String responseJson = StreamingUtil.sendRequest(API_ENDPOINT, API_KEY, requestBody);

            if (DEBUG_MODE) {
                System.out.println("响应内容: " + responseJson);
            }

            String content = StreamingUtil.parseResponseContent(responseJson);
            if (content != null && !content.isEmpty()) {
                return content;
            } else {
                return getSampleAdvice();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return getSampleAdvice();
        }
    }

    /**
     * 处理流式查询，使用回调接口实时返回响应
     */
    public void processStreamingQuery(String userMessage, StreamCallback callback) {
        try {
            String requestBody = buildRequestBody(userMessage, true);

            if (DEBUG_MODE) {
                System.out.println("发送流式请求到: " + API_ENDPOINT);
                System.out.println("请求体: " + requestBody);
            }

            // 使用StreamingUtil发送流式请求
            StreamingUtil.sendStreamingRequest(API_ENDPOINT, API_KEY, requestBody, callback);

        } catch (Exception e) {
            e.printStackTrace();
            callback.onError("Error initiating API request: " + e.getMessage());
        }
    }

    /**
     * 构建请求JSON
     */
    private String buildRequestBody(String userMessage, boolean stream) {
        StringBuilder requestJson = new StringBuilder();
        requestJson.append("{");
        requestJson.append("\"model\":\"").append(MODEL).append("\",");
        requestJson.append("\"messages\":[");

        // 系统消息
        requestJson.append("{\"role\":\"system\",\"content\":\"You are a professional financial advisor who specializes in providing practical financial advice.\"},");

        // 用户消息
        requestJson.append("{\"role\":\"user\",\"content\":\"").append(StreamingUtil.escapeJsonString(userMessage)).append("\"}");

        requestJson.append("],");
        requestJson.append("\"temperature\":0.7,");
        requestJson.append("\"max_tokens\":2048,");
        requestJson.append("\"stream\":").append(stream);
        requestJson.append("}");

        return requestJson.toString();
    }

    // Fallback method: return sample AI advice
    public String getSampleAdvice() {
        StatisticService.CategoryStatistics stats = statisticService.getCurrentMonthStatistics();
        StringBuilder advice = new StringBuilder();

        advice.append("📊 Based on your financial data, here are my recommendations:\n\n");

        // 检查预算设置状态
        if (stats.getBudget() <= 0) {
            advice.append("❗ **Important**: You haven't set a monthly budget yet. This is crucial for effective financial management.\n\n");
            advice.append("**Recommended Budget Setup:**\n");
            advice.append("1. Set a realistic monthly budget based on your income\n");
            advice.append("2. Allocate 50% for needs, 30% for wants, and 20% for savings (50/30/20 rule)\n");
            advice.append("3. Review and adjust monthly\n\n");
        }

        // Budget assessment
        advice.append("🏦 **Budget Analysis**\n");
        if (stats.getBudget() > 0) {
            if (stats.isOverBudget()) {
                advice.append("⚠️ You've exceeded this month's budget by ¥").append(String.format("%.2f", Math.abs(stats.getRemaining())))
                        .append(". Please reduce unnecessary expenses immediately.\n");
            } else if (stats.isNearBudgetLimit()) {
                advice.append("⚡ You've used most of your budget, with only ¥").append(String.format("%.2f", stats.getRemaining()))
                        .append(" remaining. Please plan your remaining expenses carefully.\n");
            } else {
                advice.append("✅ Good budget control with ¥").append(String.format("%.2f", stats.getRemaining()))
                        .append(" remaining. Keep it up!\n");
            }
        } else {
            advice.append("• No budget set. Consider setting a monthly budget to track spending.\n");
        }

        // Expense analysis
        advice.append("\n📈 **Expense Analysis**\n");
        double totalExpenses = stats.getTotalExpenses();
        if (totalExpenses > 0) {
            for (Map.Entry<String, Double> entry : stats.getExpenseByCategory().entrySet()) {
                double amount = entry.getValue();
                if (amount > 0) {
                    double percentage = (amount / totalExpenses) * 100;
                    if (percentage > 30) {
                        advice.append("• ").append(entry.getKey()).append(" is taking up too much (")
                                .append(String.format("%.1f", percentage)).append("%) of your budget. Consider reducing it.\n");
                    } else if (percentage > 20) {
                        advice.append("• ").append(entry.getKey()).append(" proportion is reasonable (")
                                .append(String.format("%.1f", percentage)).append("%).\n");
                    }
                }
            }
        }

        // Savings recommendation
        advice.append("\n💰 **Savings Plan**\n");
        double remaining = stats.getRemaining();
        if (remaining > 0) {
            double suggestedSaving = remaining * 0.4;
            advice.append("• Consider saving 40% of your remaining budget (about ¥").append(String.format("%.2f", suggestedSaving))
                    .append(") into a savings account.\n");
            advice.append("• Set a mid-term savings goal, such as a 6-month emergency fund.\n");
        } else {
            advice.append("• Your current situation shows negative remaining budget. Focus on expense reduction first.\n");
        }

        // Investment advice
        if (stats.getTotalIncome() > stats.getTotalExpenses() * 1.3) {
            advice.append("\n📈 **Investment Recommendations**\n");
            advice.append("• Your income situation is good. Consider moderate investments.\n");
            advice.append("• First establish an emergency fund, then consider low-risk investments like index funds.\n");
            advice.append("• Limit investments to no more than 50% of your monthly surplus.\n");
        }

        advice.append("\n---\n");
        advice.append("💡 Note: The above advice is for reference only. Please adjust according to your personal situation.");

        return advice.toString();
    }
}