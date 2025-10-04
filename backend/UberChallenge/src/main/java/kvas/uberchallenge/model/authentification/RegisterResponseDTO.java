package kvas.uberchallenge.model.authentification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class RegisterResponseDTO {
    @Builder.Default
    private String message = "Zaebis";
}
