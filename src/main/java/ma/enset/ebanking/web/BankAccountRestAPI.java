package ma.enset.ebanking.web;

import ma.enset.ebanking.dtos.AccountHistoryDto;
import ma.enset.ebanking.dtos.AccountOperationDto;
import ma.enset.ebanking.dtos.BankAccountDto;
import ma.enset.ebanking.exceptions.BankAccountNotFoundException;
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
}
