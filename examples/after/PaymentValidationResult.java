package examples.after;

import java.util.List;

/**
 * Result of a pure validation query — explicit reasons when invalid.
 */
public record PaymentValidationResult(boolean valid, List<String> reasons) {

    public static PaymentValidationResult ok() {
        return new PaymentValidationResult(true, List.of());
    }

    public static PaymentValidationResult invalid(String... reasons) {
        return new PaymentValidationResult(false, List.of(reasons));
    }
}
