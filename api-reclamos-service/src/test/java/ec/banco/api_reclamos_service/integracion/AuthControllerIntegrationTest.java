package ec.banco.api_reclamos_service.integracion;


import com.fasterxml.jackson.databind.ObjectMapper;

import ec.banco.api_reclamos_service.entity.Cliente;
import ec.banco.api_reclamos_service.repository.ClienteRepository;
import ec.banco.commons.api_commons.dto.auth.LoginRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para AuthController.
 * Levanta el contexto completo de Spring con H2 en memoria.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("AuthController - Tests de Integración")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        clienteRepository.deleteAll();

        clienteRepository.save(Cliente.builder()
                .identificacion("1712345678")
                .nombres("Juan Carlos")
                .apellidos("Pérez López")
                .password(passwordEncoder.encode("juan1234"))
                .activo(true)
                .build());
    }

    @Nested
    @DisplayName("POST /v1/api/auth/login")
    class LoginEndpoint {

        @Test
        @DisplayName("Debe retornar 200 y JWT cuando las credenciales son correctas")
        void debeRetornar200ConCredencialesCorrectas() throws Exception {
            LoginRequestDto request = new LoginRequestDto();
            request.setIdentificacion("1712345678");
            request.setPassword("juan1234");

            mockMvc.perform(post("/v1/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").isNotEmpty())
                    .andExpect(jsonPath("$.identificacion").value("1712345678"))
                    .andExpect(jsonPath("$.nombres").value("Juan Carlos"))
                    .andExpect(jsonPath("$.apellidos").value("Pérez López"))
                    .andExpect(jsonPath("$.expiresIn").isNumber());
        }

        @Test
        @DisplayName("Debe retornar 401 cuando el password es incorrecto")
        void debeRetornar401ConPasswordIncorrecto() throws Exception {
            LoginRequestDto request = new LoginRequestDto();
            request.setIdentificacion("1712345678");
            request.setPassword("passwordMalo");

            mockMvc.perform(post("/v1/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Credenciales inválidas"));
        }

        @Test
        @DisplayName("Debe retornar 401 cuando la identificación no existe")
        void debeRetornar401ConIdentificacionInexistente() throws Exception {
            LoginRequestDto request = new LoginRequestDto();
            request.setIdentificacion("9999999999");
            request.setPassword("cualquierPassword");

            mockMvc.perform(post("/v1/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Debe retornar 400 cuando faltan campos obligatorios")
        void debeRetornar400CuandoFaltanCampos() throws Exception {
            LoginRequestDto request = new LoginRequestDto();
            request.setIdentificacion("");
            request.setPassword("");

            mockMvc.perform(post("/v1/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.validationErrors").isNotEmpty());
        }
    }
}
