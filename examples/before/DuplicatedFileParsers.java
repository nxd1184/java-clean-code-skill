package examples.before;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates Rule 13 (DRY).
 * Three methods. The skeleton is identical: open file, read lines, skip
 * blank/comment, parse each line, close. Only the per-line parse differs.
 * A bug fix in one method (e.g. handling BOM) has to be repeated in all
 * three. Plain Java — no framework.
 */
public class DuplicatedFileParsers {

    public List<Customer> parseCustomers(Path file) throws IOException {
        List<Customer> out = new ArrayList<>();
        try (BufferedReader r = Files.newBufferedReader(file)) {
            String line;
            while ((line = r.readLine()) != null) {
                if (line.isBlank() || line.startsWith("#")) continue;
                String[] parts = line.split(",");
                out.add(new Customer(parts[0], parts[1]));
            }
        }
        return out;
    }

    public List<Product> parseProducts(Path file) throws IOException {
        List<Product> out = new ArrayList<>();
        try (BufferedReader r = Files.newBufferedReader(file)) {
            String line;
            while ((line = r.readLine()) != null) {
                if (line.isBlank() || line.startsWith("#")) continue;
                String[] parts = line.split(",");
                out.add(new Product(parts[0], Integer.parseInt(parts[1])));
            }
        }
        return out;
    }

    public List<Order> parseOrders(Path file) throws IOException {
        List<Order> out = new ArrayList<>();
        try (BufferedReader r = Files.newBufferedReader(file)) {
            String line;
            while ((line = r.readLine()) != null) {
                if (line.isBlank() || line.startsWith("#")) continue;
                String[] parts = line.split(",");
                out.add(new Order(Long.parseLong(parts[0]), parts[1]));
            }
        }
        return out;
    }

    record Customer(String id, String name) {}
    record Product(String sku, int qty) {}
    record Order(long id, String customerId) {}
}
