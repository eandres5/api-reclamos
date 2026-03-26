package ec.banco.api_reclamos_service.service;

import ec.banco.api_reclamos_service.dto.request.ReclamoRequestDto;
import ec.banco.api_reclamos_service.dto.response.ReclamoResponseDto;

public interface ReclamoService {

    ReclamoResponseDto registrarReclamo(ReclamoRequestDto request);
}
