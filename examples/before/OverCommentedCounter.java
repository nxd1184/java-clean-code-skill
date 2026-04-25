package examples.before;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Demonstrates Rule 20 (comments earn their keep).
 * Every line has a comment restating the code in English. The comments
 * teach nothing the code doesn't already say — and they go stale the
 * moment behavior changes. Worse: real intent (the WHY) is buried.
 */
public class OverCommentedCounter {

    // The counter, used to count things.
    private final AtomicInteger counter = new AtomicInteger(0);

    /**
     * Increments the counter and returns the new value.
     * @param items the list of items
     * @return the new counter value
     */
    public int incrementBy(List<String> items) {
        // Get the size of the list
        int size = items.size();
        // Add the size to the counter
        int newValue = counter.addAndGet(size);
        // Return the new value
        return newValue;
    }

    /**
     * Resets the counter to zero.
     */
    public void reset() {
        // Set the counter to zero
        counter.set(0);
    }

    /**
     * Gets the current value of the counter.
     * @return the current counter value
     */
    public int getValue() {
        // Return the counter value
        return counter.get();
    }
}
