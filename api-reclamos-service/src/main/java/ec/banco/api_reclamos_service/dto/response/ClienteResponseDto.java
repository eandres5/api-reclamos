package ec.banco.api_reclamos_service.dto.response;

import lombok.Builder;

@Builder
public record ClienteResponseDto(
        String identificacion,
        String nombres,
        String apellidos) {
}
