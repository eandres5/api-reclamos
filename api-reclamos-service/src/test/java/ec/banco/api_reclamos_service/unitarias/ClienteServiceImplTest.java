package ec.banco.api_reclamos_service.unitarias;

import ec.banco.api_reclamos_service.dto.response.ClienteResponseDto;
import ec.banco.api_reclamos_service.entity.Cliente;
import ec.banco.api_reclamos_service.repository.ClienteRepository;
import ec.banco.api_reclamos_service.service.impl.ClienteServiceImpl;
import ec.banco.commons.api_commons.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.util.Optional;


@ExtendWith(MockitoExtension.class)
@DisplayName("ClienteService - Tests Unitarios")
class ClienteServiceImplTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    private Cliente clienteMock;

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
    }

    @Nested
    @DisplayName("buscarPorIdentificacion()")
    class BuscarPorIdentificacion {

        @Test
        @DisplayName("Debe retornar ClienteResponseDto cuando el cliente existe")
        void debeRetornarClienteCuandoExiste() {
            // Arrange
            when(clienteRepository.findByIdentificacion("1712345678"))
                    .thenReturn(Optional.of(clienteMock));

            ClienteResponseDto resultado = clienteService.buscarPorIdentificacion("1712345678");

            assertThat(resultado).isNotNull();
            assertThat(resultado.identificacion()).isEqualTo("1712345678");
            assertThat(resultado.nombres()).isEqualTo("Juan Carlos");
            assertThat(resultado.apellidos()).isEqualTo("Pérez López");

            verify(clienteRepository, times(1)).findByIdentificacion("1712345678");
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el cliente no existe")
        void debeLanzarExcepcionCuandoNoExiste() {
            when(clienteRepository.findByIdentificacion("9999999999"))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> clienteService.buscarPorIdentificacion("9999999999"))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Cliente")
                    .hasMessageContaining("9999999999");

            verify(clienteRepository, times(1)).findByIdentificacion("9999999999");
        }

        @Test
        @DisplayName("No debe exponer el password en el DTO de respuesta")
        void noDebeExponerPassword() {
            when(clienteRepository.findByIdentificacion("1712345678"))
                    .thenReturn(Optional.of(clienteMock));
            ClienteResponseDto resultado = clienteService.buscarPorIdentificacion("1712345678");
            assertThat(resultado).hasNoNullFieldsOrProperties();
            assertThat(resultado.getClass().getDeclaredFields())
                    .noneMatch(field -> field.getName().equals("password"));
        }
    }
}
