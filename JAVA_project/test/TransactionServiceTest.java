import main.java.com.myproject.model.Transaction;
import main.java.com.myproject.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Calendar;
import java.util.Date;
import static org.assertj.core.api.Assertions.*;

class TransactionServiceTest {

    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService();
    }

    @Test
    void shouldReturnZeroWhenNoTransactions() {

        int count = transactionService.getCurrentMonthTransactionCount();

        assertThat(count).isEqualTo(0);
    }

    @Test
    void shouldCountOnlyCurrentMonthTransactions() {

        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        Date lastMonth = cal.getTime();

        transactionService.addTransaction(
                new Transaction(today, "Current month", -100.0, "Shopping")
        );
        transactionService.addTransaction(
                new Transaction(today, "Another current", -200.0, "Food")
        );
        transactionService.addTransaction(
                new Transaction(lastMonth, "Last month", -150.0, "Other")
        );

        int count = transactionService.getCurrentMonthTransactionCount();

        assertThat(count).isEqualTo(2);
    }

    @Test
    void shouldCountBothIncomeAndExpense() {

        Date today = new Date();
        transactionService.addTransaction(
                new Transaction(today, "Expense", -100.0, "Shopping")
        );
        transactionService.addTransaction(
                new Transaction(today, "Income", 500.0, "Salary")
        );

        int count = transactionService.getCurrentMonthTransactionCount();

        assertThat(count).isEqualTo(2);
    }
}