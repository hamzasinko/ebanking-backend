package ma.enset.ebanking.mapper;

import ma.enset.ebanking.dtos.AccountOperationDto;
import ma.enset.ebanking.dtos.CurrentBankAccountDto;
import ma.enset.ebanking.dtos.CustomerDto;
import ma.enset.ebanking.dtos.SavingBankAccountDto;
import ma.enset.ebanking.entities.AccountOperation;
import ma.enset.ebanking.entities.CurrentAccount;
import ma.enset.ebanking.entities.Customer;
import ma.enset.ebanking.entities.SavingAccount;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
public class BankAccountMapperImpl {
    public CustomerDto fromCustomer(Customer customer){
        CustomerDto customerDto= new CustomerDto();
        BeanUtils.copyProperties(customer,customerDto);
        return customerDto;
    }
    public Customer fromCustomerDto(CustomerDto customerDto){
        Customer customer=new Customer();
        BeanUtils.copyProperties(customerDto,customer);
        return customer;
    }
    public SavingBankAccountDto fromSavingAccount(SavingAccount savingAccount){
        SavingBankAccountDto savingBankAccountDto=new SavingBankAccountDto();
        BeanUtils.copyProperties(savingAccount,savingBankAccountDto);
        savingBankAccountDto.setCustomerDto(fromCustomer(savingAccount.getCustomer()));
        savingBankAccountDto.setType(savingAccount.getClass().getSimpleName());
        return savingBankAccountDto;
    }
    public SavingAccount fromSavingBankAccountDto(SavingBankAccountDto savingBankAccountDto){
        SavingAccount savingAccount=new SavingAccount();
        BeanUtils.copyProperties(savingBankAccountDto,savingAccount);
        savingAccount.setCustomer(fromCustomerDto(savingBankAccountDto.getCustomerDto()));
        return savingAccount;
    }
    public CurrentBankAccountDto fromCurrentAccount(CurrentAccount currentAccount) {
        CurrentBankAccountDto currentBankAccountDto=new CurrentBankAccountDto();
        BeanUtils.copyProperties(currentAccount,currentBankAccountDto);
        currentBankAccountDto.setCustomerDto(fromCustomer(currentAccount.getCustomer()));
        currentBankAccountDto.setType(currentAccount.getClass().getSimpleName());
        return currentBankAccountDto;
    }
    public CurrentAccount fromCurrentBankAccountDto(CurrentBankAccountDto currentBankAccountDto) {
        CurrentAccount currentAccount=new CurrentAccount();
        BeanUtils.copyProperties(currentBankAccountDto,currentAccount);
        currentAccount.setCustomer(fromCustomerDto(currentBankAccountDto.getCustomerDto()));
        return currentAccount;
    }
    public AccountOperationDto fromAccountOperation(AccountOperation accountOperation){
        AccountOperationDto accountOperationDto = new AccountOperationDto();
        BeanUtils.copyProperties(accountOperation,accountOperationDto);
        return accountOperationDto;
    }
}