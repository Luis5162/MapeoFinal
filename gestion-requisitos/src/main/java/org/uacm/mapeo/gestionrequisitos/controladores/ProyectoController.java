package org.uacm.mapeo.gestionrequisitos.controladores;
import org.uacm.mapeo.gestionrequisitos.entidades.Proyecto;
import org.uacm.mapeo.gestionrequisitos.entidades.Empresa;
import org.uacm.mapeo.gestionrequisitos.Repositorios.ProyectoRepository;
import org.uacm.mapeo.gestionrequisitos.Repositorios.EmpresaRepository; // Necesario para buscar la empresa
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/proyectos")
@CrossOrigin(origins = "*")
public class ProyectoController {

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    // 1. LISTAR TODO
    @GetMapping
    public Page<Proyecto> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return proyectoRepository.findAll(pageable);
    }
    // 2. BUSCAR POR ID (GET)
    @GetMapping("/{id}")
    public ResponseEntity<Proyecto> obtenerPorId(@PathVariable Integer id) {
        Optional<Proyecto> proyecto = proyectoRepository.findById(id);
        if (proyecto.isPresent()) {
            return ResponseEntity.ok(proyecto.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 3. ACTUALIZAR (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<Proyecto> actualizar(@PathVariable Integer id, @RequestBody Proyecto datosNuevos) {
        Optional<Proyecto> proyectoExistente = proyectoRepository.findById(id);

        if (proyectoExistente.isPresent()) {
            Proyecto p = proyectoExistente.get();

            // Actualizamos datos básicos
            p.setNombre(datosNuevos.getNombre());
            p.setFechaInicio(datosNuevos.getFechaInicio());
            p.setActivo(datosNuevos.getActivo());

            // Actualizamos la EMPRESA (Relación)
            // Verificamos si nos mandaron una empresa nueva
            if (datosNuevos.getEmpresa() != null && datosNuevos.getEmpresa().getIdEmpresa() != null) {
                // Buscamos la empresa en la BD para asegurarnos que existe
                Optional<Empresa> empresaNueva = empresaRepository.findById(datosNuevos.getEmpresa().getIdEmpresa());
                if (empresaNueva.isPresent()) {
                    p.setEmpresa(empresaNueva.get());
                }
            }

            Proyecto guardado = proyectoRepository.save(p);
            return ResponseEntity.ok(guardado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 4. ELIMINAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (proyectoRepository.existsById(id)) {
            proyectoRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
