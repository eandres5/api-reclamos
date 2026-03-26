package ec.banco.api_reclamos_service.service.impl;

import ec.banco.api_reclamos_service.dto.request.ReclamoRequestDto;
import ec.banco.api_reclamos_service.dto.response.ReclamoResponseDto;
import ec.banco.api_reclamos_service.entity.Cliente;
import ec.banco.api_reclamos_service.entity.Reclamo;
import ec.banco.api_reclamos_service.repository.ClienteRepository;
import ec.banco.api_reclamos_service.repository.ReclamoRepository;
import ec.banco.api_reclamos_service.service.ReclamoService;
import ec.banco.commons.api_commons.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReclamoServiceImpl implements ReclamoService {

    private final ReclamoRepository reclamoRepository;
    private final ClienteRepository clienteRepository;

    /**
     * Este metodo registra un reclamo por cliente.
     *
     * @param request DTO con informacion del reclamo
     * @return DTO con mensaje de exito
     */
    @Override
    @Transactional
    public ReclamoResponseDto registrarReclamo(final ReclamoRequestDto request) {
        log.info("Registrando reclamo para cliente: {}", request.identificacionCliente());

        Cliente cliente = clienteRepository
                .findByIdentificacion(request.identificacionCliente())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente", "identificación", request.identificacionCliente()));

        Reclamo reclamo = Reclamo.builder()
                .cliente(cliente)
                .tipoReclamo(request.tipoReclamo())
                .detalleReclamo(request.detalleReclamo())
                .build();

        Reclamo reclamoGuardado = reclamoRepository.save(reclamo);

        log.info("Reclamo registrado con ID: {}", reclamoGuardado.getId());

        return ReclamoResponseDto.builder()
                .id(reclamoGuardado.getId())
                .identificacionCliente(cliente.getIdentificacion())
                .nombresCliente(cliente.getNombres())
                .apellidosCliente(cliente.getApellidos())
                .tipoReclamo(reclamoGuardado.getTipoReclamo())
                .detalleReclamo(reclamoGuardado.getDetalleReclamo())
                .fechaCreacion(reclamoGuardado.getFechaCreacion())
                .build();
    }
}
