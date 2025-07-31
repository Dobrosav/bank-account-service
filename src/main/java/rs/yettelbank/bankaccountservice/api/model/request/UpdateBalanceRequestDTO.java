package rs.yettelbank.bankaccountservice.api.model.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class UpdateBalanceRequestDTO implements Serializable {
    @NotNull(message = "Amount is required.")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero.")
    private BigDecimal amount;

    public UpdateBalanceRequestDTO() {
    }

    public UpdateBalanceRequestDTO(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateBalanceRequestDTO that = (UpdateBalanceRequestDTO) o;
        return Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }

    @Override
    public String toString() {
        return "UpdateBalanceRequestDTO{" +
                "amount=" + amount +
                '}';
    }
}
