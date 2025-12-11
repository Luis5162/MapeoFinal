package org.uacm.mapeo.gestionrequisitos.entidades;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "diagrama")
@Data
public class Diagrama {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_diagrama")
    private Integer idDiagrama;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_diagrama")
    private TipoDiagrama tipoDiagrama;

    private String nombre;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "creado_por")
    private String creadoPor;

    @Column(name = "archivo_ruta")
    private String archivoRuta;

    @Enumerated(EnumType.STRING)
    private EstadoDiagrama estado = EstadoDiagrama.borrador;


    @ManyToOne
    @JoinColumn(name = "id_proyecto", nullable = false)
    private Proyecto proyecto;
}