package ec.banco.api_reclamos_service.dto.response;

import ec.banco.api_reclamos_service.enumerado.TipoReclamo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReclamoResponseDto {
    private Long id;
    private String identificacionCliente;
    private String nombresCliente;
    private String apellidosCliente;
    private TipoReclamo tipoReclamo;
    private String detalleReclamo;
    private LocalDateTime fechaCreacion;
}
