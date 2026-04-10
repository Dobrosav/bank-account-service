package rs.yettelbank.bankaccountservice.api.model.event;

import rs.yettelbank.bankaccountservice.model.TransactionType;
import java.math.BigDecimal;

public class TransactionEventDTO {
    private Long accountId;
    private BigDecimal amount;
    private TransactionType type;


    public TransactionEventDTO() {}

    public TransactionEventDTO(Long accountId, BigDecimal amount, TransactionType type) {
        this.accountId = accountId;
        this.amount = amount;
        this.type = type;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "TransactionEventDTO{" +
                "accountId=" + accountId +
                ", amount=" + amount +
                ", type=" + type +
                '}';
    }
}