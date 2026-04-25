package examples.after;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * After: comments removed unless they carry information code can't.
 * Method names + types tell the WHAT. The one surviving comment
 * explains a non-obvious WHY (the AtomicInteger choice).
 *
 * Rule 20 (comments earn their keep — explain WHY, not WHAT).
 */
public class Counter {

    // AtomicInteger (not int) because callers from multiple request
    // threads in our REST controller increment concurrently — see
    // ADR-014 for the alternatives we ruled out.
    private final AtomicInteger value = new AtomicInteger(0);

    public int incrementBy(List<String> items) {
        return value.addAndGet(items.size());
    }

    public void reset() {
        value.set(0);
    }

    public int getValue() {
        return value.get();
    }
}
