package org.uacm.mapeo.gestionrequisitos.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.uacm.mapeo.gestionrequisitos.entidades.Empresa;
import org.uacm.mapeo.gestionrequisitos.entidades.Proyecto;
import org.uacm.mapeo.gestionrequisitos.Repositorios.EmpresaRepository;
import org.uacm.mapeo.gestionrequisitos.Repositorios.ProyectoRepository;
import org.uacm.mapeo.gestionrequisitos.servicios.CatalogoService;

import java.util.List;

@Service
public class CatalogoServiceImpl implements CatalogoService {

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Empresa> obtenerTodasEmpresas() {
        return empresaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Proyecto> obtenerTodosProyectos() {
        return proyectoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Proyecto> obtenerProyectosPorEmpresa(Integer idEmpresa) {
        return proyectoRepository.findByEmpresaIdEmpresa(idEmpresa);
    }
}