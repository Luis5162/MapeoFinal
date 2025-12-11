package org.uacm.mapeo.gestionrequisitos.entidades;

import jakarta.persistence .*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
    @Table(name = "empresa")
    @Data  // <--- ¡ESTA ES LA MAGIA! Genera Getters, Setters, ToString, etc. automáticos.
    @NoArgsConstructor // Genera constructor vacío (necesario para JPA)
    @AllArgsConstructor // Genera constructor con todo
    public class Empresa {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id_empresa")
        private Integer idEmpresa;

        @NotBlank(message = "El nombre de la empresa es obligatorio")
        private String nombre;

        private Boolean activo = true;
    }

