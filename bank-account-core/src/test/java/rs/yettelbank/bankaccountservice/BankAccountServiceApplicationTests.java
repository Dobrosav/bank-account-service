package rs.yettelbank.bankaccountservice;

import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
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


class BankAccountServiceApplicationTests extends AbstractIntegrationTest{


    @Mock
    private AccountRepo accountRepository;

    @InjectMocks
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

        when(accountRepository.findByClientIdAndAccountType(anyLong(), any())).thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        // Act
        AccountResponseDTO response = accountService.openNewAccount(request);

        // Assert
        assertNotNull(response);
        assertEquals(testAccount.getClientId(), response.getClientId());
        assertTrue(response.getAccountNumber().startsWith("RS123-"));
        assertEquals(AccountStatus.ACTIVE, response.getStatus());
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
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        // Act
        AccountResponseDTO response = accountService.updateAccountStatus(1L, AccountStatus.CLOSED);

        // Assert
        assertNotNull(response);
        assertEquals(AccountStatus.CLOSED, response.getStatus());
    }

    @Test
    void updateAccountBalance_DepositSuccess() throws BadRequestException {
        // Arrange
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        BigDecimal depositAmount = BigDecimal.valueOf(500);
        BigDecimal expectedBalance = testAccount.getBalance().add(depositAmount);

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
        testAccount.setBalance(BigDecimal.ZERO);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

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
