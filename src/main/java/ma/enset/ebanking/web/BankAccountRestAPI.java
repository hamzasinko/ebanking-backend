package ma.enset.ebanking.web;

import ma.enset.ebanking.dtos.*;
import ma.enset.ebanking.exceptions.BalanceNotSufficientException;
import ma.enset.ebanking.exceptions.BankAccountNotFoundException;
import ma.enset.ebanking.exceptions.CustomerNotFoundException;
import ma.enset.ebanking.exceptions.UnableToTransferException;
import ma.enset.ebanking.services.BankAccountService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
public class BankAccountRestAPI {
    private BankAccountService bankAccountService;

    public BankAccountRestAPI(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping("/account/{accountId}")
    public BankAccountDto getBankAccount(@PathVariable String accountId) throws BankAccountNotFoundException {
        return bankAccountService.getBankAccount(accountId);
    }
    @GetMapping("/account/customer/{customerId}")
    public List<BankAccountDto> getCustomerBankAccounts(@PathVariable Long customerId) throws CustomerNotFoundException {
        return bankAccountService.getCustomerBankAccounts(customerId);
    }
    @GetMapping("/account")
    public List<BankAccountDto> listAccounts(){
        return bankAccountService.bankAccountList();
    }
    @GetMapping("/account/{accountId}/operations")
    public List<AccountOperationDto> getHistory(@PathVariable String accountId){
        return bankAccountService.accountHistory(accountId);
    }
    @GetMapping("/account/{accountId}/pageOperations")
    public AccountHistoryDto getAccountHistory(
            @PathVariable String accountId,
            @RequestParam(name = "page",defaultValue = "0") int page,
            @RequestParam(name = "size",defaultValue = "5") int size) throws BankAccountNotFoundException {
        return bankAccountService.getAccountHistory(accountId,page,size);
    }
    @PostMapping("/account/debit")
    public DebitDto debit(@RequestBody DebitDto debitDto) throws BankAccountNotFoundException, BalanceNotSufficientException {
        this.bankAccountService.debit(debitDto.getAccountId(),debitDto.getAmount(),debitDto.getDescription());
        return debitDto;
    }
    @PostMapping("/account/credit")
    public CreditDto credit(@RequestBody CreditDto creditDto) throws BankAccountNotFoundException {
        this.bankAccountService.credit(creditDto.getAccountId(),creditDto.getAmount(),creditDto.getDescription());
        return creditDto;
    }
    @PostMapping("/account/transfer")
    public void transfer(@RequestBody TransferDto transferDto) throws BankAccountNotFoundException, BalanceNotSufficientException, UnableToTransferException {
        this.bankAccountService.transfer(transferDto.getAccountSource(),
                transferDto.getAccountDestination(),
                transferDto.getAmount());
    }
}
