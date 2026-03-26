package ec.banco.api_reclamos_service.controller;

import ec.banco.api_reclamos_service.dto.request.ReclamoRequestDto;
import ec.banco.api_reclamos_service.dto.response.ReclamoResponseDto;
import ec.banco.api_reclamos_service.service.ReclamoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/reclamos")
@RequiredArgsConstructor
@Tag(name = "Reclamos", description = "Endpoints para el registro de reclamos de clientes")
public class ReclamoController {

    private final ReclamoService reclamoService;

    /**
     * Este metodo crea un registro de reclamos.
     *
     * @param request informacion de reclamo en DTO
     * @return mensaje de respuesta exitoso
     */
    @Operation(
            summary = "Registrar un nuevo reclamo",
            description = "Crea un reclamo asociado a un cliente existente. Requiere identificación, tipo y detalle."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reclamo registrado exitosamente",
                    content = @Content(schema = @Schema(implementation = ReclamoResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    @PostMapping
    public ResponseEntity<ReclamoResponseDto> registrar(final @Valid @RequestBody ReclamoRequestDto request) {
        ReclamoResponseDto response = reclamoService.registrarReclamo(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
