package ma.enset.ebanking.dtos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.enset.ebanking.entities.BankAccount;
import ma.enset.ebanking.enums.OperationType;

import java.util.Date;

@Data
public class AccountOperationDto {
    private Long id;
    private Date operationDate;
    private double amount;
    private OperationType type;
    private String description;
}
