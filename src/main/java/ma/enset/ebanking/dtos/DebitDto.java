package ma.enset.ebanking.dtos;

import lombok.Data;

@Data
public class DebitDto {
    private String accountId;
    private double amount;
    private String description;
}
