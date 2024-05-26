package ma.enset.ebanking;

import ma.enset.ebanking.dtos.BankAccountDto;
import ma.enset.ebanking.dtos.CurrentBankAccountDto;
import ma.enset.ebanking.dtos.CustomerDto;
import ma.enset.ebanking.dtos.SavingBankAccountDto;
import ma.enset.ebanking.entities.*;
import ma.enset.ebanking.enums.AccountStatus;
import ma.enset.ebanking.enums.OperationType;
import ma.enset.ebanking.exceptions.BalanceNotSufficientException;
import ma.enset.ebanking.exceptions.BankAccountNotFoundException;
import ma.enset.ebanking.exceptions.CustomerNotFoundException;
import ma.enset.ebanking.repositories.AccountOperationRepository;
import ma.enset.ebanking.repositories.BankAccountRepository;
import ma.enset.ebanking.repositories.CustomerRepository;
import ma.enset.ebanking.services.BankAccountService;
import ma.enset.ebanking.services.BankService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class EbankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(EbankingApplication.class, args);
	}
	@Bean
	CommandLineRunner commandLineRunner(BankAccountService bankAccountService){
		return args -> {
			Stream.of("Hassan","Imane","Mohamed").forEach(name->{
				CustomerDto customer=new CustomerDto();
				customer.setName(name);
				customer.setEmail(name+"@gmail.com");
				bankAccountService.saveCustomer(customer);
			});
			bankAccountService.ListCustomer().forEach(customer -> {
                try {
                    bankAccountService.saveCurrrentBankAccount(Math.random()*90000,9000,customer.getId());
                	bankAccountService.saveSavingBankAccount(Math.random()*120000,5.5,customer.getId());
				} catch (CustomerNotFoundException e) {
                    e.printStackTrace();
                }
            });
			List<BankAccountDto> bankAccounts = bankAccountService.bankAccountList();
			for(BankAccountDto bankAccount:bankAccounts)
				for(int i=0;i<10;i++){
					String accountId;
					if(bankAccount instanceof SavingBankAccountDto) {
						accountId=((SavingBankAccountDto) bankAccount).getId();
					}else{
						accountId=((CurrentBankAccountDto) bankAccount).getId();
					}
					bankAccountService.credit(accountId,10000+Math.random()*120000,"Credit");
					bankAccountService.debit(accountId,1000+Math.random()*9000,"debit");
				}
		};
	}
	//@Bean
	CommandLineRunner start(CustomerRepository customerRepository,
							BankAccountRepository bankAccountRepository,
							AccountOperationRepository accountOperationRepository){
		return args -> {
			Stream.of("Hassan","Yassine","Aicha").forEach(
					name->{
						Customer customer=new Customer();
						customer.setName(name);
						customer.setEmail(name+"@gmail.com");
						customerRepository.save(customer);
					}
			);
			customerRepository.findAll().forEach(cust->{
				CurrentAccount currentAccount=new CurrentAccount();
				currentAccount.setId(UUID.randomUUID().toString());
				currentAccount.setBalance(Math.random()*90000);
				currentAccount.setCreatedAt(new Date());
				currentAccount.setStatus(AccountStatus.CREATED);
				currentAccount.setCustomer(cust);
				currentAccount.setOverDraft(9000);
				bankAccountRepository.save(currentAccount);
			});
			customerRepository.findAll().forEach(cust->{
				SavingAccount savingAccount=new SavingAccount();
				savingAccount.setId(UUID.randomUUID().toString());
				savingAccount.setBalance(Math.random()*90000);
				savingAccount.setCreatedAt(new Date());
				savingAccount.setStatus(AccountStatus.CREATED);
				savingAccount.setCustomer(cust);
				savingAccount.setInterestRate(5.5);
				bankAccountRepository.save(savingAccount);
			});
			bankAccountRepository.findAll().forEach(acc->{
				for(int i=0;i<10;i++)
				{
					AccountOperation accountOperation=new AccountOperation();
					accountOperation.setOperationDate(new Date());
					accountOperation.setAmount(Math.random()*12000);
					accountOperation.setType(Math.random()>0.5? OperationType.DEBIT:OperationType.CREDIT);
					accountOperation.setBankAccount(acc);
					accountOperationRepository.save(accountOperation);
				}
			});
		};
	}
}
