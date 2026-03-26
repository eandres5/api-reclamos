package ec.banco.commons.api_commons.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDto {

    @NotBlank(message = "La identificación es obligatoria")
    private String identificacion;
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}