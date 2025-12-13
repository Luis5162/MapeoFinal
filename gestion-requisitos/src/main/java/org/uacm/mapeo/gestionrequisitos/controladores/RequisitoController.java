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
import org.springframework.data.domain.PageRequest;

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
    public Page<Requisito> listar(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "5") int size) {
        return requisitoRepository.findAll(PageRequest.of(page, size));
    }

    // 2. BUSCAR POR ID (Necesario para que cargue el formulario de editar)
    @GetMapping("/{id}")
    public ResponseEntity<Requisito> obtenerPorId(@PathVariable Integer id) {
        Optional<Requisito> req = requisitoRepository.findById(id);
        if (req.isPresent()) {
            return ResponseEntity.ok(req.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 3. GUARDAR NUEVO (POST)
    @PostMapping
    public Requisito guardar(@RequestBody Requisito requisito) {
        // Lógica simple para asignar proyecto si viene solo el ID
        if (requisito.getProyecto() != null && requisito.getProyecto().getIdProyecto() != null) {
            Optional<Proyecto> p = proyectoRepository.findById(requisito.getProyecto().getIdProyecto());
            p.ifPresent(requisito::setProyecto);
        }
        return requisitoRepository.save(requisito);
    }

    // 4. ACTUALIZAR / EDITAR (PUT) - ¡ESTO ES LO QUE TE FALTABA!
    @PutMapping("/{id}")
    public ResponseEntity<Requisito> actualizar(@PathVariable Integer id, @RequestBody Requisito datosNuevos) {
        Optional<Requisito> reqExistente = requisitoRepository.findById(id);

        if (reqExistente.isPresent()) {
            Requisito r = reqExistente.get();

            // Actualizamos los datos
            r.setCodigo(datosNuevos.getCodigo());
            r.setNombre(datosNuevos.getNombre());
            r.setDescripcion(datosNuevos.getDescripcion());
            r.setCapturadoPor(datosNuevos.getCapturadoPor());
            r.setPrioridad(datosNuevos.getPrioridad());
            r.setEstado(datosNuevos.getEstado());

            // Actualizamos la relación con Proyecto
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

    // 5. ELIMINAR
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