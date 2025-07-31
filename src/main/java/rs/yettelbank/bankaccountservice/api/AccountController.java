package rs.yettelbank.bankaccountservice.api;

import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.yettelbank.bankaccountservice.api.model.request.OpenAccountRequestDTO;
import rs.yettelbank.bankaccountservice.api.model.request.UpdateAccountStatusRequestDTO;
import rs.yettelbank.bankaccountservice.api.model.request.UpdateBalanceRequestDTO;
import rs.yettelbank.bankaccountservice.api.model.response.AccountResponseDTO;
import rs.yettelbank.bankaccountservice.model.TransactionType;
import rs.yettelbank.bankaccountservice.service.AccountService;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponseDTO> openNewAccount(@Valid @RequestBody OpenAccountRequestDTO request) throws BadRequestException {
        AccountResponseDTO newAccount = accountService.openNewAccount(request);
        return new ResponseEntity<>(newAccount, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> getAccountById(@PathVariable Long id) {
        AccountResponseDTO account = accountService.getAccountById(id);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<AccountResponseDTO> getAccountByAccountNumber(@PathVariable String accountNumber) {
        AccountResponseDTO account = accountService.getAccountByAccountNumber(accountNumber);
        return ResponseEntity.ok(account);
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<AccountResponseDTO>> getAccountsByClientId(@PathVariable Long clientId) {
        List<AccountResponseDTO> accounts = accountService.getAccountsByClientId(clientId);
        return ResponseEntity.ok(accounts);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<AccountResponseDTO> updateAccountStatus(@PathVariable Long id,
                                                                  @Valid @RequestBody UpdateAccountStatusRequestDTO request) throws BadRequestException {
        AccountResponseDTO updatedAccount = accountService.updateAccountStatus(id, request.getStatus());
        return ResponseEntity.ok(updatedAccount);
    }

    @PutMapping("/{id}/deposit")
    public ResponseEntity<AccountResponseDTO> deposit(@PathVariable Long id,
                                                      @Valid @RequestBody UpdateBalanceRequestDTO request) throws BadRequestException {
        AccountResponseDTO updatedAccount = accountService.updateAccountBalance(id, request.getAmount(), TransactionType.DEPOSIT);
        return ResponseEntity.ok(updatedAccount);
    }

    @PutMapping("/{id}/withdraw")
    public ResponseEntity<AccountResponseDTO> withdraw(@PathVariable Long id,
                                                       @Valid @RequestBody UpdateBalanceRequestDTO request) throws BadRequestException {
        AccountResponseDTO updatedAccount = accountService.updateAccountBalance(id, request.getAmount(), TransactionType.WITHDRAWAL);
        return ResponseEntity.ok(updatedAccount);
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<AccountResponseDTO> closeAccount(@PathVariable Long id) throws BadRequestException {
        AccountResponseDTO closedAccount = accountService.closeAccount(id);
        return ResponseEntity.ok(closedAccount);
    }
}