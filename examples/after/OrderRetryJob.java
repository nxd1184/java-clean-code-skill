package examples.after;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * The job now reads as English. Every quantity is named.
 * Operators tune behavior in application.yml without redeploying.
 */
@Component
public class OrderRetryJob {

    private final RetryProperties retry;
    private final FailedOrderReader reader;
    private final OrderSender sender;

    public OrderRetryJob(RetryProperties retry,
                         FailedOrderReader reader,
                         OrderSender sender) {
        this.retry = retry;
        this.reader = reader;
        this.sender = sender;
    }

    @Scheduled(fixedDelayString = "${orders.retry.scan-interval:PT24H}")
    public void retryFailedOrders() {
        for (Long orderId : reader.findFailedOrderIds()) {
            attemptDelivery(orderId);
        }
    }

    private void attemptDelivery(Long orderId) {
        for (int attempt = 1; attempt <= retry.maxAttempts(); attempt++) {
            if (trySend(orderId, attempt)) return;
        }
    }

    private boolean trySend(Long orderId, int attempt) {
        try {
            sender.send(orderId);
            return true;
        } catch (Exception e) {
            sleepUninterruptibly(retry.backoffFor(attempt));
            return false;
        }
    }

    private void sleepUninterruptibly(java.time.Duration d) {
        try { Thread.sleep(d.toMillis()); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    interface FailedOrderReader {
        java.util.List<Long> findFailedOrderIds();
    }

    interface OrderSender {
        void send(Long id);
    }
}
