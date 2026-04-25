package examples.after;

public class OrderService {

    private static final int LARGE_ORDER_THRESHOLD_CENTS = 10_000_00;

    private final OrderRepository orderRepository;
    private final PaymentClient paymentClient;
    private final OrderNotifier orderNotifier;
    private final OrderAuditLogger auditLogger;

    public OrderService(OrderRepository orderRepository,
                        PaymentClient paymentClient,
                        OrderNotifier orderNotifier,
                        OrderAuditLogger auditLogger) {
        this.orderRepository = orderRepository;
        this.paymentClient = paymentClient;
        this.orderNotifier = orderNotifier;
        this.auditLogger = auditLogger;
    }

    public Order placeOrder(Order order) {
        requireShippableZipCode(order);
        Order saved = orderRepository.save(order);
        paymentClient.charge(saved);
        if (saved.totalCents() >= LARGE_ORDER_THRESHOLD_CENTS) {
            auditLogger.logLargeOrder(saved);
        }
        return saved;
    }

    public void notifyCustomer(Order order) {
        orderNotifier.sendConfirmation(order);
    }

    private void requireShippableZipCode(Order order) {
        String zip = order.customerZipCode();
        if (zip == null || zip.length() != 5) {
            throw new UnshippableAddressException(order.id());
        }
    }
}
