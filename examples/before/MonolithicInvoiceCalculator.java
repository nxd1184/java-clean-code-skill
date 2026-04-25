package examples.before;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Demonstrates Rule 6 (one abstraction level) and Rule 7 (stepdown).
 * One 50-line method mixing: line-item iteration, subtotal math,
 * tax-jurisdiction lookup, late-fee day arithmetic, currency rounding,
 * total assembly. Reader has to keep ALL of those concerns in head
 * simultaneously to follow the calculation. No framework — pure domain.
 */
public class MonolithicInvoiceCalculator {

    public BigDecimal calculateInvoice(List<LineItem> items, String jurisdiction,
                                       LocalDate dueDate, LocalDate today) {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (LineItem item : items) {
            BigDecimal lineTotal = item.unitPrice().multiply(BigDecimal.valueOf(item.quantity()));
            if (item.discountPercent() > 0) {
                BigDecimal discount = lineTotal
                    .multiply(BigDecimal.valueOf(item.discountPercent()))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                lineTotal = lineTotal.subtract(discount);
            }
            subtotal = subtotal.add(lineTotal);
        }

        BigDecimal taxRate;
        if (jurisdiction.equals("US-CA")) taxRate = new BigDecimal("0.0725");
        else if (jurisdiction.equals("US-NY")) taxRate = new BigDecimal("0.04");
        else if (jurisdiction.startsWith("EU-")) taxRate = new BigDecimal("0.20");
        else taxRate = BigDecimal.ZERO;

        BigDecimal tax = subtotal.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(tax);

        if (today.isAfter(dueDate)) {
            long daysLate = ChronoUnit.DAYS.between(dueDate, today);
            BigDecimal lateFeePerDay = new BigDecimal("0.50");
            BigDecimal lateFee = lateFeePerDay.multiply(BigDecimal.valueOf(daysLate));
            total = total.add(lateFee);
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public record LineItem(BigDecimal unitPrice, int quantity, int discountPercent) {}
}
