package examples.before;

import org.springframework.stereotype.Service;

/**
 * Demonstrates Rule 4 (no flag arguments) — every call site has to remember
 * what `true, false, true, false` means and pass them in the right order.
 * Also Rule 12 (SRP): this method handles fulfillment + notification + audit.
 */
@Service
public class BooleanFlagOrderService {

    public void processOrder(Long orderId,
                             boolean isExpedited,
                             boolean sendEmail,
                             boolean sendSms,
                             boolean writeAuditLog) {
        // ... loads order, ignored for brevity ...

        if (isExpedited) {
            shipExpedited(orderId);
        } else {
            shipStandard(orderId);
        }

        if (sendEmail) {
            emailCustomer(orderId);
        }
        if (sendSms) {
            smsCustomer(orderId);
        }

        if (writeAuditLog) {
            audit(orderId, isExpedited, sendEmail, sendSms);
        }
    }

    // Call sites end up looking like this — unreadable:
    //   service.processOrder(42L, true, true, false, true);
    //   service.processOrder(99L, false, false, false, true);

    private void shipExpedited(Long id) { /* ... */ }
    private void shipStandard(Long id)  { /* ... */ }
    private void emailCustomer(Long id) { /* ... */ }
    private void smsCustomer(Long id)   { /* ... */ }
    private void audit(Long id, boolean a, boolean b, boolean c) { /* ... */ }
}
