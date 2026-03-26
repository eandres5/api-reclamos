package ec.banco.api_reclamos_service.service.impl;

import ec.banco.api_reclamos_service.dto.response.ClienteResponseDto;
import ec.banco.api_reclamos_service.entity.Cliente;
import ec.banco.api_reclamos_service.repository.ClienteRepository;
import ec.banco.api_reclamos_service.service.ClienteService;
import ec.banco.commons.api_commons.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    @Override
    @Transactional
    public ClienteResponseDto buscarPorIdentificacion(String identificacion) {
        log.info("Consultando cliente con identificación: {}", identificacion);

        Cliente cliente = clienteRepository.findByIdentificacion(identificacion)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente", "identificación", identificacion));

        return ClienteResponseDto.builder()
                .identificacion(cliente.getIdentificacion())
                .nombres(cliente.getNombres())
                .apellidos(cliente.getApellidos())
                .build();
    }
}
