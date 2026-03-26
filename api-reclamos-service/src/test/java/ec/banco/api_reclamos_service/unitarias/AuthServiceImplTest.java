package ec.banco.api_reclamos_service.unitarias;


import ec.banco.api_reclamos_service.entity.Cliente;
import ec.banco.api_reclamos_service.repository.ClienteRepository;
import ec.banco.api_reclamos_service.service.impl.AuthServiceImpl;
import ec.banco.commons.api_commons.dto.auth.LoginRequestDto;
import ec.banco.commons.api_commons.dto.auth.LoginResponseDto;
import ec.banco.commons.api_commons.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - Tests Unitarios")
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtService jwtService;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private AuthServiceImpl authService;

    private Cliente clienteMock;
    private LoginRequestDto loginRequest;
    private UserDetails userDetailsMock;

    @BeforeEach
    void setUp() {
        clienteMock = Cliente.builder()
                .id(1L)
                .identificacion("1712345678")
                .nombres("Juan Carlos")
                .apellidos("Pérez López")
                .password("encodedPassword")
                .activo(true)
                .build();

        loginRequest = new LoginRequestDto();
        loginRequest.setIdentificacion("1712345678");
        loginRequest.setPassword("juan1234");

        userDetailsMock = User.builder()
                .username("1712345678")
                .password("encodedPassword")
                .authorities(Collections.emptyList())
                .build();
    }

    @Nested
    @DisplayName("login()")
    class Login {

        @Test
        @DisplayName("Debe retornar token JWT y datos del cliente cuando las credenciales son válidas")
        void debeRetornarTokenCuandoCredencialesValidas() {
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(new UsernamePasswordAuthenticationToken(userDetailsMock, null));
            when(clienteRepository.findByIdentificacion("1712345678"))
                    .thenReturn(Optional.of(clienteMock));
            when(userDetailsService.loadUserByUsername("1712345678"))
                    .thenReturn(userDetailsMock);
            when(jwtService.generateToken(userDetailsMock))
                    .thenReturn("jwt.token.generado");
            when(jwtService.getExpirationMs())
                    .thenReturn(3600000L);


            LoginResponseDto resultado = authService.login(loginRequest);

            assertThat(resultado).isNotNull();
            assertThat(resultado.getToken()).isEqualTo("jwt.token.generado");
            assertThat(resultado.getIdentificacion()).isEqualTo("1712345678");
            assertThat(resultado.getNombres()).isEqualTo("Juan Carlos");
            assertThat(resultado.getApellidos()).isEqualTo("Pérez López");
            assertThat(resultado.getExpiresIn()).isEqualTo(3600000L);

            verify(authenticationManager, times(1)).authenticate(any());
            verify(jwtService, times(1)).generateToken(userDetailsMock);
        }

        @Test
        @DisplayName("Debe lanzar BadCredentialsException cuando la contraseña es incorrecta")
        void debeLanzarExcepcionConPasswordIncorrecto() {
            loginRequest.setPassword("passwordIncorrecto");
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            assertThatThrownBy(() -> authService.login(loginRequest))
                    .isInstanceOf(BadCredentialsException.class);

            verify(clienteRepository, never()).findByIdentificacion(any());
            verify(jwtService, never()).generateToken(any());
        }

        @Test
        @DisplayName("Debe lanzar BadCredentialsException cuando la identificación no existe")
        void debeLanzarExcepcionConIdentificacionInvalida() {
            loginRequest.setIdentificacion("0000000000");
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Bad credentials"));

            assertThatThrownBy(() -> authService.login(loginRequest))
                    .isInstanceOf(BadCredentialsException.class);
        }
    }
}
