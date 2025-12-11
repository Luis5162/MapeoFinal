package org.uacm.mapeo.gestionrequisitos.Repositorios;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.uacm.mapeo.gestionrequisitos.entidades.Requisito;

import java.util.List;

@RepositoryRestResource(path = "requisitos", collectionResourceRel = "requisitos")
public interface RequisitoRepository extends JpaRepository<Requisito, Integer> {

    // Método útil para filtrar requisitos por proyecto
    List<Requisito> findByProyectoIdProyecto(Integer idProyecto);

    // Método para buscar requisitos por proyecto con paginación
    Page<Requisito> findByProyectoIdProyecto(Integer idProyecto, Pageable pageable);

    // Método para buscar requisitos por nombre (usando paginación)
    Page<Requisito> findByNombreContaining(String nombre, Pageable pageable);
}