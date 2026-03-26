package ec.banco.api_reclamos_service.controller;

import ec.banco.api_reclamos_service.dto.response.ClienteResponseDto;
import ec.banco.api_reclamos_service.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Endpoints para Consulta de información de clientes.")
@SecurityRequirement(name = "bearerAuth")
public class ClienteController {

    private final ClienteService clienteService;

    @Operation(
            summary = "Consultar cliente por identificación",
            description = "Busca un cliente por su número de identificación (cédula). Retorna nombres y apellidos."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cliente encontrado",
                    content = @Content(schema = @Schema(implementation = ClienteResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @GetMapping("/{identificacion}")
    public ResponseEntity<ClienteResponseDto> buscarPorIdentificacion(final @PathVariable String identificacion) {
        ClienteResponseDto cliente = clienteService.buscarPorIdentificacion(identificacion);
        return ResponseEntity.ok(cliente);
    }
}
