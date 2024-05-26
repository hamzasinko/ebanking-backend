package ma.enset.ebanking.repositories;

import ma.enset.ebanking.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer,Long> {
}
