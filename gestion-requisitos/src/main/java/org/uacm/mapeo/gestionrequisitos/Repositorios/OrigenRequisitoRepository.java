package org.uacm.mapeo.gestionrequisitos.Repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import org.uacm.mapeo.gestionrequisitos.entidades.OrigenRequisito;

@RepositoryRestResource(path = "origenRequisito", collectionResourceRel = "origenRequisito")
public interface OrigenRequisitoRepository extends JpaRepository<OrigenRequisito, Integer> {
    // Generalmente accedemos al origen a través del requisito, 
    // pero dejamos esto por si necesitas buscar orígenes directos.
}