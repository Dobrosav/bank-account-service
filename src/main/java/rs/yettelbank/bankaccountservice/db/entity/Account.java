package rs.yettelbank.bankaccountservice.db.entity;

import jakarta.persistence.*;
import rs.yettelbank.bankaccountservice.model.AccountStatus;
import rs.yettelbank.bankaccountservice.model.AccountType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Za auto-increment u MS SQL Serveru
    private Long id;

    @Column(name = "account_number", nullable = false, unique = true)
    private String accountNumber;

    @Enumerated(EnumType.STRING) // Čuva enum kao String u bazi
    @Column(name = "account_type", nullable = false)
    private AccountType accountType;

    @Enumerated(EnumType.STRING) // Čuva enum kao String u bazi
    @Column(name = "currency", nullable = false)
    private Currency currency;

    @Column(name = "balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(name = "open_date", nullable = false)
    private LocalDate openDate;

    @Enumerated(EnumType.STRING) // Čuva enum kao String u bazi
    @Column(name = "status", nullable = false)
    private AccountStatus status;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(name = "interest_rate")
    private BigDecimal interestRate; // Nullable, jer nije za sve tipove računa

    public enum Currency {
        RSD,
        EUR,
        USD,
        GBP
    }

    public Account() {
    }

    public Account(String accountNumber, AccountType accountType, Currency currency, BigDecimal balance, LocalDate openDate, AccountStatus status, Long clientId, BigDecimal interestRate) {
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.currency = currency;
        this.balance = balance;
        this.openDate = openDate;
        this.status = status;
        this.clientId = clientId;
        this.interestRate = interestRate;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public LocalDate getOpenDate() {
        return openDate;
    }

    public void setOpenDate(LocalDate openDate) {
        this.openDate = openDate;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    @Override
    public String toString() {
        return "Account{" +
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
