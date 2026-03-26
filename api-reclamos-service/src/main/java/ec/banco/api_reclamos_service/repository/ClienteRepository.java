package ec.banco.api_reclamos_service.repository;

import ec.banco.api_reclamos_service.entity.Cliente;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends ListCrudRepository<Cliente, Long> {

    /**
     * Este metodo consulta un cliente por identificacion.
     *
     * @param identificacion numero de identificacion a consultar
     * @return cliente filtrado por cedula
     */
    Optional<Cliente> findByIdentificacion(String identificacion);

    /**
     * Este metodo verifica si existe un registro por numero de identificacion.
     *
     * @param identificacion numero de identificacion a consultar
     * @return valor true o false
     */
    boolean existsByIdentificacion(String identificacion);
}
