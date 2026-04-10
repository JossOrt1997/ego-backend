package com.analiticasoft.ida.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "persona")
@Getter
@Setter
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "rfc", length = 13)
    private String rfc;

    @Column(name = "curp", length = 18)
    private String curp;

}