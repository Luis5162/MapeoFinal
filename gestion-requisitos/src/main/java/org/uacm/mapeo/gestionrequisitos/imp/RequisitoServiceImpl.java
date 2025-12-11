package org.uacm.mapeo.gestionrequisitos.imp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.uacm.mapeo.gestionrequisitos.entidades.Requisito;
import org.uacm.mapeo.gestionrequisitos.Repositorios.RequisitoRepository;
import org.uacm.mapeo.gestionrequisitos.servicios.RequisitoService;

import java.util.List;

@Service
public class RequisitoServiceImpl implements RequisitoService {

    @Autowired
    private RequisitoRepository repositorio;

    @Override
    @Transactional(readOnly = true) // Solo lectura es más rápido
    public List<Requisito> listarTodos() {
        return repositorio.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Requisito> listarPaginado(Pageable pageable) {
        return repositorio.findAll(pageable);
    }

    @Override
    @Transactional // Escritura
    public Requisito guardar(Requisito requisito) {
        // REGLA DE NEGOCIO (Ejemplo):
        // Si viene un origen, nos aseguramos que se vincule correctamente al requisito
        // antes de guardar, aunque el CascadeType.ALL ayuda, esto refuerza la relación.
        if (requisito.getOrigen() != null) {
            requisito.getOrigen().setRequisito(requisito);
        }

        return repositorio.save(requisito);
    }

    @Override
    @Transactional(readOnly = true)
    public Requisito obtenerPorId(Integer id) {
        return repositorio.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        repositorio.deleteById(id);
    }
}