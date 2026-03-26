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

        requestMock = new ReclamoRequestDto();
        requestMock.setIdentificacionCliente("1712345678");
        requestMock.setTipoReclamo(TipoReclamo.TARJETAS_CREDITO);
        requestMock.setDetalleReclamo("Cobro duplicado en mi tarjeta de crédito por $45.50");
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
            assertThat(resultado.getId()).isEqualTo(1L);
            assertThat(resultado.getIdentificacionCliente()).isEqualTo("1712345678");
            assertThat(resultado.getNombresCliente()).isEqualTo("Juan Carlos");
            assertThat(resultado.getApellidosCliente()).isEqualTo("Pérez López");
            assertThat(resultado.getTipoReclamo()).isEqualTo(TipoReclamo.TARJETAS_CREDITO);
            assertThat(resultado.getDetalleReclamo()).isEqualTo("Cobro duplicado en mi tarjeta de crédito por $45.50");
            assertThat(resultado.getFechaCreacion()).isNotNull();

            verify(clienteRepository, times(1)).findByIdentificacion("1712345678");
            verify(reclamoRepository, times(1)).save(any(Reclamo.class));
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el cliente no existe")
        void debeLanzarExcepcionSiClienteNoExiste() {
            requestMock.setIdentificacionCliente("9999999999");
            when(clienteRepository.findByIdentificacion("9999999999"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> reclamoService.registrarReclamo(requestMock))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Cliente")
                    .hasMessageContaining("9999999999");

            verify(reclamoRepository, never()).save(any(Reclamo.class));
        }

        @Test
        @DisplayName("Debe persistir el reclamo con el tipo correcto")
        void debePersistirConTipoCorrecto() {
            requestMock.setTipoReclamo(TipoReclamo.TRANSFERENCIAS);

            Reclamo reclamoGuardado = Reclamo.builder()
                    .id(2L)
                    .cliente(clienteMock)
                    .tipoReclamo(TipoReclamo.TRANSFERENCIAS)
                    .detalleReclamo(requestMock.getDetalleReclamo())
                    .fechaCreacion(LocalDateTime.now())
                    .build();

            when(clienteRepository.findByIdentificacion("1712345678"))
                    .thenReturn(Optional.of(clienteMock));
            when(reclamoRepository.save(any(Reclamo.class)))
                    .thenReturn(reclamoGuardado);
            reclamoService.registrarReclamo(requestMock);

            ArgumentCaptor<Reclamo> reclamoCaptor = ArgumentCaptor.forClass(Reclamo.class);
            verify(reclamoRepository).save(reclamoCaptor.capture());

            Reclamo reclamoCapturado = reclamoCaptor.getValue();
            assertThat(reclamoCapturado.getTipoReclamo()).isEqualTo(TipoReclamo.TRANSFERENCIAS);
            assertThat(reclamoCapturado.getCliente().getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Debe funcionar con tipo PAGO_SERVICIOS")
        void debeFuncionarConPagoServicios() {
            requestMock.setTipoReclamo(TipoReclamo.PAGO_SERVICIOS);
            requestMock.setDetalleReclamo("Pago de luz no procesado correctamente");

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
            assertThat(resultado.getTipoReclamo()).isEqualTo(TipoReclamo.PAGO_SERVICIOS);
        }
    }
}