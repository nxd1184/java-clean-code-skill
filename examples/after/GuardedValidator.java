package examples.after;

/**
 * Same logic, guard-clause style. Each invalid case fails fast at the
 * top of the method. The "happy path" sits at the bottom, unindented.
 * Reads top-to-bottom: "to be valid, you must NOT be any of these things,
 * then you're ok."
 *
 * Rule 18 (early returns, max 2 levels of nesting). Plain Java.
 */
public class GuardedValidator {

    private static final int MIN_AGE = 18;
    private static final String SANCTIONED_COUNTRY = "OFAC";

    public ValidationResult validate(Account account) {
        if (account == null)                       return fail("account null");
        if (isBlank(account.email()))              return fail("email required");
        if (!account.email().contains("@"))        return fail("email must contain @");
        if (account.age() < MIN_AGE)               return fail("must be 18+");
        if (account.country() == null)             return fail("country required");
        if (SANCTIONED_COUNTRY.equals(account.country())) return fail("country sanctioned");

        return ValidationResult.ok();
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private ValidationResult fail(String reason) {
        return ValidationResult.fail(reason);
    }

    public record Account(String email, int age, String country) {}

    public record ValidationResult(boolean valid, String reason) {
        public static ValidationResult ok() { return new ValidationResult(true, null); }
        public static ValidationResult fail(String reason) { return new ValidationResult(false, reason); }
    }
}
