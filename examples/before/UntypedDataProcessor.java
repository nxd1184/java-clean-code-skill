package examples.before;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Demonstrates Rule 1 (names reveal intent) and Rule 11 (objects
 * hide data). The class name says nothing. The argument is a
 * Map<String,Object> — what keys? what types? Methods are named
 * `process` and `handle` — process WHAT? handle WHAT?
 * Reader has to step through every line to figure out "this is
 * actually a money transfer between accounts."
 */
public class UntypedDataProcessor {

    public boolean process(Map<String, Object> data) {
        String from = (String) data.get("from");
        String to = (String) data.get("to");
        BigDecimal amt = (BigDecimal) data.get("amt");
        if (amt.signum() <= 0) return false;
        // ... debits 'from', credits 'to', logs ...
        return true;
    }

    public void handle(Map<String, Object> data) {
        process(data);
    }
}
