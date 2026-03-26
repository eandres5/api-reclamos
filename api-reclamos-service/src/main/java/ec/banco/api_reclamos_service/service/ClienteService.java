package ec.banco.api_reclamos_service.service;

import ec.banco.api_reclamos_service.dto.response.ClienteResponseDto;

public interface ClienteService {

    ClienteResponseDto buscarPorIdentificacion(String identificacion);
}
