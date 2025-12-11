package org.uacm.mapeo.gestionrequisitos.servicios;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.uacm.mapeo.gestionrequisitos.entidades.Requisito;

import java.util.List;

public interface RequisitoService {

    // Obtener todos (sin paginar, para reportes simples)
    List<Requisito> listarTodos();

    // Obtener paginado (para la tabla principal de la vista)
    Page<Requisito> listarPaginado(Pageable pageable);

    // Guardar (crear o editar). Maneja también el guardado del OrigenRequisito
    Requisito guardar(Requisito requisito);

    // Buscar uno solo para editarlo
    Requisito obtenerPorId(Integer id);

    // Eliminar
    void eliminar(Integer id);
}