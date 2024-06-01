package ma.enset.ebanking.web;

import ma.enset.ebanking.dtos.*;
import ma.enset.ebanking.exceptions.BalanceNotSufficientException;
import ma.enset.ebanking.exceptions.BankAccountNotFoundException;
import ma.enset.ebanking.exceptions.CustomerNotFoundException;
import ma.enset.ebanking.exceptions.UnableToTransferException;
import ma.enset.ebanking.services.BankAccountService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin("*")
public class BankAccountRestAPI {
    private BankAccountService bankAccountService;

    public BankAccountRestAPI(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping("/account/{accountId}")
    @PreAuthorize("hasAnyAuthority('SCOPE_USER')")
    public BankAccountDto getBankAccount(@PathVariable String accountId) throws BankAccountNotFoundException {
        return bankAccountService.getBankAccount(accountId);
    }
    @GetMapping("/account/customer/{customerId}")
    @PreAuthorize("hasAnyAuthority('SCOPE_USER')")
    public List<BankAccountDto> getCustomerBankAccounts(@PathVariable Long customerId) throws CustomerNotFoundException {
        return bankAccountService.getCustomerBankAccounts(customerId);
    }
    @GetMapping("/account")
    @PreAuthorize("hasAnyAuthority('SCOPE_USER')")
    public List<BankAccountDto> listAccounts(){
        return bankAccountService.bankAccountList();
    }
    @PostMapping("/account")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN')")
    public BankAccountDto saveBankAccount(@RequestBody Map<String, Object> request) throws CustomerNotFoundException {
        String accountType = (String) request.get("accountType");
        System.out.println(accountType);
        double balance = ((Number) request.get("balance")).doubleValue();
        System.out.println(balance);
        Double overDraft = request.containsKey("overDraft") ? ((Number) request.get("overDraft")).doubleValue() : null;
        System.out.println(overDraft);
        Double interestRate = request.containsKey("interestRate") ? ((Number) request.get("interestRate")).doubleValue() : null;
        System.out.println(interestRate);
        Integer customerId = (Integer) request.get("customerId");
        System.out.println(customerId);
        return bankAccountService.saveBankAccount(accountType, balance, overDraft, interestRate, Long.valueOf(customerId));
    }
    @GetMapping("/account/{accountId}/operations")
    @PreAuthorize("hasAnyAuthority('SCOPE_USER')")
    public List<AccountOperationDto> getHistory(@PathVariable String accountId){
        return bankAccountService.accountHistory(accountId);
    }
    @GetMapping("/account/{accountId}/pageOperations")
    @PreAuthorize("hasAnyAuthority('SCOPE_USER')")
    public AccountHistoryDto getAccountHistory(
            @PathVariable String accountId,
            @RequestParam(name = "page",defaultValue = "0") int page,
            @RequestParam(name = "size",defaultValue = "5") int size) throws BankAccountNotFoundException {
        return bankAccountService.getAccountHistory(accountId,page,size);
    }
    @PostMapping("/account/debit")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN')")
    public DebitDto debit(@RequestBody DebitDto debitDto) throws BankAccountNotFoundException, BalanceNotSufficientException {
        this.bankAccountService.debit(debitDto.getAccountId(),debitDto.getAmount(),debitDto.getDescription());
        return debitDto;
    }
    @PostMapping("/account/credit")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN')")
    public CreditDto credit(@RequestBody CreditDto creditDto) throws BankAccountNotFoundException {
        this.bankAccountService.credit(creditDto.getAccountId(),creditDto.getAmount(),creditDto.getDescription());
        return creditDto;
    }
    @PostMapping("/account/transfer")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN')")
    public void transfer(@RequestBody TransferDto transferDto) throws BankAccountNotFoundException, BalanceNotSufficientException, UnableToTransferException {
        this.bankAccountService.transfer(transferDto.getAccountSource(),
                transferDto.getAccountDestination(),
                transferDto.getAmount());
    }
}
