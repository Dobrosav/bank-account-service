package rs.yettelbank.bankaccountservice.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@RequestMapping("/api/v1/accounts")
@Tag(name = "Accounts API", description = "API for managing bank accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(summary = "Open a new bank account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Account opened successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or business rule violation")
    })
    @PostMapping
    public ResponseEntity<AccountResponseDTO> openNewAccount(@Valid @RequestBody OpenAccountRequestDTO request) throws BadRequestException {
        AccountResponseDTO newAccount = accountService.openNewAccount(request);
        return new ResponseEntity<>(newAccount, HttpStatus.CREATED);
    }

    @Operation(summary = "Get account by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account found"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> getAccountById(@PathVariable(name = "id") Long id) {
        AccountResponseDTO account = accountService.getAccountById(id);
        return ResponseEntity.ok(account);
    }

    @Operation(summary = "Get account by account number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account found"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<AccountResponseDTO> getAccountByAccountNumber(@PathVariable(name = "accountNumber") String accountNumber) {
        AccountResponseDTO account = accountService.getAccountByAccountNumber(accountNumber);
        return ResponseEntity.ok(account);
    }

    @Operation(summary = "Get all accounts for a client")
    @ApiResponse(responseCode = "200", description = "List of accounts retrieved successfully")
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<AccountResponseDTO>> getAccountsByClientId(@PathVariable(name = "clientId") Long clientId) {
        List<AccountResponseDTO> accounts = accountService.getAccountsByClientId(clientId);
        return ResponseEntity.ok(accounts);
    }

    @Operation(summary = "Update account status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account status updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid status or business rule violation"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<AccountResponseDTO> updateAccountStatus(@PathVariable(name = "id") Long id,
                                                                  @Valid @RequestBody UpdateAccountStatusRequestDTO request) throws BadRequestException {
        AccountResponseDTO updatedAccount = accountService.updateAccountStatus(id, request.getStatus());
        return ResponseEntity.ok(updatedAccount);
    }

    @Operation(summary = "Simulate a deposit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deposit successful, balance updated"),
            @ApiResponse(responseCode = "400", description = "Invalid amount or account not active"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PutMapping("/{id}/deposit")
    public ResponseEntity<AccountResponseDTO> deposit(@PathVariable(name = "id") Long id,
                                                      @Valid @RequestBody UpdateBalanceRequestDTO request) throws BadRequestException {
        AccountResponseDTO updatedAccount = accountService.updateAccountBalance(id, request.getAmount(), TransactionType.DEPOSIT);
        return ResponseEntity.ok(updatedAccount);
    }

    @Operation(summary = "Simulate a withdrawal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Withdrawal successful, balance updated"),
            @ApiResponse(responseCode = "400", description = "Invalid amount, insufficient funds, or account not active"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PutMapping("/{id}/withdraw")
    public ResponseEntity<AccountResponseDTO> withdraw(@PathVariable(name = "id") Long id,
                                                       @Valid @RequestBody UpdateBalanceRequestDTO request) throws BadRequestException {
        AccountResponseDTO updatedAccount = accountService.updateAccountBalance(id, request.getAmount(), TransactionType.WITHDRAWAL);
        return ResponseEntity.ok(updatedAccount);
    }

    @Operation(summary = "Close an account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account closed successfully"),
            @ApiResponse(responseCode = "400", description = "Account balance is not zero"),
            @ApiResponse(responseCode = "404", description = "Account not found")
    })
    @PutMapping("/{id}/close")
    public ResponseEntity<AccountResponseDTO> closeAccount(@PathVariable(name = "id") Long id) throws BadRequestException {
        AccountResponseDTO closedAccount = accountService.closeAccount(id);
        return ResponseEntity.ok(closedAccount);
    }
}