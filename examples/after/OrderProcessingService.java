package examples.after;

import org.springframework.stereotype.Service;

/**
 * After: split by intent. Two named entry points for shipping speed,
 * a typed policy object for notifications, and audit always runs (a
 * boolean flag for "should I audit?" is a code smell — audit always).
 *
 * Rules: 4 (no flag args), 12 (SRP — each method does one thing).
 */
@Service
public class OrderProcessingService {

    private final OrderShipper shipper;
    private final OrderNotifier notifier;
    private final OrderAuditLogger auditLogger;

    public OrderProcessingService(OrderShipper shipper,
                                  OrderNotifier notifier,
                                  OrderAuditLogger auditLogger) {
        this.shipper = shipper;
        this.notifier = notifier;
        this.auditLogger = auditLogger;
    }

    public void processStandardOrder(Long orderId, NotificationPolicy notify) {
        shipper.shipStandard(orderId);
        notifier.notify(orderId, notify);
        auditLogger.recordShipment(orderId, "STANDARD");
    }

    public void processExpeditedOrder(Long orderId, NotificationPolicy notify) {
        shipper.shipExpedited(orderId);
        notifier.notify(orderId, notify);
        auditLogger.recordShipment(orderId, "EXPEDITED");
    }

    // Call sites become readable:
    //   service.processStandardOrder(42L, NotificationPolicy.EMAIL_AND_SMS);
    //   service.processExpeditedOrder(99L, NotificationPolicy.NONE);

    interface OrderShipper {
        void shipStandard(Long id);
        void shipExpedited(Long id);
    }

    interface OrderNotifier {
        void notify(Long id, NotificationPolicy policy);
    }

    interface OrderAuditLogger {
        void recordShipment(Long id, String shipmentType);
    }
}
