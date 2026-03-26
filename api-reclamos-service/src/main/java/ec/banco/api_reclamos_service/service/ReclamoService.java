package ec.banco.api_reclamos_service.service;

import ec.banco.api_reclamos_service.dto.request.ReclamoRequestDto;
import ec.banco.api_reclamos_service.dto.response.ReclamoResponseDto;

public interface ReclamoService {

    /**
     * Este meotdo registra un reclamo.
     *
     * @param request informacion del reclamo a guardar en tipo DTO
     * @return Respuesta exito o error
     */
    ReclamoResponseDto registrarReclamo(ReclamoRequestDto request);
}
