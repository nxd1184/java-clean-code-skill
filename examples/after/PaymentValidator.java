package examples.after;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Pure query — never charges, never writes. Returns a structured result
 * a caller can inspect. Trivially unit-testable, no transaction needed.
 *
 * Rules: 5 (CQS — answers, doesn't act), 8 (no hidden side effects).
 */
@Service
public class PaymentValidator {

    private static final BigDecimal SUSPICIOUS_THRESHOLD = new BigDecimal("10000");

    public PaymentValidationResult validate(BigDecimal amount, String cardToken) {
        List<String> reasons = new ArrayList<>();

        if (amount == null || amount.signum() <= 0) {
            reasons.add("amount must be positive");
        }
        if (cardToken == null || cardToken.isBlank()) {
            reasons.add("cardToken required");
        }
        if (amount != null && amount.compareTo(SUSPICIOUS_THRESHOLD) > 0) {
            reasons.add("amount exceeds suspicious-activity threshold");
        }

        return reasons.isEmpty()
            ? PaymentValidationResult.ok()
            : new PaymentValidationResult(false, reasons);
    }
}
