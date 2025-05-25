import main.java.com.myproject.service.BudgetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class BudgetServiceTest {

    private BudgetService budgetService;

    @BeforeEach
    void setUp() {
        budgetService = new BudgetService();
    }

    @Test
    void shouldReturnFalseWhenBudgetNotSet() {

        boolean hasBudget = budgetService.hasBudget();

        assertThat(hasBudget).isFalse();
    }

    @Test
    void shouldReturnTrueWhenBudgetIsSet() {

        budgetService.setMonthlyBudget(1000.0);

        boolean hasBudget = budgetService.hasBudget();

        assertThat(hasBudget).isTrue();
    }

    @Test
    void shouldReturnFalseWhenBudgetIsZero() {

        budgetService.setMonthlyBudget(0.0);

        boolean hasBudget = budgetService.hasBudget();

        assertThat(hasBudget).isFalse();
    }
}

