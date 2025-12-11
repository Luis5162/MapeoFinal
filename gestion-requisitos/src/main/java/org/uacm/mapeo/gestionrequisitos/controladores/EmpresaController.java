package org.uacm.mapeo.gestionrequisitos.controladores;



import org.uacm.mapeo.gestionrequisitos.entidades.Empresa;
import org.uacm.mapeo.gestionrequisitos.Repositorios.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/empresas") // Esta es la ruta base
@CrossOrigin(origins = "*") // Permite conexiones externas
public class EmpresaController {

    @Autowired
    private EmpresaRepository empresaRepository;

    // 1. LISTAR TODAS (GET /api/empresas)
    @GetMapping
    public Page<Empresa> listarEmpresas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return empresaRepository.findAll(PageRequest.of(page, size));
    }

    // 2. BUSCAR POR ID (GET /api/empresas/{id}) -> ¡ESTO TE FALTABA!
    @GetMapping("/{id}")
    public ResponseEntity<Empresa> obtenerEmpresa(@PathVariable Integer id) {
        Optional<Empresa> empresa = empresaRepository.findById(id);

        if (empresa.isPresent()) {
            return ResponseEntity.ok(empresa.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 3. ACTUALIZAR / EDITAR (PUT /api/empresas/{id}) -> ¡ESTO TE FALTABA!
    @PutMapping("/{id}")
    public ResponseEntity<Empresa> actualizarEmpresa(@PathVariable Integer id, @RequestBody Empresa empresaDetalles) {
        Optional<Empresa> empresaOptional = empresaRepository.findById(id);

        if (empresaOptional.isPresent()) {
            Empresa empresa = empresaOptional.get();

            // Actualizamos los campos
            empresa.setNombre(empresaDetalles.getNombre());
            empresa.setActivo(empresaDetalles.getActivo());
            // Si tienes más campos, actualízalos aquí también

            Empresa empresaActualizada = empresaRepository.save(empresa);
            return ResponseEntity.ok(empresaActualizada);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 4. ELIMINAR (DELETE /api/empresas/{id})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEmpresa(@PathVariable Integer id) {
        if (empresaRepository.existsById(id)) {
            empresaRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}