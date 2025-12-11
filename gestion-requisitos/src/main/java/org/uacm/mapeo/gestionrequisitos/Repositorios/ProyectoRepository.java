package org.uacm.mapeo.gestionrequisitos.Repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.uacm.mapeo.gestionrequisitos.entidades.Proyecto;

import java.util.List;

// Esta anotación crea automáticamente la ruta: /api/v1/proyectos
@RepositoryRestResource(path = "proyectos", collectionResourceRel = "proyectos")
public interface ProyectoRepository extends JpaRepository<Proyecto, Integer> {

    // Método extra: Buscar proyectos por ID de empresa
    // Spring Data REST expone esto automáticamente como: /proyectos/search/por-empresa?idEmpresa=1
    List<Proyecto> findByEmpresaIdEmpresa(Integer idEmpresa);
}