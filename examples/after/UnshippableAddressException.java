package examples.after;

public class UnshippableAddressException extends RuntimeException {
    public UnshippableAddressException(String orderId) {
        super("order " + orderId + " has an unshippable address");
    }
}
