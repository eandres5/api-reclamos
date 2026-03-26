package ec.banco.api_reclamos_service.controller;

import ec.banco.api_reclamos_service.service.AuthService;
import ec.banco.commons.api_commons.dto.auth.LoginRequestDto;
import ec.banco.commons.api_commons.dto.auth.LoginResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints de login para clientes.")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica al cliente con su identificación (cédula) y contraseña. Retorna un token JWT."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso",
                    content = @Content(schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        LoginResponseDto response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
