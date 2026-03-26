package ec.banco.api_reclamos_service.integracion;


import com.fasterxml.jackson.databind.ObjectMapper;
import ec.banco.api_reclamos_service.dto.request.ReclamoRequestDto;
import ec.banco.api_reclamos_service.entity.Cliente;
import ec.banco.api_reclamos_service.enumerado.TipoReclamo;
import ec.banco.api_reclamos_service.repository.ClienteRepository;
import ec.banco.api_reclamos_service.repository.ReclamoRepository;
import ec.banco.commons.api_commons.dto.auth.LoginRequestDto;
import ec.banco.commons.api_commons.dto.auth.LoginResponseDto;
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
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para ClienteController y ReclamoController.
 * Verifica el flujo completo: login → consultar cliente → registrar reclamo.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Reclamos API - Tests de Integración")
class ReclamosIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private ReclamoRepository reclamoRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private String jwtToken;

    @BeforeEach
    void setUp() throws Exception {
        reclamoRepository.deleteAll();
        clienteRepository.deleteAll();

        // Crear cliente de prueba
        clienteRepository.save(Cliente.builder()
                .identificacion("1712345678")
                .nombres("Juan Carlos")
                .apellidos("Pérez López")
                .password(passwordEncoder.encode("juan1234"))
                .activo(true)
                .build());

        clienteRepository.save(Cliente.builder()
                .identificacion("1798765432")
                .nombres("María Elena")
                .apellidos("González Ruiz")
                .password(passwordEncoder.encode("maria1234"))
                .activo(true)
                .build());

        // Obtener JWT para tests autenticados
        jwtToken = obtenerToken("1712345678", "juan1234");
    }

    /**
     * Helper: hace login y retorna el JWT.
     */
    private String obtenerToken(String identificacion, String password) throws Exception {
        LoginRequestDto loginRequest = new LoginRequestDto();
        loginRequest.setIdentificacion(identificacion);
        loginRequest.setPassword(password);

        MvcResult result = mockMvc.perform(post("/v1/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        LoginResponseDto loginResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(), LoginResponseDto.class);

        return loginResponse.getToken();
    }

    // CLIENTE CONTROLLER
    @Nested
    @DisplayName("GET /v1/api/clientes/{identificacion}")
    class ConsultarCliente {

        @Test
        @DisplayName("Debe retornar 200 con datos del cliente cuando existe")
        void debeRetornarClienteCuandoExiste() throws Exception {
            mockMvc.perform(get("/v1/api/clientes/1712345678")
                            .header("Authorization", "Bearer " + jwtToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.identificacion").value("1712345678"))
                    .andExpect(jsonPath("$.nombres").value("Juan Carlos"))
                    .andExpect(jsonPath("$.apellidos").value("Pérez López"));
        }

        @Test
        @DisplayName("Debe retornar 404 cuando el cliente no existe")
        void debeRetornar404CuandoNoExiste() throws Exception {
            mockMvc.perform(get("/v1/api/clientes/9999999999")
                            .header("Authorization", "Bearer " + jwtToken))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").exists());
        }

        @Test
        @DisplayName("Debe poder consultar otro cliente diferente al logueado")
        void debePoderConsultarOtroCliente() throws Exception {
            mockMvc.perform(get("/v1/api/clientes/1798765432")
                            .header("Authorization", "Bearer " + jwtToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.identificacion").value("1798765432"))
                    .andExpect(jsonPath("$.nombres").value("María Elena"));
        }
    }

    // RECLAMO CONTROLLER
    @Nested
    @DisplayName("POST /v1/api/reclamos")
    class RegistrarReclamo {

        @Test
        @DisplayName("Debe retornar 201 cuando el reclamo se registra exitosamente")
        void debeRetornar201CuandoReclamoExitoso() throws Exception {

            ReclamoRequestDto request = new ReclamoRequestDto(
                    "1712345678",
                    TipoReclamo.TARJETAS_CREDITO,
                    "Cobro duplicado en mi tarjeta de crédito por $45.50");


            mockMvc.perform(post("/v1/api/reclamos")
                            .header("Authorization", "Bearer " + jwtToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isNumber())
                    .andExpect(jsonPath("$.identificacionCliente").value("1712345678"))
                    .andExpect(jsonPath("$.nombresCliente").value("Juan Carlos"))
                    .andExpect(jsonPath("$.apellidosCliente").value("Pérez López"))
                    .andExpect(jsonPath("$.tipoReclamo").value("TARJETAS_CREDITO"))
                    .andExpect(jsonPath("$.detalleReclamo").value("Cobro duplicado en mi tarjeta de crédito por $45.50"))
                    .andExpect(jsonPath("$.fechaCreacion").isNotEmpty());
        }

        @Test
        @DisplayName("Debe retornar 201 con tipo TRANSFERENCIAS")
        void debeRegistrarConTipoTransferencias() throws Exception {
            ReclamoRequestDto request = new ReclamoRequestDto(
                    "1798765432",
                    TipoReclamo.TRANSFERENCIAS,
                    "Transferencia de $200 no reflejada en cuenta destino");

            mockMvc.perform(post("/v1/api/reclamos")
                            .header("Authorization", "Bearer " + jwtToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.tipoReclamo").value("TRANSFERENCIAS"))
                    .andExpect(jsonPath("$.nombresCliente").value("María Elena"));
        }

        @Test
        @DisplayName("Debe retornar 201 con tipo PAGO_SERVICIOS")
        void debeRegistrarConTipoPagoServicios() throws Exception {
            ReclamoRequestDto request = new ReclamoRequestDto(
                    "1712345678",
                    TipoReclamo.PAGO_SERVICIOS,
                    "Pago de servicio eléctrico no fue procesado correctamente");

            mockMvc.perform(post("/v1/api/reclamos")
                            .header("Authorization", "Bearer " + jwtToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.tipoReclamo").value("PAGO_SERVICIOS"));
        }

        @Test
        @DisplayName("Debe retornar 404 cuando el cliente del reclamo no existe")
        void debeRetornar404ConClienteInexistente() throws Exception {
            ReclamoRequestDto request = new ReclamoRequestDto(
                    "9999999999",
                    TipoReclamo.TARJETAS_CREDITO,
                    "Reclamo con cliente inexistente para prueba");

            mockMvc.perform(post("/v1/api/reclamos")
                            .header("Authorization", "Bearer " + jwtToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Debe retornar 400 cuando faltan campos obligatorios")
        void debeRetornar400CuandoFaltanCampos() throws Exception {
            ReclamoRequestDto request = new ReclamoRequestDto("", null, "");

            mockMvc.perform(post("/v1/api/reclamos")
                            .header("Authorization", "Bearer " + jwtToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.validationErrors").isNotEmpty());
        }

        @Test
        @DisplayName("Debe retornar 400 cuando el detalle es muy corto")
        void debeRetornar400CuandoDetalleEsMuyCorto() throws Exception {
            ReclamoRequestDto request = new ReclamoRequestDto(
                    "1712345678",
                    TipoReclamo.TARJETAS_CREDITO,
                    "Corto");

            mockMvc.perform(post("/v1/api/reclamos")
                            .header("Authorization", "Bearer " + jwtToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.validationErrors.detalleReclamo").exists());
        }
    }

    // FLUJO COMPLETO E2E
    @Nested
    @DisplayName("Flujo Completo E2E")
    class FlujoCompleto {

        @Test
        @DisplayName("Login → Consultar Cliente → Registrar Reclamo (flujo completo)")
        void flujoCompletoExitoso() throws Exception {
            String token = obtenerToken("1798765432", "maria1234");
            mockMvc.perform(get("/v1/api/clientes/1798765432")
                            .header("Authorization", "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nombres").value("María Elena"));

            ReclamoRequestDto reclamo = new ReclamoRequestDto(
                    "1798765432",
                    TipoReclamo.TRANSFERENCIAS,
                    "Transferencia de $500 realizada pero no recibida en cuenta destino");

            mockMvc.perform(post("/v1/api/reclamos")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(reclamo)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isNumber())
                    .andExpect(jsonPath("$.nombresCliente").value("María Elena"))
                    .andExpect(jsonPath("$.tipoReclamo").value("TRANSFERENCIAS"));
        }
    }
}
