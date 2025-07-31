package rs.yettelbank.bankaccountservice.service;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rs.yettelbank.bankaccountservice.api.model.request.OpenAccountRequestDTO;
import rs.yettelbank.bankaccountservice.api.model.response.AccountResponseDTO;
import rs.yettelbank.bankaccountservice.db.entity.Account;
import rs.yettelbank.bankaccountservice.db.repo.AccountRepo;
import rs.yettelbank.bankaccountservice.exception.ErrorType;
import rs.yettelbank.bankaccountservice.exception.ServiceException;
import rs.yettelbank.bankaccountservice.mapper.MapperDtoJpa;
import rs.yettelbank.bankaccountservice.model.AccountStatus;
import rs.yettelbank.bankaccountservice.model.AccountType;
import rs.yettelbank.bankaccountservice.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final AccountRepo accountRepository;
    private static final String BANK_PREFIX = "RS123-";
    private static final AtomicLong accountNumberSequence = new AtomicLong(1000000);

    @Autowired
    public AccountService(AccountRepo accountRepository) {
        this.accountRepository = accountRepository;
    }

    private String generateAccountNumber() {
        return BANK_PREFIX + String.format("%07d", accountNumberSequence.getAndIncrement());
    }

    @Transactional
    public AccountResponseDTO openNewAccount(OpenAccountRequestDTO request) throws BadRequestException {
        if (request.getClientId() == null || request.getClientId() <= 0) {
            throw new BadRequestException("Client ID must be a positive number.");
        }

        Optional<Account> existingAccountByType = accountRepository.findByClientIdAndAccountType(request.getClientId(), request.getAccountType());
        if (existingAccountByType.isPresent()) {
            throw new BadRequestException("Client already has an account of type " + request.getAccountType() + ". Cannot open another one of the same type.");
        }

        Account account = new Account();
        account.setClientId(request.getClientId());
        account.setAccountType(request.getAccountType());
        account.setCurrency(request.getCurrency() != null ? request.getCurrency() : Account.Currency.RSD);
        account.setAccountNumber(generateAccountNumber());
        account.setOpenDate(LocalDate.now());
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(AccountStatus.PENDING);

        Account savedAccount = accountRepository.save(account);

        return mapToAccountResponseDTO(savedAccount);
    }

    public AccountResponseDTO getAccountById(Long id) {
        Account account = accountRepository.findById(id).get();
        if (account == null) {
            throw new ServiceException(ErrorType.ACCOUNT_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        return mapToAccountResponseDTO(account);
    }

    public AccountResponseDTO getAccountByAccountNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber);
        if (account == null) {
            throw new ServiceException(ErrorType.ACCOUNT_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        return mapToAccountResponseDTO(account);
    }

    public List<AccountResponseDTO> getAccountsByClientId(Long clientId) {
        List<Account> accounts = accountRepository.findByClientId(clientId);
        return accounts.stream()
                .map(this::mapToAccountResponseDTO)
                .collect(Collectors.toList());
    }

    public List<AccountResponseDTO> getAccountsByTypeAndStatus(AccountType accountType, AccountStatus status) {
        List<Account> accounts;
        if (accountType != null && status != null) {
            accounts = accountRepository.findByAccountTypeAndStatus(accountType, status);
        } else if (accountType != null) {
            accounts = accountRepository.findByAccountType(accountType);
        } else if (status != null) {
            accounts = accountRepository.findByStatus(status);
        } else {
            accounts = accountRepository.findAll();
        }
        return accounts.stream()
                .map(this::mapToAccountResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public AccountResponseDTO updateAccountStatus(Long id, AccountStatus newStatus) throws BadRequestException {
        Account account = accountRepository.findById(id).get();
        if (account == null) {
            throw new ServiceException(ErrorType.ACCOUNT_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        if (account.getStatus() == AccountStatus.CLOSED && newStatus != AccountStatus.CLOSED) {
            throw new BadRequestException("Cannot reactivate a closed account.");
        }

        account.setStatus(newStatus);
        Account updatedAccount = accountRepository.save(account);
        return mapToAccountResponseDTO(updatedAccount);
    }

    @Transactional
    public AccountResponseDTO updateAccountBalance(Long id, BigDecimal amount, TransactionType type) throws BadRequestException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Amount must be positive.");
        }

        Account account = accountRepository.findById(id).get();
        if (account == null) {
            throw new ServiceException(ErrorType.ACCOUNT_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new BadRequestException("Cannot perform transactions on an account that is not ACTIVE. Current status: " + account.getStatus());
        }

        BigDecimal currentBalance = account.getBalance();
        BigDecimal newBalance;

        if (type == TransactionType.DEPOSIT) {
            newBalance = currentBalance.add(amount);
        } else if (type == TransactionType.WITHDRAWAL) {
            if (currentBalance.compareTo(amount) < 0) {
                throw new BadRequestException("Insufficient funds. Current balance: " + currentBalance + ", requested withdrawal: " + amount);
            }
            newBalance = currentBalance.subtract(amount);
        } else {
            throw new BadRequestException("Invalid transaction type: " + type);
        }

        account.setBalance(newBalance);
        Account updatedAccount = accountRepository.save(account);
        return mapToAccountResponseDTO(updatedAccount);
    }

    @Transactional
    public AccountResponseDTO closeAccount(Long id) throws BadRequestException {
        Account account = accountRepository.findById(id).get();
        if (account == null) {
            throw new ServiceException(ErrorType.ACCOUNT_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new BadRequestException("Account balance must be 0 before closing. Current balance: " + account.getBalance());
        }

        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new BadRequestException("Account with ID " + id + " is already closed.");
        }

        account.setStatus(AccountStatus.CLOSED);
        Account closedAccount = accountRepository.save(account);
        return mapToAccountResponseDTO(closedAccount);
    }

    private AccountResponseDTO mapToAccountResponseDTO(Account account) {
        return MapperDtoJpa.INSTANCE.mapToAccountResponseDTO(account);
    }
}