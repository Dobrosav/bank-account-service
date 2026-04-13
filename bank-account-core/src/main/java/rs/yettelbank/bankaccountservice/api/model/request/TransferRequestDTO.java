package rs.yettelbank.bankaccountservice.api.model.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.math.BigDecimal;

public class TransferRequestDTO implements Serializable {
    @NotNull(message = "Source account ID is required.")
    private Long fromAccountId;

    @NotNull(message = "Destination account ID is required.")
    private Long toAccountId;

    @NotNull(message = "Amount is required.")
    @DecimalMin(value = "0.01", message = "Transfer amount must be greater than zero.")
    private BigDecimal amount;

    public TransferRequestDTO() {
    }

    public TransferRequestDTO(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
    }

    public Long getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(Long fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public Long getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(Long toAccountId) {
        this.toAccountId = toAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "TransferRequestDTO{" +
                "fromAccountId=" + fromAccountId +
                ", toAccountId=" + toAccountId +
                ", amount=" + amount +
                '}';
    }
}
