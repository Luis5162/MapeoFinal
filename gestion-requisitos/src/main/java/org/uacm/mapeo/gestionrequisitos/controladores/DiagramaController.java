package org.uacm.mapeo.gestionrequisitos.controladores;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.uacm.mapeo.gestionrequisitos.entidades.Diagrama;
import org.uacm.mapeo.gestionrequisitos.entidades.Proyecto;
import org.uacm.mapeo.gestionrequisitos.Repositorios.DiagramaRepository;
import org.uacm.mapeo.gestionrequisitos.Repositorios.ProyectoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/diagramas") // La ruta que busca Python
@CrossOrigin(origins = "*")
public class DiagramaController {

    @Autowired
    private DiagramaRepository diagramaRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    // 1. LISTAR TODOS
    @GetMapping
    public Page<Diagrama> listar(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "5") int size) {
        return diagramaRepository.findAll(PageRequest.of(page, size));
    }

    // 2. BUSCAR POR ID (GET) - Para llenar el formulario de editar
    @GetMapping("/{id}")
    public ResponseEntity<Diagrama> obtenerPorId(@PathVariable Integer id) {
        Optional<Diagrama> diagrama = diagramaRepository.findById(id);
        if (diagrama.isPresent()) {
            return ResponseEntity.ok(diagrama.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 3. GUARDAR NUEVO (POST)
    @PostMapping
    public Diagrama guardar(@RequestBody Diagrama diagrama) {
        // Vinculamos el proyecto si viene el ID
        if (diagrama.getProyecto() != null && diagrama.getProyecto().getIdProyecto() != null) {
            Optional<Proyecto> p = proyectoRepository.findById(diagrama.getProyecto().getIdProyecto());
            p.ifPresent(diagrama::setProyecto);
        }
        return diagramaRepository.save(diagrama);
    }

    // 4. ACTUALIZAR (PUT) - Aquí ocurre la magia del editar
    @PutMapping("/{id}")
    public ResponseEntity<Diagrama> actualizar(@PathVariable Integer id, @RequestBody Diagrama datosNuevos) {
        Optional<Diagrama> diagExistente = diagramaRepository.findById(id);

        if (diagExistente.isPresent()) {
            Diagrama d = diagExistente.get();

            // Actualizamos campos de texto
            d.setNombre(datosNuevos.getNombre());
            d.setCreadoPor(datosNuevos.getCreadoPor());
            d.setArchivoRuta(datosNuevos.getArchivoRuta());

            // Actualizamos ENUMS
            // Spring intentará convertir el texto que viene de Python al Enum correspondiente
            d.setTipoDiagrama(datosNuevos.getTipoDiagrama());
            d.setEstado(datosNuevos.getEstado());

            // Actualizamos la relación con PROYECTO
            if (datosNuevos.getProyecto() != null && datosNuevos.getProyecto().getIdProyecto() != null) {
                Optional<Proyecto> p = proyectoRepository.findById(datosNuevos.getProyecto().getIdProyecto());
                if (p.isPresent()) {
                    d.setProyecto(p.get());
                }
            }

            Diagrama guardado = diagramaRepository.save(d);
            return ResponseEntity.ok(guardado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 5. ELIMINAR (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (diagramaRepository.existsById(id)) {
            diagramaRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}