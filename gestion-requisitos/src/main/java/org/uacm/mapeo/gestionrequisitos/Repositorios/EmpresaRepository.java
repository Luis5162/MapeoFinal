package org.uacm.mapeo.gestionrequisitos.Repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.uacm.mapeo.gestionrequisitos.entidades.Empresa;

// Esta anotación crea automáticamente la ruta: /api/v1/empresas
@RepositoryRestResource(path = "empresas", collectionResourceRel = "empresas")
public interface EmpresaRepository extends JpaRepository<Empresa, Integer> {
    // No necesitas programar nada, ya tienes CRUD completo y paginación.
}