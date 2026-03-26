package ec.banco.api_reclamos_service.repository;

import ec.banco.api_reclamos_service.entity.Reclamo;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReclamoRepository extends ListCrudRepository<Reclamo, Long> {

    /**
     * Este metodo retorna una lista de reclamos por identifiacion.
     *
     * @param identificacion numero de identificaion como filtro
     * @return lista de reclamos por indentificacion
     */
    List<Reclamo> findByClienteIdentificacionOrderByFechaCreacionDesc(String identificacion);
}