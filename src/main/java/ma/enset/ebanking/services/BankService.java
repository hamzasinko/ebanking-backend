package ma.enset.ebanking.services;

import jakarta.transaction.Transactional;
import ma.enset.ebanking.entities.BankAccount;
import ma.enset.ebanking.entities.CurrentAccount;
import ma.enset.ebanking.entities.SavingAccount;
import ma.enset.ebanking.repositories.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class BankService {
    @Autowired
    private BankAccountRepository bankAccountRepository;
    public void consulter(){
        BankAccount bankAccount= bankAccountRepository.findById("642bde74-333a-4e5c-ad28-27b7ceaa89d7").orElse(null);
        if(bankAccount!=null) {
            System.out.println("**************");
            System.out.println(bankAccount.getId());
            System.out.println(bankAccount.getBalance());
            System.out.println(bankAccount.getStatus());
            System.out.println(bankAccount.getCreatedAt());
            System.out.println(bankAccount.getCustomer().getName());
            if (bankAccount instanceof CurrentAccount)
                System.out.println(((CurrentAccount) bankAccount).getOverDraft());
            else if (bankAccount instanceof SavingAccount)
                System.out.println(((SavingAccount) bankAccount).getInterestRate());
            bankAccount.getAccountOperations().forEach(op -> {
                System.out.println("********************");
                System.out.println(op.getType());
                System.out.println(op.getOperationDate());
                System.out.println(op.getAmount());

            });
        }
    }
}
