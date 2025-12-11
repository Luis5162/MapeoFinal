package org.uacm.mapeo.gestionrequisitos.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.ToString;
import java.time.LocalDateTime;

@Entity
@Table(name = "requisito")
@Data
public class Requisito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_requisito")
    private Integer idRequisito;

    private String codigo;

    @NotBlank(message = "El nombre del requisito es obligatorio")
    private String nombre;

    private String descripcion;

    @Enumerated(EnumType.STRING)
    private Prioridad prioridad;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "capturado_por")
    private String capturadoPor;

    @Enumerated(EnumType.STRING)
    private EstadoRequisito estado = EstadoRequisito.activo;

    // Relación Muchos Requisitos -> 1 Proyecto
    @ManyToOne
    @JoinColumn(name = "id_proyecto", nullable = false)
    private Proyecto proyecto;

    // Relación 1:1 con OrigenRequisito
    // mappedBy dice: "La FK no está aquí, está en la clase OrigenRequisito campo 'requisito'"
    @OneToOne(mappedBy = "requisito", cascade = CascadeType.ALL)
    @ToString.Exclude
    private OrigenRequisito origen;
}
