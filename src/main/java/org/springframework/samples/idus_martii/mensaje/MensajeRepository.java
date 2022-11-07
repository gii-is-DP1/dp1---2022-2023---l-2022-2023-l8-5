package org.springframework.samples.idus_martii.mensaje;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MensajeRepository extends CrudRepository<Mensaje, Integer>{
    List<Mensaje> findAll();
    Mensaje save(Mensaje mensaje);
    Optional<Mensaje> findById(int id);
}
