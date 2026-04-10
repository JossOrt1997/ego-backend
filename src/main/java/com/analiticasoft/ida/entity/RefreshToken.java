package com.analiticasoft.ida.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "refresh_token")
@Getter
@Setter
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Usaremos un campo TEXT para el token, ya que puede ser largo
    @Column(name = "token", nullable = false, unique = true, columnDefinition = "TEXT")
    private String token;

    // Vínculo OneToOne con el usuario
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;

    // Fecha en la que expira el token de refresco (larga duración)
    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;
}