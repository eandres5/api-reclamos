package ec.banco.api_reclamos_service.dto.request;

import ec.banco.api_reclamos_service.enumerado.TipoReclamo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReclamoRequestDto {
    @NotBlank(message = "La identificación del cliente es obligatoria")
    private String identificacionCliente;
    @NotNull(message = "El tipo de reclamo es obligatorio")
    private TipoReclamo tipoReclamo;
    @NotBlank(message = "El detalle del reclamo es obligatorio")
    @Size(min = 10, max = 500, message = "El detalle debe tener entre 10 y 500 caracteres")
    private String detalleReclamo;
}
