package examples.after;

import java.math.BigDecimal;

/**
 * Replaces Map<String, Object>. Every field is named, typed, and
 * non-null (validated in the compact constructor). Behavior — like
 * "is this a positive amount?" — lives ON the object, hidden from
 * outside.
 *
 * Rule 1 (names), Rule 11 (objects expose behavior, hide data).
 */
public record AccountTransfer(String fromAccountId,
                              String toAccountId,
                              BigDecimal amount) {

    public AccountTransfer {
        if (fromAccountId == null || fromAccountId.isBlank())
            throw new IllegalArgumentException("fromAccountId required");
        if (toAccountId == null || toAccountId.isBlank())
            throw new IllegalArgumentException("toAccountId required");
        if (amount == null || amount.signum() <= 0)
            throw new IllegalArgumentException("amount must be positive");
        if (fromAccountId.equals(toAccountId))
            throw new IllegalArgumentException("cannot transfer to same account");
    }

    public boolean isLargeTransfer(BigDecimal threshold) {
        return amount.compareTo(threshold) > 0;
    }
}
