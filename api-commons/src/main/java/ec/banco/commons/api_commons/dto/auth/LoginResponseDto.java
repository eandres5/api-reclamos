package ec.banco.commons.api_commons.dto.auth;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDto {
    private String token;
    private String identificacion;
    private String nombres;
    private String apellidos;
    private long expiresIn;
}