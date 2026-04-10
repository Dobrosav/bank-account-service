package rs.yettelbank.bankaccountservice;

import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import rs.yettelbank.bankaccountservice.api.model.event.TransactionEventDTO;
import rs.yettelbank.bankaccountservice.api.model.request.OpenAccountRequestDTO;
import rs.yettelbank.bankaccountservice.api.model.response.AccountResponseDTO;
import rs.yettelbank.bankaccountservice.db.entity.Account;
import rs.yettelbank.bankaccountservice.db.repo.AccountRepo;
import rs.yettelbank.bankaccountservice.mapper.MapperDtoJpa;
import rs.yettelbank.bankaccountservice.model.AccountStatus;
import rs.yettelbank.bankaccountservice.model.AccountType;
import rs.yettelbank.bankaccountservice.model.TransactionType;
import rs.yettelbank.bankaccountservice.service.AccountService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;


class BankAccountServiceApplicationTests extends AbstractIntegrationTest {

    @MockBean
    private AccountRepo accountRepository;

    @MockBean
    private KafkaTemplate<String, TransactionEventDTO> kafkaTemplate;

    @Autowired
    private AccountService accountService;

    private Account testAccount;
    private AccountResponseDTO testAccountResponse;

    @BeforeEach
    void setUp() {
        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setClientId(100L);
        testAccount.setAccountNumber("RS123-1000000");
        testAccount.setAccountType(AccountType.CURRENT);
        testAccount.setStatus(AccountStatus.ACTIVE);
        testAccount.setBalance(BigDecimal.valueOf(1000));
        testAccount.setOpenDate(LocalDate.now());

        testAccountResponse = MapperDtoJpa.INSTANCE.mapToAccountResponseDTO(testAccount);
    }

    @Test
    void contextLoads() {
    }

    @Test
    void openNewAccount_Success() throws BadRequestException {
        // Arrange
        OpenAccountRequestDTO request = new OpenAccountRequestDTO();
        request.setClientId(100L);
        request.setAccountType(AccountType.CURRENT);

        Account pendingAccount = new Account();
        pendingAccount.setId(1L);
        pendingAccount.setClientId(100L);
        pendingAccount.setAccountNumber("RS123-0000001");
        pendingAccount.setAccountType(AccountType.CURRENT);
        pendingAccount.setStatus(AccountStatus.PENDING);
        pendingAccount.setBalance(BigDecimal.ZERO);

        when(accountRepository.findByClientIdAndAccountType(anyLong(), any())).thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class))).thenReturn(pendingAccount);

        // Act
        AccountResponseDTO response = accountService.openNewAccount(request);

        // Assert
        assertNotNull(response);
        assertEquals(testAccount.getClientId(), response.getClientId());
        assertTrue(response.getAccountNumber().startsWith("RS123-"));
        assertEquals(AccountStatus.PENDING, response.getStatus());
    }

    @Test
    void openNewAccount_DuplicateAccountType_ThrowsException() {
        // Arrange
        OpenAccountRequestDTO request = new OpenAccountRequestDTO();
        request.setClientId(100L);
        request.setAccountType(AccountType.CURRENT);

        when(accountRepository.findByClientIdAndAccountType(anyLong(), any()))
                .thenReturn(Optional.of(testAccount));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> accountService.openNewAccount(request));
    }

    @Test
    void getAccountById_Success() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        // Act
        AccountResponseDTO response = accountService.getAccountById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(testAccount.getId(), response.getId());
    }

    @Test
    void getAccountByAccountNumber_Success() {
        // Arrange
        when(accountRepository.findByAccountNumber("RS123-1000000")).thenReturn(testAccount);

        // Act
        AccountResponseDTO response = accountService.getAccountByAccountNumber("RS123-1000000");

        // Assert
        assertNotNull(response);
        assertEquals(testAccount.getAccountNumber(), response.getAccountNumber());
    }

    @Test
    void updateAccountStatus_Success() throws BadRequestException {
        // Arrange
        Account accountToUpdate = new Account();
        accountToUpdate.setId(1L);
        accountToUpdate.setClientId(100L);
        accountToUpdate.setAccountNumber("RS123-1000000");
        accountToUpdate.setAccountType(AccountType.CURRENT);
        accountToUpdate.setStatus(AccountStatus.ACTIVE);
        accountToUpdate.setBalance(BigDecimal.valueOf(1000));
        accountToUpdate.setOpenDate(LocalDate.now());

        Account closedAccount = new Account();
        closedAccount.setId(1L);
        closedAccount.setClientId(100L);
        closedAccount.setAccountNumber("RS123-1000000");
        closedAccount.setAccountType(AccountType.CURRENT);
        closedAccount.setStatus(AccountStatus.CLOSED);
        closedAccount.setBalance(BigDecimal.valueOf(1000));
        closedAccount.setOpenDate(LocalDate.now());

        when(accountRepository.findById(1L)).thenReturn(Optional.of(accountToUpdate));
        when(accountRepository.save(any(Account.class))).thenReturn(closedAccount);

        // Act
        AccountResponseDTO response = accountService.updateAccountStatus(1L, AccountStatus.CLOSED);

        // Assert
        assertNotNull(response);
        assertEquals(AccountStatus.CLOSED, response.getStatus());
    }

    @Test
    void updateAccountBalance_DepositSuccess() throws BadRequestException {
        // Arrange
        BigDecimal depositAmount = BigDecimal.valueOf(500);
        BigDecimal expectedBalance = testAccount.getBalance().add(depositAmount);

        Account updatedAccount = new Account();
        updatedAccount.setId(1L);
        updatedAccount.setClientId(100L);
        updatedAccount.setAccountNumber("RS123-1000000");
        updatedAccount.setAccountType(AccountType.CURRENT);
        updatedAccount.setStatus(AccountStatus.ACTIVE);
        updatedAccount.setBalance(expectedBalance);
        updatedAccount.setOpenDate(LocalDate.now());

        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(updatedAccount);



        // Act
        AccountResponseDTO response = accountService.updateAccountBalance(1L, depositAmount, TransactionType.DEPOSIT);

        // Assert
        assertNotNull(response);
        assertEquals(expectedBalance, response.getBalance());
    }

    @Test
    void updateAccountBalance_WithdrawalInsufficientFunds() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        BigDecimal withdrawalAmount = BigDecimal.valueOf(2000);

        // Act & Assert
        assertThrows(BadRequestException.class,
                () -> accountService.updateAccountBalance(1L, withdrawalAmount, TransactionType.WITHDRAWAL));
    }

    @Test
    void closeAccount_Success() throws BadRequestException {
        // Arrange
        Account zeroBalanceAccount = new Account();
        zeroBalanceAccount.setId(1L);
        zeroBalanceAccount.setClientId(100L);
        zeroBalanceAccount.setAccountNumber("RS123-1000000");
        zeroBalanceAccount.setAccountType(AccountType.CURRENT);
        zeroBalanceAccount.setStatus(AccountStatus.ACTIVE);
        zeroBalanceAccount.setBalance(BigDecimal.ZERO);
        zeroBalanceAccount.setOpenDate(LocalDate.now());

        Account closedAccount = new Account();
        closedAccount.setId(1L);
        closedAccount.setClientId(100L);
        closedAccount.setAccountNumber("RS123-1000000");
        closedAccount.setAccountType(AccountType.CURRENT);
        closedAccount.setStatus(AccountStatus.CLOSED);
        closedAccount.setBalance(BigDecimal.ZERO);
        closedAccount.setOpenDate(LocalDate.now());

        when(accountRepository.findById(1L)).thenReturn(Optional.of(zeroBalanceAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(closedAccount);

        // Act
        AccountResponseDTO response = accountService.closeAccount(1L);

        // Assert
        assertNotNull(response);
        assertEquals(AccountStatus.CLOSED, response.getStatus());
    }

    @Test
    void closeAccount_NonZeroBalance() {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> accountService.closeAccount(1L));
    }

    @Test
    void getAccountsByClientId_Success() {
        // Arrange
        List<Account> accounts = Arrays.asList(testAccount);
        when(accountRepository.findByClientId(100L)).thenReturn(accounts);

        // Act
        List<AccountResponseDTO> response = accountService.getAccountsByClientId(100L);

        // Assert
        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertEquals(1, response.size());
        assertEquals(testAccount.getClientId(), response.get(0).getClientId());
    }

}
