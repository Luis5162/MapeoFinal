package org.uacm.mapeo.gestionrequisitos.entidades;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import java.time.LocalDate;

@Entity
@Table(name = "origen_requisito")
@Data

public class OrigenRequisito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_origen_req")
    private Integer idOrigenReq;

    @Column(name = "nombre_quien_dio")
    private String nombreQuienDio;

    @Column(name = "fecha_provision")
    private LocalDate fechaProvision;

    private String contacto;
    private String cargo;
    private String departamento;
    private String notas;

    // Relación 1:1 (Dueño de la FK)
    @OneToOne
    @JoinColumn(name = "id_requisito", unique = true, nullable = false)
    @ToString.Exclude
    @JsonIgnore
    private Requisito requisito;
}