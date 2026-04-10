package rs.yettelbank.bankaccountservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import rs.yettelbank.bankaccountservice.db.entity.Account;
import rs.yettelbank.bankaccountservice.db.repo.AccountRepo;
import rs.yettelbank.bankaccountservice.model.AccountStatus;
import rs.yettelbank.bankaccountservice.model.AccountType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class AccountRepoIntegrationTest extends AbstractIntegrationTest {

    private final AccountRepo accountRepository;

    @Autowired
    public AccountRepoIntegrationTest(AccountRepo accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Test
    void shouldSaveAndRetrieveAccountFromRealDb() {
        Account account = new Account();
        account.setClientId(101L);
        account.setAccountNumber("RS123-9999999");
        account.setAccountType(AccountType.SAVINGS);
        account.setCurrency(Account.Currency.EUR);
        account.setBalance(BigDecimal.valueOf(5000));
        account.setStatus(AccountStatus.ACTIVE);
        account.setOpenDate(LocalDate.now());

        Account saved = accountRepository.save(account);

        Optional<Account> found = accountRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getAccountNumber()).isEqualTo("RS123-9999999");
        assertThat(found.get().getBalance()).isEqualByComparingTo(BigDecimal.valueOf(5000));
    }
}