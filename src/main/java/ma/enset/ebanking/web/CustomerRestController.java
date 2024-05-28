package ma.enset.ebanking.web;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.enset.ebanking.dtos.CustomerDto;
import ma.enset.ebanking.entities.Customer;
import ma.enset.ebanking.exceptions.CustomerNotFoundException;
import ma.enset.ebanking.services.BankAccountService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@CrossOrigin("*")
public class CustomerRestController {
    private BankAccountService bankAccountService;

    @GetMapping("/customer/search")
    @PreAuthorize("hasAnyAuthority('SCOPE_USER')")
    public List<CustomerDto> searchCustomers(@RequestParam(name = "keyword", defaultValue = "") String keyword){
        return bankAccountService.searchCustomers("%"+keyword+"%");
    }
    @GetMapping("/customer")
    @PreAuthorize("hasAnyAuthority('SCOPE_USER')")
    public List<CustomerDto> customers(){
        return bankAccountService.ListCustomer();
    }
    @GetMapping("/customer/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_USER')")
    public CustomerDto getCustomer(@PathVariable(name = "id") Long customerId) throws CustomerNotFoundException {
        return bankAccountService.getCustomer(customerId);
    }
    @PostMapping("/customer")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN')")
    public CustomerDto saveCustomer(@RequestBody CustomerDto customerDto){
        return bankAccountService.saveCustomer(customerDto);
    }
    @PutMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN')")
    public CustomerDto updateCustomer(@PathVariable Long customerId, @RequestBody CustomerDto customerDto)
    {
        customerDto.setId(customerId);
        return bankAccountService.updateCustomer(customerDto);
    }
    @DeleteMapping("/customer/{id}")
    @PreAuthorize("hasAnyAuthority('SCOPE_ADMIN')")
    public void deleteCustomer(@PathVariable Long id){
        bankAccountService.deleteCustomer(id);
    }
}
