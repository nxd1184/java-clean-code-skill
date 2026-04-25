package examples.after;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Single read-and-parse skeleton. Per-type parsing arrives as a lambda.
 * One bug fix (handling BOM, different delimiters, ...) lives in one place.
 *
 * Rule 13 (DRY).
 */
public class GenericFileParser {

    private static final String COMMENT = "#";
    private static final String DELIMITER = ",";

    public <T> List<T> parseFile(Path file, RecordParser<T> parser) throws IOException {
        List<T> out = new ArrayList<>();
        try (BufferedReader r = Files.newBufferedReader(file)) {
            String line;
            while ((line = r.readLine()) != null) {
                if (isSkippable(line)) continue;
                out.add(parser.parse(line.split(DELIMITER)));
            }
        }
        return out;
    }

    private boolean isSkippable(String line) {
        return line.isBlank() || line.startsWith(COMMENT);
    }

    // Call sites become tiny:
    //
    //   var customers = parser.parseFile(path, f -> new Customer(f[0], f[1]));
    //   var products  = parser.parseFile(path, f -> new Product(f[0], Integer.parseInt(f[1])));
    //   var orders    = parser.parseFile(path, f -> new Order(Long.parseLong(f[0]), f[1]));
}
