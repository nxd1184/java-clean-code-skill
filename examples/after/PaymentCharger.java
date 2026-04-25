package examples.after;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Pure command — assumes the payment was already validated by
 * PaymentValidator. Single responsibility: move money + record it.
 *
 * Rules: 5 (CQS — does, doesn't answer), 8 (side effects are
 * obvious from the method name).
 */
@Service
public class PaymentCharger {

    private final PaymentGateway gateway;
    private final ChargeHistoryRepository history;

    public PaymentCharger(PaymentGateway gateway, ChargeHistoryRepository history) {
        this.gateway = gateway;
        this.history = history;
    }

    @Transactional
    public void charge(Long orderId, BigDecimal amount, String cardToken) {
        gateway.charge(cardToken, amount);
        history.record(orderId, amount);
    }

    interface PaymentGateway {
        void charge(String cardToken, BigDecimal amount);
    }

    interface ChargeHistoryRepository {
        void record(Long orderId, BigDecimal amount);
    }
}
