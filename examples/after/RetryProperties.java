package examples.after;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Typed configuration — every value has a name, a unit (Duration), a
 * default, and is overridable in application.yml. No more "what does
 * 30000 mean?".
 *
 * Rule 17 (no magic numbers).
 */
@ConfigurationProperties(prefix = "orders.retry")
public record RetryProperties(
        Duration scanInterval,
        int maxAttempts,
        Duration initialBackoff
) {
    public RetryProperties {
        if (scanInterval == null)    scanInterval    = Duration.ofDays(1);
        if (maxAttempts <= 0)        maxAttempts     = 5;
        if (initialBackoff == null)  initialBackoff  = Duration.ofSeconds(30);
    }

    public Duration backoffFor(int attemptNumber) {
        return initialBackoff.multipliedBy(attemptNumber);
    }
}
