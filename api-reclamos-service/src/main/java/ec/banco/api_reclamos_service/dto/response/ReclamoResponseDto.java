package ec.banco.api_reclamos_service.dto.response;

import ec.banco.api_reclamos_service.enumerado.TipoReclamo;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReclamoResponseDto(
        Long id,
        String identificacionCliente,
        String nombresCliente,
        String apellidosCliente,
        TipoReclamo tipoReclamo,
        String detalleReclamo,
        LocalDateTime fechaCreacion) {
}
