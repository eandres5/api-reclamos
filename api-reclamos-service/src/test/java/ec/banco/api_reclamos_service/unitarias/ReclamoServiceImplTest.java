package ec.banco.api_reclamos_service.unitarias;


import ec.banco.api_reclamos_service.dto.request.ReclamoRequestDto;
import ec.banco.api_reclamos_service.dto.response.ReclamoResponseDto;
import ec.banco.api_reclamos_service.entity.Cliente;
import ec.banco.api_reclamos_service.entity.Reclamo;
import ec.banco.api_reclamos_service.enumerado.TipoReclamo;
import ec.banco.api_reclamos_service.repository.ClienteRepository;
import ec.banco.api_reclamos_service.repository.ReclamoRepository;
import ec.banco.api_reclamos_service.service.impl.ReclamoServiceImpl;
import ec.banco.commons.api_commons.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para ReclamoServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ReclamoService - Tests Unitarios")
class ReclamoServiceImplTest {

    @Mock
    private ReclamoRepository reclamoRepository;
    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ReclamoServiceImpl reclamoService;

    private Cliente clienteMock;
    private ReclamoRequestDto requestMock;

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

        requestMock = new ReclamoRequestDto(
                "1712345678",
                TipoReclamo.TARJETAS_CREDITO,
                "Cobro duplicado en mi tarjeta de crédito por $45.50");
    }

    @Nested
    @DisplayName("registrarReclamo()")
    class RegistrarReclamo {

        @Test
        @DisplayName("Debe registrar reclamo exitosamente y retornar DTO con datos completos")
        void debeRegistrarReclamoExitosamente() {
            Reclamo reclamoGuardado = Reclamo.builder()
                    .id(1L)
                    .cliente(clienteMock)
                    .tipoReclamo(TipoReclamo.TARJETAS_CREDITO)
                    .detalleReclamo("Cobro duplicado en mi tarjeta de crédito por $45.50")
                    .fechaCreacion(LocalDateTime.now())
                    .build();

            when(clienteRepository.findByIdentificacion("1712345678"))
                    .thenReturn(Optional.of(clienteMock));
            when(reclamoRepository.save(any(Reclamo.class)))
                    .thenReturn(reclamoGuardado);

            ReclamoResponseDto resultado = reclamoService.registrarReclamo(requestMock);

            assertThat(resultado).isNotNull();
            assertThat(resultado.id()).isEqualTo(1L);
            assertThat(resultado.identificacionCliente()).isEqualTo("1712345678");
            assertThat(resultado.nombresCliente()).isEqualTo("Juan Carlos");
            assertThat(resultado.apellidosCliente()).isEqualTo("Pérez López");
            assertThat(resultado.tipoReclamo()).isEqualTo(TipoReclamo.TARJETAS_CREDITO);
            assertThat(resultado.detalleReclamo()).isEqualTo("Cobro duplicado en mi tarjeta de crédito por $45.50");
            assertThat(resultado.fechaCreacion()).isNotNull();

            verify(clienteRepository, times(1)).findByIdentificacion("1712345678");
            verify(reclamoRepository, times(1)).save(any(Reclamo.class));
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el cliente no existe")
        void debeLanzarExcepcionSiClienteNoExiste() {
            requestMock = new ReclamoRequestDto(
                    "9999999999",
                    TipoReclamo.TARJETAS_CREDITO,
                    "Cobro duplicado en mi tarjeta de crédito por $45.50");

            when(clienteRepository.findByIdentificacion("9999999999"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> reclamoService.registrarReclamo(requestMock))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Cliente")
                    .hasMessageContaining("9999999999");

            verify(reclamoRepository, never()).save(any(Reclamo.class));
        }

        @Test
        @DisplayName("Debe funcionar con tipo PAGO_SERVICIOS")
        void debeFuncionarConPagoServicios() {

            requestMock = new ReclamoRequestDto(
                    "1712345678",
                    TipoReclamo.PAGO_SERVICIOS,
                    "Pago de luz no procesado correctamente");

            Reclamo reclamoGuardado = Reclamo.builder()
                    .id(3L)
                    .cliente(clienteMock)
                    .tipoReclamo(TipoReclamo.PAGO_SERVICIOS)
                    .detalleReclamo("Pago de luz no procesado correctamente")
                    .fechaCreacion(LocalDateTime.now())
                    .build();

            when(clienteRepository.findByIdentificacion("1712345678"))
                    .thenReturn(Optional.of(clienteMock));
            when(reclamoRepository.save(any(Reclamo.class)))
                    .thenReturn(reclamoGuardado);

            ReclamoResponseDto resultado = reclamoService.registrarReclamo(requestMock);
            assertThat(resultado.tipoReclamo()).isEqualTo(TipoReclamo.PAGO_SERVICIOS);
        }
    }
}