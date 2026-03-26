package ec.banco.api_reclamos_service.service;

import ec.banco.api_reclamos_service.dto.response.ClienteResponseDto;

public interface ClienteService {

    /**
     * Este metodo consulta un cliente por identifiacion.
     *
     * @param identificacion numero de indentificacion como filtro
     * @return informacion de cliente como DTO en caso de existir
     */
    ClienteResponseDto buscarPorIdentificacion(String identificacion);
}
