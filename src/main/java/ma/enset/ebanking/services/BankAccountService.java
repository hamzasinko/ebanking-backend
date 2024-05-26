package ma.enset.ebanking.services;

import ma.enset.ebanking.dtos.*;
import ma.enset.ebanking.entities.BankAccount;
import ma.enset.ebanking.entities.CurrentAccount;
import ma.enset.ebanking.entities.Customer;
import ma.enset.ebanking.entities.SavingAccount;
import ma.enset.ebanking.exceptions.BalanceNotSufficientException;
import ma.enset.ebanking.exceptions.BankAccountNotFoundException;
import ma.enset.ebanking.exceptions.CustomerNotFoundException;

import java.util.List;

public interface BankAccountService {

    List<BankAccountDto> bankAccountList();
    CustomerDto saveCustomer(CustomerDto customer);
    CurrentBankAccountDto saveCurrrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException;
    SavingBankAccountDto saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException;
    List<CustomerDto> ListCustomer();
    BankAccountDto getBankAccount(String accountId) throws BankAccountNotFoundException;
    void debit(String accountId, double amount, String description) throws BankAccountNotFoundException, BalanceNotSufficientException;
    void credit(String accountId, double amount, String description) throws BankAccountNotFoundException;
    void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException;

    CustomerDto getCustomer(Long customerId) throws CustomerNotFoundException;

    CustomerDto updateCustomer(CustomerDto customerDto);

    void deleteCustomer(Long customerId);

    List<AccountOperationDto> accountHistory(String accountId);

    AccountHistoryDto getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException;
}
