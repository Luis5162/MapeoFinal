package org.uacm.mapeo.gestionrequisitos.servicios;

import org.uacm.mapeo.gestionrequisitos.entidades.Empresa;
import org.uacm.mapeo.gestionrequisitos.entidades.Proyecto;

import java.util.List;

public interface CatalogoService {
    List<Empresa> obtenerTodasEmpresas();
    List<Proyecto> obtenerTodosProyectos();
    List<Proyecto> obtenerProyectosPorEmpresa(Integer idEmpresa);
}