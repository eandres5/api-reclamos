package ec.banco.api_reclamos_service.service.impl;

import ec.banco.api_reclamos_service.entity.Cliente;
import ec.banco.api_reclamos_service.repository.ClienteRepository;
import ec.banco.api_reclamos_service.service.AuthService;
import ec.banco.commons.api_commons.dto.auth.LoginRequestDto;
import ec.banco.commons.api_commons.dto.auth.LoginResponseDto;
import ec.banco.commons.api_commons.exception.ResourceNotFoundException;
import ec.banco.commons.api_commons.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final ClienteRepository clienteRepository;

    /**
     * Este metodo genera un acceso para el consumo de servicios.
     *
     * @param request DTO con informacion para generar el login
     * @return autoizacion de tipo token
     */
    @Override
    public LoginResponseDto login(final LoginRequestDto request) {
        log.info("Intento de login para cliente: {}", request.getIdentificacion());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getIdentificacion(),
                        request.getPassword()
                )
        );

        Cliente cliente = clienteRepository.findByIdentificacion(request.getIdentificacion())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente", "identificación", request.getIdentificacion()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getIdentificacion());
        String token = jwtService.generateToken(userDetails);

        return LoginResponseDto.builder()
                .token(token)
                .identificacion(cliente.getIdentificacion())
                .nombres(cliente.getNombres())
                .apellidos(cliente.getApellidos())
                .expiresIn(jwtService.getExpirationMs())
                .build();
    }
}
