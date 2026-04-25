package examples.before;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Demonstrates Rule 17 (no magic numbers).
 * What does 86400000 mean? What's special about 5? About 30000?
 * Reader has to mentally compute or trust the comment is current.
 */
@Component
public class HardcodedRetryJob {

    @Scheduled(fixedDelay = 86400000) // every day
    public void retryFailedOrders() {
        for (Long orderId : findFailedOrderIds()) {
            int attempt = 0;
            while (attempt < 5) {
                try {
                    sendOrder(orderId);
                    break;
                } catch (Exception e) {
                    attempt++;
                    sleep(30000 * attempt); // 30s, 60s, 90s, ...
                }
            }
        }
    }

    private java.util.List<Long> findFailedOrderIds() { return java.util.List.of(); }
    private void sendOrder(Long id) { /* ... */ }
    private void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
