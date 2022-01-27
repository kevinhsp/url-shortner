package theBigShortners.urlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    public static Object findByEmail;
    private static AccountRepository accountRepository;
    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    public static Account findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }
    public static void saveAccount(Account user) {
        accountRepository.save(user);
    }
}
