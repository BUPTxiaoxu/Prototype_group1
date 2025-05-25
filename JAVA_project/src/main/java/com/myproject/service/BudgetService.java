package main.java.com.myproject.service;

import main.java.com.myproject.model.Budget;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 预算服务 - 提供预算相关功能
 */
public class BudgetService {

    private Budget currentBudget;

    // 分类预算存储
    private Map<String, Double> categoryBudgets;

    /**
     * 构造函数
     */
    public BudgetService() {
        // Initialize with zero budget
        Calendar cal = Calendar.getInstance();
        // Set to first day of current month
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startDate = cal.getTime();

        // Set to last day of current month
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Date endDate = cal.getTime();

        currentBudget = new Budget(0.0, startDate, endDate);

        // 初始化分类预算Map
        categoryBudgets = new HashMap<>();
    }

    public boolean hasBudget() {
        return getBudgetAmount() > 0;
    }


    public void setMonthlyBudget(double amount) {
        Calendar cal = Calendar.getInstance();
        // Set to first day of current month
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date startDate = cal.getTime();

        // Set to last day of current month
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        Date endDate = cal.getTime();

        currentBudget = new Budget(amount, startDate, endDate);

        // 添加调试输出
        System.out.println("Budget set to: ¥" + amount);
        System.out.println("Current budget amount: ¥" + getBudgetAmount());
    }

    /**
     * 获取当前预算对象
     * @return 预算对象
     */
    public Budget getCurrentBudget() {
        return currentBudget;
    }

    /**
     * 获取当前预算金额
     * @return 预算金额
     */
    public double getBudgetAmount() {
        if (currentBudget == null) {
            return 0.0;
        }
        return currentBudget.getAmount();
    }

    /**
     * 判断是否超出预算
     * @param totalSpent 总支出
     * @return 是否超出预算
     */
    public boolean isOverBudget(double totalSpent) {
        return totalSpent > getBudgetAmount() && getBudgetAmount() > 0;
    }

    /**
     * 获取剩余预算
     * @param totalSpent 总支出
     * @return 剩余预算
     */
    public double getRemainingBudget(double totalSpent) {
        return getBudgetAmount() - totalSpent;
    }

    /**
     * 打印当前预算状态
     */
    public void printBudgetStatus() {
        System.out.println("Current budget status:");
        System.out.println("Budget: ¥" + getBudgetAmount());
        System.out.println("Start date: " + currentBudget.getStartDate());
        System.out.println("End date: " + currentBudget.getEndDate());
    }

    //=== 分类预算方法 ===//

    /**
     * 设置分类预算
     * @param category 分类名称
     * @param amount 预算金额
     */
    public void setCategoryBudget(String category, double amount) {
        categoryBudgets.put(category, amount);
    }

    /**
     * 获取分类预算
     * @param category 分类名称
     * @return 预算金额，如果没有设置则返回0
     */
    public double getCategoryBudget(String category) {
        return categoryBudgets.getOrDefault(category, 0.0);
    }

    /**
     * 检查分类是否有预算
     * @param category 分类名称
     * @return 是否有预算
     */
    public boolean hasCategoryBudget(String category) {
        return categoryBudgets.containsKey(category);
    }

    /**
     * 移除分类预算
     * @param category 分类名称
     */
    public void removeCategoryBudget(String category) {
        categoryBudgets.remove(category);
    }

    /**
     * 获取所有分类预算
     * @return 分类预算Map
     */
    public Map<String, Double> getAllCategoryBudgets() {
        return new HashMap<>(categoryBudgets);
    }
}
