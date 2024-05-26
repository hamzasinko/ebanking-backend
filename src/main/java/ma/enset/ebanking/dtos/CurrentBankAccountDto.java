package ma.enset.ebanking.dtos;

import lombok.Data;
import ma.enset.ebanking.enums.AccountStatus;

import java.util.Date;


@Data
public class CurrentBankAccountDto extends BankAccountDto {
    private String id;
    private double balance;
    private Date createdAt;
    private AccountStatus status;
    private CustomerDto customerDto;
    private double overDraft;
}
