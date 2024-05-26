package ma.enset.ebanking.repositories;

import ma.enset.ebanking.entities.BankAccount;
import ma.enset.ebanking.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount,String> {
}
