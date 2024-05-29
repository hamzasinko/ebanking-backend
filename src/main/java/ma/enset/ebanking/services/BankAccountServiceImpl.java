package ma.enset.ebanking.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.enset.ebanking.dtos.*;
import ma.enset.ebanking.entities.*;
import ma.enset.ebanking.enums.OperationType;
import ma.enset.ebanking.exceptions.BalanceNotSufficientException;
import ma.enset.ebanking.exceptions.BankAccountNotFoundException;
import ma.enset.ebanking.exceptions.CustomerNotFoundException;
import ma.enset.ebanking.exceptions.UnableToTransferException;
import ma.enset.ebanking.mapper.BankAccountMapperImpl;
import ma.enset.ebanking.repositories.AccountOperationRepository;
import ma.enset.ebanking.repositories.BankAccountRepository;
import ma.enset.ebanking.repositories.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j

public class BankAccountServiceImpl implements BankAccountService{
    private CustomerRepository customerRepository;
    private BankAccountRepository bankAccountRepository;
    private AccountOperationRepository accountOperationRepository;
    private BankAccountMapperImpl dtoMapper;

    @Override
    public CustomerDto saveCustomer(CustomerDto customerDto) {
        log.info("Saving new customer");
        Customer customer=dtoMapper.fromCustomerDto(customerDto);
        Customer savedCustomer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    public CurrentBankAccountDto saveCurrrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException {
        log.info("Saving new Current account");
        CurrentAccount currentBankAccount=new CurrentAccount();
        Customer customer=customerRepository.findById(customerId).orElse(null);
        if (customer == null)
            throw new CustomerNotFoundException("Customer not found");
        currentBankAccount.setId(UUID.randomUUID().toString());
        currentBankAccount.setCreatedAt(new Date());
        currentBankAccount.setBalance(initialBalance);
        currentBankAccount.setCustomer(customer);
        currentBankAccount.setOverDraft(overDraft);
        CurrentAccount savedCurrentAccount = bankAccountRepository.save(currentBankAccount);
        return dtoMapper.fromCurrentAccount(savedCurrentAccount);
    }

    @Override
    public SavingBankAccountDto saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException {
        log.info("Saving new Saving account");
        SavingAccount savingAccount=new SavingAccount();
        Customer customer=customerRepository.findById(customerId).orElse(null);
        if (customer == null)
            throw new CustomerNotFoundException("Customer not found");
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setCreatedAt(new Date());
        savingAccount.setBalance(initialBalance);
        savingAccount.setCustomer(customer);
        savingAccount.setInterestRate(interestRate);
        SavingAccount savedSavingAccount = bankAccountRepository.save(savingAccount);
        return dtoMapper.fromSavingAccount(savedSavingAccount);
    }


    @Override
    public List<CustomerDto> ListCustomer() {
        List<Customer> customers = customerRepository.findAll();
        List<CustomerDto> customerDtos = customers.stream()
                .map(customer -> dtoMapper.fromCustomer(customer))
                .collect(Collectors.toList());
        return customerDtos;
    }

    @Override
    public BankAccountDto getBankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElseThrow(()->new BankAccountNotFoundException("BankAccount not found"));
        if(bankAccount instanceof SavingAccount) {
            SavingAccount savingAccount = (SavingAccount) bankAccount;
            return dtoMapper.fromSavingAccount(savingAccount);
        }else{
            CurrentAccount currentAccount = (CurrentAccount) bankAccount;
            return dtoMapper.fromCurrentAccount(currentAccount);
        }
    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElseThrow(()->new BankAccountNotFoundException("BankAccount not found"));
        if(bankAccount.getBalance()<amount)
            throw new BalanceNotSufficientException("Balance not sufficient");
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance()-amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElseThrow(()->new BankAccountNotFoundException("BankAccount not found"));
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance()+amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException, UnableToTransferException {
        if(accountIdSource.equals(accountIdDestination))
            throw new UnableToTransferException("An account can't transfer to itself");
        debit(accountIdSource,amount,"Transfer to "+accountIdDestination);
        credit(accountIdDestination,amount,"Transfer from "+accountIdSource);
    }
    @Override
    public List<BankAccountDto> bankAccountList(){
       List<BankAccount> bankAccounts = bankAccountRepository.findAll();
       List<BankAccountDto> bankAccountDtos = bankAccounts.stream().map(bankAccount -> {
           if(bankAccount instanceof CurrentAccount) {
               CurrentAccount currentAccount=(CurrentAccount) bankAccount;
               return dtoMapper.fromCurrentAccount(currentAccount);
           }else{
               SavingAccount savingAccount=(SavingAccount) bankAccount;
               return  dtoMapper.fromSavingAccount(savingAccount);
           }
       }).collect(Collectors.toList());
       return bankAccountDtos;
    }
    @Override
    public CustomerDto getCustomer(Long customerId) throws CustomerNotFoundException {
        Customer customer= customerRepository.findById(customerId)
                .orElseThrow(()->new CustomerNotFoundException("Customer Not found"));
        return dtoMapper.fromCustomer(customer);
    }
    @Override
    public CustomerDto updateCustomer(CustomerDto customerDto) {
        log.info("Saving new customer");
        Customer customer=dtoMapper.fromCustomerDto(customerDto);
        Customer savedCustomer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);
    }
    @Override
    public void deleteCustomer(Long customerId){
        customerRepository.deleteById(customerId);
    }
    @Override
    public List<AccountOperationDto> accountHistory(String accountId){
        List<AccountOperation> accountOperations = accountOperationRepository.findByBankAccount_Id(accountId);
        return accountOperations.stream().map(op -> dtoMapper.fromAccountOperation(op)).collect(Collectors.toList());
    }

    @Override
    public AccountHistoryDto getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException {
        BankAccount bankAccount=bankAccountRepository.findById(accountId).orElse(null);
        if(bankAccount==null) throw new BankAccountNotFoundException("Account not found");
        Page<AccountOperation> accountOperations = accountOperationRepository.findByBankAccount_IdOrderByOperationDateDesc(accountId, PageRequest.of(page,size));
        AccountHistoryDto accountHistoryDto=new AccountHistoryDto();
        List<AccountOperationDto> accountOperationDtos = accountOperations.getContent().stream().map(op->dtoMapper.fromAccountOperation(op)).collect(Collectors.toList());
        accountHistoryDto.setAccountOperationDtos(accountOperationDtos);
        accountHistoryDto.setAccountId(bankAccount.getId());
        accountHistoryDto.setBalance(bankAccount.getBalance());
        accountHistoryDto.setCurrentPage(page);
        accountHistoryDto.setPageSize(size);
        accountHistoryDto.setTotalPages(accountOperations.getTotalPages());
        return accountHistoryDto;
    }

    @Override
    public List<CustomerDto> searchCustomers(String keyword) {
        List<Customer> customers = customerRepository.searchCustomer(keyword);
        List<CustomerDto> customerDtos = customers.stream().map(customer -> dtoMapper.fromCustomer(customer)).collect(Collectors.toList());
        return customerDtos;
    }

    @Override
    public List<BankAccountDto> getCustomerBankAccounts(Long customerId) throws CustomerNotFoundException {
        Customer customer= customerRepository.findById(customerId)
                .orElseThrow(()->new CustomerNotFoundException("Customer Not found"));
        List<BankAccount> bankAccounts = bankAccountRepository.findByCustomer_Id(customerId);
            List<BankAccountDto> bankAccountDtos = bankAccounts.stream().map(bankAccount -> {
                if(bankAccount instanceof CurrentAccount) {
                    CurrentAccount currentAccount=(CurrentAccount) bankAccount;
                    return dtoMapper.fromCurrentAccount(currentAccount);
                }else{
                    SavingAccount savingAccount=(SavingAccount) bankAccount;
                    return  dtoMapper.fromSavingAccount(savingAccount);
                }
            }).collect(Collectors.toList());
        return bankAccountDtos;
    }

}
