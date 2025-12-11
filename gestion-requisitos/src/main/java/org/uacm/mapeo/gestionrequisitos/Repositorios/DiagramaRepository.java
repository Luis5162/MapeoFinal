package org.uacm.mapeo.gestionrequisitos.Repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource; // <--- IMPORTANTE
import org.uacm.mapeo.gestionrequisitos.entidades.Diagrama;
import org.uacm.mapeo.gestionrequisitos.entidades.TipoDiagrama;
import java.util.List;

// AL AGREGAR ESTO, Spring crea la API automática en: /api/v1/diagramas
@RepositoryRestResource(path = "diagramas", collectionResourceRel = "diagramas")
public interface DiagramaRepository extends JpaRepository<Diagrama, Integer> {

    // Este método se convertirá automáticamente en un buscador:
    // /api/v1/diagramas/search/findByTipoDiagrama?tipo=casos_de_uso
    List<Diagrama> findByTipoDiagrama(TipoDiagrama tipo);
}