package examples.after;

/**
 * One line of a delimited file → one typed record.
 * Functional interface — callers pass a lambda or method reference.
 */
@FunctionalInterface
public interface RecordParser<T> {
    T parse(String[] fields);
}
