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
import rs.yettelbank.bankaccountservice.model.AccountStatus;
import rs.yettelbank.bankaccountservice.model.AccountType;
import rs.yettelbank.bankaccountservice.model.TransactionType;
import rs.yettelbank.bankaccountservice.service.AccountService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AccountServiceIntegrationTest extends FullIntegrationTest {

    @MockBean
    private KafkaTemplate<String, TransactionEventDTO> kafkaTemplate;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepo accountRepository;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
    }

    @Test
    void openNewAccount_Success() throws BadRequestException {
        OpenAccountRequestDTO request = new OpenAccountRequestDTO();
        request.setClientId(100L);
        request.setAccountType(AccountType.CURRENT);

        AccountResponseDTO response = accountService.openNewAccount(request);

        assertNotNull(response);
        assertEquals(100L, response.getClientId());
        assertTrue(response.getAccountNumber().startsWith("RS123-"));
        assertEquals(AccountStatus.PENDING, response.getStatus());
        assertEquals(BigDecimal.ZERO, response.getBalance());

        Optional<Account> savedAccount = accountRepository.findById(response.getId());
        assertThat(savedAccount).isPresent();
        assertEquals(AccountType.CURRENT, savedAccount.get().getAccountType());
    }

    @Test
    void openNewAccount_DuplicateAccountType_ThrowsException() throws BadRequestException {
        OpenAccountRequestDTO request = new OpenAccountRequestDTO();
        request.setClientId(100L);
        request.setAccountType(AccountType.CURRENT);

        accountService.openNewAccount(request);

        OpenAccountRequestDTO duplicateRequest = new OpenAccountRequestDTO();
        duplicateRequest.setClientId(100L);
        duplicateRequest.setAccountType(AccountType.CURRENT);

        assertThrows(BadRequestException.class, () -> accountService.openNewAccount(duplicateRequest));
    }

    @Test
    void getAccountById_Success() throws BadRequestException {
        OpenAccountRequestDTO request = new OpenAccountRequestDTO();
        request.setClientId(100L);
        request.setAccountType(AccountType.CURRENT);

        AccountResponseDTO created = accountService.openNewAccount(request);

        AccountResponseDTO response = accountService.getAccountById(created.getId());

        assertNotNull(response);
        assertEquals(created.getId(), response.getId());
        assertEquals(100L, response.getClientId());
    }

    @Test
    void getAccountById_NotFound() {
        assertThrows(Exception.class, () -> accountService.getAccountById(999L));
    }

    @Test
    void getAccountByAccountNumber_Success() throws BadRequestException {
        OpenAccountRequestDTO request = new OpenAccountRequestDTO();
        request.setClientId(100L);
        request.setAccountType(AccountType.CURRENT);

        AccountResponseDTO created = accountService.openNewAccount(request);

        AccountResponseDTO response = accountService.getAccountByAccountNumber(created.getAccountNumber());

        assertNotNull(response);
        assertEquals(created.getAccountNumber(), response.getAccountNumber());
    }

    @Test
    void updateAccountStatus_Success() throws BadRequestException {
        OpenAccountRequestDTO request = new OpenAccountRequestDTO();
        request.setClientId(100L);
        request.setAccountType(AccountType.CURRENT);

        AccountResponseDTO created = accountService.openNewAccount(request);

        AccountResponseDTO response = accountService.updateAccountStatus(created.getId(), AccountStatus.ACTIVE);

        assertNotNull(response);
        assertEquals(AccountStatus.ACTIVE, response.getStatus());
    }

    @Test
    void updateAccountBalance_DepositSuccess() throws BadRequestException {
        OpenAccountRequestDTO request = new OpenAccountRequestDTO();
        request.setClientId(100L);
        request.setAccountType(AccountType.CURRENT);

        AccountResponseDTO created = accountService.openNewAccount(request);
        accountService.updateAccountStatus(created.getId(), AccountStatus.ACTIVE);

        AccountResponseDTO response = accountService.updateAccountBalance(
                created.getId(), BigDecimal.valueOf(500), TransactionType.DEPOSIT);

        assertNotNull(response);
        assertThat(response.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(500));
    }

    @Test
    void updateAccountBalance_WithdrawalSuccess() throws BadRequestException {
        OpenAccountRequestDTO request = new OpenAccountRequestDTO();
        request.setClientId(100L);
        request.setAccountType(AccountType.CURRENT);

        AccountResponseDTO created = accountService.openNewAccount(request);
        accountService.updateAccountStatus(created.getId(), AccountStatus.ACTIVE);
        accountService.updateAccountBalance(created.getId(), BigDecimal.valueOf(1000), TransactionType.DEPOSIT);

        AccountResponseDTO response = accountService.updateAccountBalance(
                created.getId(), BigDecimal.valueOf(300), TransactionType.WITHDRAWAL);

        assertNotNull(response);
        assertThat(response.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(700));
    }

    @Test
    void updateAccountBalance_WithdrawalInsufficientFunds() throws BadRequestException {
        OpenAccountRequestDTO request = new OpenAccountRequestDTO();
        request.setClientId(100L);
        request.setAccountType(AccountType.CURRENT);

        AccountResponseDTO created = accountService.openNewAccount(request);

        assertThrows(BadRequestException.class, () ->
                accountService.updateAccountBalance(created.getId(), BigDecimal.valueOf(100), TransactionType.WITHDRAWAL));
    }

    @Test
    void closeAccount_Success() throws BadRequestException {
        OpenAccountRequestDTO request = new OpenAccountRequestDTO();
        request.setClientId(100L);
        request.setAccountType(AccountType.CURRENT);

        AccountResponseDTO created = accountService.openNewAccount(request);

        AccountResponseDTO response = accountService.closeAccount(created.getId());

        assertNotNull(response);
        assertEquals(AccountStatus.CLOSED, response.getStatus());
    }

    @Test
    void closeAccount_NonZeroBalance() throws BadRequestException {
        OpenAccountRequestDTO request = new OpenAccountRequestDTO();
        request.setClientId(100L);
        request.setAccountType(AccountType.CURRENT);

        AccountResponseDTO created = accountService.openNewAccount(request);
        accountService.updateAccountStatus(created.getId(), AccountStatus.ACTIVE);
        accountService.updateAccountBalance(created.getId(), BigDecimal.valueOf(100), TransactionType.DEPOSIT);

        assertThrows(BadRequestException.class, () -> accountService.closeAccount(created.getId()));
    }

    @Test
    void getAccountsByClientId_Success() throws BadRequestException {
        OpenAccountRequestDTO request1 = new OpenAccountRequestDTO();
        request1.setClientId(100L);
        request1.setAccountType(AccountType.CURRENT);
        accountService.openNewAccount(request1);

        OpenAccountRequestDTO request2 = new OpenAccountRequestDTO();
        request2.setClientId(100L);
        request2.setAccountType(AccountType.SAVINGS);
        accountService.openNewAccount(request2);

        List<AccountResponseDTO> response = accountService.getAccountsByClientId(100L);

        assertNotNull(response);
        assertEquals(2, response.size());
    }

    @Test
    void transferFunds_Success() throws BadRequestException {
        OpenAccountRequestDTO fromRequest = new OpenAccountRequestDTO();
        fromRequest.setClientId(100L);
        fromRequest.setAccountType(AccountType.CURRENT);
        AccountResponseDTO fromAccount = accountService.openNewAccount(fromRequest);
        accountService.updateAccountStatus(fromAccount.getId(), AccountStatus.ACTIVE);
        accountService.updateAccountBalance(fromAccount.getId(), BigDecimal.valueOf(1000), TransactionType.DEPOSIT);

        OpenAccountRequestDTO toRequest = new OpenAccountRequestDTO();
        toRequest.setClientId(200L);
        toRequest.setAccountType(AccountType.CURRENT);
        AccountResponseDTO toAccount = accountService.openNewAccount(toRequest);
        accountService.updateAccountStatus(toAccount.getId(), AccountStatus.ACTIVE);

        AccountResponseDTO response = accountService.transferFunds(fromAccount.getId(), toAccount.getId(), BigDecimal.valueOf(300));

        assertNotNull(response);
        assertThat(response.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(700));

        AccountResponseDTO destinationAccount = accountService.getAccountById(toAccount.getId());
        assertThat(destinationAccount.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(300));
    }

    @Test
    void transferFunds_SameAccount_ThrowsException() throws BadRequestException {
        OpenAccountRequestDTO request = new OpenAccountRequestDTO();
        request.setClientId(100L);
        request.setAccountType(AccountType.CURRENT);
        AccountResponseDTO account = accountService.openNewAccount(request);
        accountService.updateAccountStatus(account.getId(), AccountStatus.ACTIVE);

        assertThrows(BadRequestException.class, () ->
                accountService.transferFunds(account.getId(), account.getId(), BigDecimal.valueOf(100)));
    }

    @Test
    void transferFunds_SourceAccountNotFound() {
        assertThrows(Exception.class, () ->
                accountService.transferFunds(999L, 100L, BigDecimal.valueOf(100)));
    }

    @Test
    void transferFunds_DestinationAccountNotFound() throws BadRequestException {
        OpenAccountRequestDTO request = new OpenAccountRequestDTO();
        request.setClientId(100L);
        request.setAccountType(AccountType.CURRENT);
        AccountResponseDTO account = accountService.openNewAccount(request);
        accountService.updateAccountStatus(account.getId(), AccountStatus.ACTIVE);

        assertThrows(Exception.class, () ->
                accountService.transferFunds(account.getId(), 999L, BigDecimal.valueOf(100)));
    }

    @Test
    void transferFunds_SourceAccountNotActive_ThrowsException() throws BadRequestException {
        OpenAccountRequestDTO fromRequest = new OpenAccountRequestDTO();
        fromRequest.setClientId(100L);
        fromRequest.setAccountType(AccountType.CURRENT);
        AccountResponseDTO fromAccount = accountService.openNewAccount(fromRequest);

        OpenAccountRequestDTO toRequest = new OpenAccountRequestDTO();
        toRequest.setClientId(200L);
        toRequest.setAccountType(AccountType.CURRENT);
        AccountResponseDTO toAccount = accountService.openNewAccount(toRequest);
        accountService.updateAccountStatus(toAccount.getId(), AccountStatus.ACTIVE);

        assertThrows(BadRequestException.class, () ->
                accountService.transferFunds(fromAccount.getId(), toAccount.getId(), BigDecimal.valueOf(100)));
    }

    @Test
    void transferFunds_DestinationAccountNotActive_ThrowsException() throws BadRequestException {
        OpenAccountRequestDTO fromRequest = new OpenAccountRequestDTO();
        fromRequest.setClientId(100L);
        fromRequest.setAccountType(AccountType.CURRENT);
        AccountResponseDTO fromAccount = accountService.openNewAccount(fromRequest);
        accountService.updateAccountStatus(fromAccount.getId(), AccountStatus.ACTIVE);

        OpenAccountRequestDTO toRequest = new OpenAccountRequestDTO();
        toRequest.setClientId(200L);
        toRequest.setAccountType(AccountType.CURRENT);
        AccountResponseDTO toAccount = accountService.openNewAccount(toRequest);

        assertThrows(BadRequestException.class, () ->
                accountService.transferFunds(fromAccount.getId(), toAccount.getId(), BigDecimal.valueOf(100)));
    }

    @Test
    void transferFunds_InsufficientFunds_ThrowsException() throws BadRequestException {
        OpenAccountRequestDTO fromRequest = new OpenAccountRequestDTO();
        fromRequest.setClientId(100L);
        fromRequest.setAccountType(AccountType.CURRENT);
        AccountResponseDTO fromAccount = accountService.openNewAccount(fromRequest);
        accountService.updateAccountStatus(fromAccount.getId(), AccountStatus.ACTIVE);

        OpenAccountRequestDTO toRequest = new OpenAccountRequestDTO();
        toRequest.setClientId(200L);
        toRequest.setAccountType(AccountType.CURRENT);
        AccountResponseDTO toAccount = accountService.openNewAccount(toRequest);
        accountService.updateAccountStatus(toAccount.getId(), AccountStatus.ACTIVE);

        assertThrows(BadRequestException.class, () ->
                accountService.transferFunds(fromAccount.getId(), toAccount.getId(), BigDecimal.valueOf(1000)));
    }
}
