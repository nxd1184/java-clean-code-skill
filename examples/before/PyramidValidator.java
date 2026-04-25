package examples.before;

/**
 * Demonstrates Rule 18 (early returns; max 2 levels of nesting).
 * The "happy path" is buried 4 levels deep. Every validation reads as
 * "we're successful IF all of these..." instead of "fail fast on the
 * first thing wrong." Plain Java — no framework.
 */
public class PyramidValidator {

    public ValidationResult validate(Account account) {
        if (account != null) {
            if (account.email() != null && !account.email().isBlank()) {
                if (account.email().contains("@")) {
                    if (account.age() >= 18) {
                        if (account.country() != null && !"OFAC".equals(account.country())) {
                            return ValidationResult.ok();
                        } else {
                            return ValidationResult.fail("country missing or sanctioned");
                        }
                    } else {
                        return ValidationResult.fail("must be 18+");
                    }
                } else {
                    return ValidationResult.fail("email must contain @");
                }
            } else {
                return ValidationResult.fail("email required");
            }
        } else {
            return ValidationResult.fail("account null");
        }
    }

    public record Account(String email, int age, String country) {}

    public record ValidationResult(boolean valid, String reason) {
        public static ValidationResult ok() { return new ValidationResult(true, null); }
        public static ValidationResult fail(String reason) { return new ValidationResult(false, reason); }
    }
}
