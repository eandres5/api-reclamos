package ec.banco.api_reclamos_service.service;

import ec.banco.commons.api_commons.dto.auth.LoginRequestDto;
import ec.banco.commons.api_commons.dto.auth.LoginResponseDto;

public interface AuthService {

    LoginResponseDto login(LoginRequestDto request);
}
