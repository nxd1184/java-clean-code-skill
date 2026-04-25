package examples.after;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Stepdown. Top method reads as English: subtotal → tax → late fee →
 * round. Each helper sits at exactly one abstraction level below its
 * caller. Newspaper structure: high-level entry on top, details below.
 *
 * Rules: 6 (one abstraction level per function), 7 (stepdown).
 * Pure domain logic — no framework.
 */
public class InvoiceCalculator {

    private static final BigDecimal LATE_FEE_PER_DAY = new BigDecimal("0.50");
    private static final int CURRENCY_SCALE = 2;

    // ── Public entry point — high-level narrative ──

    public BigDecimal calculateInvoice(List<LineItem> items,
                                       String jurisdiction,
                                       LocalDate dueDate,
                                       LocalDate today) {
        BigDecimal subtotal = subtotalFor(items);
        BigDecimal tax      = taxFor(subtotal, jurisdiction);
        BigDecimal lateFee  = lateFeeFor(dueDate, today);
        return roundCurrency(subtotal.add(tax).add(lateFee));
    }

    // ── Mid-level helpers — one step down ──

    private BigDecimal subtotalFor(List<LineItem> items) {
        return items.stream()
            .map(this::lineTotalFor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal taxFor(BigDecimal subtotal, String jurisdiction) {
        BigDecimal rate = TaxRates.forJurisdiction(jurisdiction);
        return roundCurrency(subtotal.multiply(rate));
    }

    private BigDecimal lateFeeFor(LocalDate dueDate, LocalDate today) {
        if (!today.isAfter(dueDate)) return BigDecimal.ZERO;
        long daysLate = ChronoUnit.DAYS.between(dueDate, today);
        return LATE_FEE_PER_DAY.multiply(BigDecimal.valueOf(daysLate));
    }

    // ── Low-level helpers — one more step down ──

    private BigDecimal lineTotalFor(LineItem item) {
        BigDecimal raw = item.unitPrice().multiply(BigDecimal.valueOf(item.quantity()));
        return raw.subtract(discountOf(raw, item.discountPercent()));
    }

    private BigDecimal discountOf(BigDecimal amount, int discountPercent) {
        if (discountPercent <= 0) return BigDecimal.ZERO;
        return amount
            .multiply(BigDecimal.valueOf(discountPercent))
            .divide(BigDecimal.valueOf(100), CURRENCY_SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal roundCurrency(BigDecimal amount) {
        return amount.setScale(CURRENCY_SCALE, RoundingMode.HALF_UP);
    }

    // ── Lookup table extracted by responsibility ──

    private static final class TaxRates {
        static BigDecimal forJurisdiction(String code) {
            return switch (code) {
                case "US-CA" -> new BigDecimal("0.0725");
                case "US-NY" -> new BigDecimal("0.04");
                default -> code.startsWith("EU-") ? new BigDecimal("0.20") : BigDecimal.ZERO;
            };
        }
    }

    public record LineItem(BigDecimal unitPrice, int quantity, int discountPercent) {}
}
