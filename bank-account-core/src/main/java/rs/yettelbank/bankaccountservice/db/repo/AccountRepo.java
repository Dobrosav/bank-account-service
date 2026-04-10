package rs.yettelbank.bankaccountservice.db.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rs.yettelbank.bankaccountservice.db.entity.Account;
import rs.yettelbank.bankaccountservice.model.AccountStatus;
import rs.yettelbank.bankaccountservice.model.AccountType;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepo extends CrudRepository<Account, Long> {

    Account findByAccountNumber(String accountNumber);

    List<Account> findByClientId(Long clientId);

    List<Account> findByAccountTypeAndStatus(AccountType accountType, AccountStatus status);

    List<Account> findByAccountTypeOrStatus(AccountType accountType, AccountStatus status);

    Optional<Account> findByClientIdAndAccountType(Long clientId, AccountType accountType);

    List<Account> findByAccountType(AccountType accountType);

    List<Account> findByStatus(AccountStatus status);

    List<Account> findAll();
}
