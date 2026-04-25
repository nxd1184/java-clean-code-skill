package examples.after;

/**
 * The class name says what it does. The method name says what it does.
 * The argument type says what's required.
 *
 * Rule 1 (names reveal intent).
 */
public class AccountTransferService {

    private final AccountLedger ledger;

    public AccountTransferService(AccountLedger ledger) {
        this.ledger = ledger;
    }

    public void executeTransfer(AccountTransfer transfer) {
        ledger.debit(transfer.fromAccountId(), transfer.amount());
        ledger.credit(transfer.toAccountId(), transfer.amount());
    }

    public interface AccountLedger {
        void debit(String accountId, java.math.BigDecimal amount);
        void credit(String accountId, java.math.BigDecimal amount);
    }
}
