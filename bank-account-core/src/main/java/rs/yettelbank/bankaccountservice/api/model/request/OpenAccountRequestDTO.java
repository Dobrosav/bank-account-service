package rs.yettelbank.bankaccountservice.api.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import rs.yettelbank.bankaccountservice.db.entity.Account;
import rs.yettelbank.bankaccountservice.model.AccountType;

import java.io.Serializable;
import java.util.Objects;

public class OpenAccountRequestDTO implements Serializable {

    @NotNull(message = "Client ID is required.")
    @Min(value = 1, message = "Client ID must be a positive number.")
    private Long clientId;

    @NotNull(message = "Account type is required.")
    private AccountType accountType;

    private Account.Currency currency;

    public OpenAccountRequestDTO() {
    }

    public OpenAccountRequestDTO(Long clientId, AccountType accountType, Account.Currency currency) {
        this.clientId = clientId;
        this.accountType = accountType;
        this.currency = currency;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public Account.Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Account.Currency currency) {
        this.currency = currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OpenAccountRequestDTO that = (OpenAccountRequestDTO) o;
        return Objects.equals(clientId, that.clientId) && accountType == that.accountType && currency == that.currency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, accountType, currency);
    }

    @Override
    public String toString() {
        return "OpenAccountRequestDTO{" +
                "clientId=" + clientId +
                ", accountType=" + accountType +
                ", currency=" + currency +
                '}';
    }
}