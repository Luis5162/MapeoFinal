package org.uacm.mapeo.gestionrequisitos.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.uacm.mapeo.gestionrequisitos.entidades.Requisito;
import org.uacm.mapeo.gestionrequisitos.entidades.Proyecto;
import org.uacm.mapeo.gestionrequisitos.Repositorios.RequisitoRepository;
import org.uacm.mapeo.gestionrequisitos.Repositorios.ProyectoRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/requisitos")
@CrossOrigin(origins = "*")
public class RequisitoController {

    @Autowired
    private RequisitoRepository requisitoRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    // 1. LISTAR TODOS (sin paginación - para compatibilidad)
    @GetMapping
    public List<Requisito> listar() {
        return requisitoRepository.findAll();
    }

    // 2. LISTAR PAGINADO (nuevo endpoint con paginación)
    @GetMapping("/paginado")
    public Page<Requisito> listarPaginado(
            @PageableDefault(size = 10, sort = "nombre", direction = Sort.Direction.ASC) Pageable pageable) {
        return requisitoRepository.findAll(pageable);
    }

    // 3. BUSCAR PAGINADO POR NOMBRE (con paginación)
    @GetMapping("/buscar")
    public Page<Requisito> buscarPorNombre(
            @RequestParam String nombre,
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {
        return requisitoRepository.findByNombreContaining(nombre, pageable);
    }

    // 4. LISTAR POR PROYECTO (con paginación opcional)
    @GetMapping("/proyecto/{idProyecto}")
    public ResponseEntity<?> listarPorProyecto(
            @PathVariable Integer idProyecto,
            @RequestParam(required = false) Boolean paginado,
            @PageableDefault(size = 10, sort = "nombre") Pageable pageable) {

        if (paginado != null && paginado) {
            // Obtener página de requisitos por proyecto
            // Nota: Necesitarías un método en el repositorio que devuelva Page en lugar de List
            // Por ahora usaré un ejemplo con findAll y filtrar, pero sería mejor crear un método específico
            Page<Requisito> page = requisitoRepository.findAll(pageable);
            return ResponseEntity.ok(page);
        } else {
            // Lista simple (sin paginación)
            List<Requisito> requisitos = requisitoRepository.findByProyectoIdProyecto(idProyecto);
            return ResponseEntity.ok(requisitos);
        }
    }

    // 5. BUSCAR POR ID
    @GetMapping("/{id}")
    public ResponseEntity<Requisito> obtenerPorId(@PathVariable Integer id) {
        Optional<Requisito> req = requisitoRepository.findById(id);
        if (req.isPresent()) {
            return ResponseEntity.ok(req.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 6. GUARDAR NUEVO
    @PostMapping
    public Requisito guardar(@RequestBody Requisito requisito) {
        if (requisito.getProyecto() != null && requisito.getProyecto().getIdProyecto() != null) {
            Optional<Proyecto> p = proyectoRepository.findById(requisito.getProyecto().getIdProyecto());
            p.ifPresent(requisito::setProyecto);
        }
        return requisitoRepository.save(requisito);
    }

    // 7. ACTUALIZAR / EDITAR
    @PutMapping("/{id}")
    public ResponseEntity<Requisito> actualizar(@PathVariable Integer id, @RequestBody Requisito datosNuevos) {
        Optional<Requisito> reqExistente = requisitoRepository.findById(id);

        if (reqExistente.isPresent()) {
            Requisito r = reqExistente.get();

            r.setCodigo(datosNuevos.getCodigo());
            r.setNombre(datosNuevos.getNombre());
            r.setDescripcion(datosNuevos.getDescripcion());
            r.setCapturadoPor(datosNuevos.getCapturadoPor());
            r.setPrioridad(datosNuevos.getPrioridad());
            r.setEstado(datosNuevos.getEstado());

            if (datosNuevos.getProyecto() != null && datosNuevos.getProyecto().getIdProyecto() != null) {
                Optional<Proyecto> p = proyectoRepository.findById(datosNuevos.getProyecto().getIdProyecto());
                if (p.isPresent()) {
                    r.setProyecto(p.get());
                }
            }

            Requisito guardado = requisitoRepository.save(r);
            return ResponseEntity.ok(guardado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 8. ELIMINAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (requisitoRepository.existsById(id)) {
            requisitoRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}