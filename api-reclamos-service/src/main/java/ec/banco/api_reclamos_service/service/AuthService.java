package ec.banco.api_reclamos_service.service;

import ec.banco.commons.api_commons.dto.auth.LoginRequestDto;
import ec.banco.commons.api_commons.dto.auth.LoginResponseDto;

public interface AuthService {

    /**
     * Este metodo genera un acceso para el consumo de servicios.
     *
     * @param request DTO con informacion para generar el login
     * @return autoizacion de tipo token
     */
    LoginResponseDto login(LoginRequestDto request);
}
