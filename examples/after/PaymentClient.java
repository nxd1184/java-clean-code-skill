package examples.after;

public interface PaymentClient {
    void charge(Order order); // throws PaymentFailedException on non-OK response
}
