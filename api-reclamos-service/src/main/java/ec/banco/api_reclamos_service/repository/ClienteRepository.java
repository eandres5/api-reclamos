package ec.banco.api_reclamos_service.repository;

import ec.banco.api_reclamos_service.entity.Cliente;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends ListCrudRepository<Cliente, Long> {

    Optional<Cliente> findByIdentificacion(String identificacion);

    boolean existsByIdentificacion(String identificacion);
}
