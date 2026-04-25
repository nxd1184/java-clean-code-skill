package examples.before;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Demonstrates Rule 5 (CQS) and Rule 8 (no hidden side effects).
 * `validatePayment` reads like a query — its name promises "answer if
 * this is valid". But it also charges the card. Callers can't trust
 * the name. Worse: it's @Transactional, so a failed downstream call
 * silently rolls back the charge.
 */
@Service
public class MixedConcernsPaymentService {

    @Transactional
    public boolean validatePayment(Long orderId, BigDecimal amount, String cardToken) {
        if (amount == null || amount.signum() <= 0) {
            return false;
        }
        if (cardToken == null || cardToken.isBlank()) {
            return false;
        }
        if (isSuspiciouslyHigh(amount)) {
            return false;
        }

        // ↓↓↓ side effect hidden inside a "validate" method ↓↓↓
        chargeGateway(cardToken, amount);
        recordChargeHistory(orderId, amount);
        return true;
    }

    private boolean isSuspiciouslyHigh(BigDecimal amount) { return false; }
    private void chargeGateway(String token, BigDecimal amount) { /* ... */ }
    private void recordChargeHistory(Long orderId, BigDecimal amount) { /* ... */ }
}
