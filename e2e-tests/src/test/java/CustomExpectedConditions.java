import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

public class CustomExpectedConditions {
    public static ExpectedCondition<Boolean> textToBeDifferentFrom(final WebElement element, final String initialText) {
        return new ExpectedCondition<>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return !element.getText().equals(initialText);
            }

            @Override
            public String toString() {
                return String.format("text to be different from \"%s\"", initialText);
            }
        };
    }
}
