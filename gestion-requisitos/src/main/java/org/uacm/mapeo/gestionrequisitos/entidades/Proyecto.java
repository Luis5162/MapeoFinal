package org.uacm.mapeo.gestionrequisitos.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "proyecto")
@Data
public class Proyecto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proyecto")
    private Integer idProyecto;

    @NotBlank(message = "El nombre del proyecto es obligatorio")
    private String nombre;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    private Boolean activo = true;

    // Relación Muchos Proyectos -> 1 Empresa
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "id_empresa", nullable = false)
    private Empresa empresa;
}
