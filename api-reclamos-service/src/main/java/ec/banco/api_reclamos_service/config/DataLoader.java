package ec.banco.api_reclamos_service.config;

import ec.banco.api_reclamos_service.entity.Cliente;
import ec.banco.api_reclamos_service.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(final String... args) {
        cargarClientes();
    }

    private void cargarClientes() {
        if (clienteRepository.count() > 0) {
            log.info("Clientes ya existen en la BDD, omitiendo carga inicial.");
            return;
        }

        log.info("Cargando clientes de prueba...");

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

        clienteRepository.save(Cliente.builder()
                .identificacion("0501234567")
                .nombres("Carlos Andrés")
                .apellidos("Ramírez Torres")
                .password(passwordEncoder.encode("carlos1234"))
                .activo(true)
                .build());

        clienteRepository.save(Cliente.builder()
                .identificacion("0912345678")
                .nombres("Ana Gabriela")
                .apellidos("Mendoza Sánchez")
                .password(passwordEncoder.encode("ana1234"))
                .activo(true)
                .build());

        clienteRepository.save(Cliente.builder()
                .identificacion("1104567890")
                .nombres("Luis Fernando")
                .apellidos("Castillo Herrera")
                .password(passwordEncoder.encode("luis1234"))
                .activo(true)
                .build());

        log.info("Clientes de prueba cargados exitosamente.");
    }
}
