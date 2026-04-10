package rs.yettelbank.bankaccountservice.api.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import rs.yettelbank.bankaccountservice.db.entity.Account;
import rs.yettelbank.bankaccountservice.model.AccountStatus;
import rs.yettelbank.bankaccountservice.model.AccountType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountResponseDTO implements Serializable {
    private Long id;
    private String accountNumber;
    private AccountType accountType;
    private Account.Currency currency;
    private BigDecimal balance;
    private LocalDate openDate;
    private AccountStatus status;
    private Long clientId;
    private BigDecimal interestRate; // Dodato za kompletnost, može biti null

    public AccountResponseDTO() {
    }

    public AccountResponseDTO(Long id, String accountNumber, AccountType accountType, Account.Currency currency,
                              BigDecimal balance, LocalDate openDate, AccountStatus status, Long clientId,
                              BigDecimal interestRate) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.currency = currency;
        this.balance = balance;
        this.openDate = openDate;
        this.status = status;
        this.clientId = clientId;
        this.interestRate = interestRate;
    }

    // Getteri
    public Long getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public Account.Currency getCurrency() {
        return currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public LocalDate getOpenDate() {
        return openDate;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public Long getClientId() {
        return clientId;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    // Setteri
    public void setId(Long id) {
        this.id = id;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public void setCurrency(Account.Currency currency) {
        this.currency = currency;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public void setOpenDate(LocalDate openDate) {
        this.openDate = openDate;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountResponseDTO that = (AccountResponseDTO) o;
        return Objects.equals(id, that.id) && Objects.equals(accountNumber, that.accountNumber) && accountType == that.accountType && currency == that.currency && Objects.equals(balance, that.balance) && Objects.equals(openDate, that.openDate) && status == that.status && Objects.equals(clientId, that.clientId) && Objects.equals(interestRate, that.interestRate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, accountNumber, accountType, currency, balance, openDate, status, clientId, interestRate);
    }

    @Override
    public String toString() {
        return "AccountResponseDTO{" +
                "id=" + id +
                ", accountNumber='" + accountNumber + '\'' +
                ", accountType=" + accountType +
                ", currency=" + currency +
                ", balance=" + balance +
                ", openDate=" + openDate +
                ", status=" + status +
                ", clientId=" + clientId +
                ", interestRate=" + interestRate +
                '}';
    }
}
