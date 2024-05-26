package ma.enset.ebanking.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.enset.ebanking.entities.BankAccount;

import java.util.List;


@Data
public class CustomerDto {
    private Long id;
    private String name;
    private String email;
}
