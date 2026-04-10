package com.analiticasoft.ida.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;

@Entity
@Table(name = "audit_log")
@Getter
@Setter
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    @Column(name = "timestamp", nullable = false, updatable = false)
    private Instant timestamp;

    @Column(name = "usuario_email", nullable = false)
    private String usuarioEmail;

    @Column(name = "accion", nullable = false, length = 100)
    private String accion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "detalles", columnDefinition = "jsonb")
    private Map<String, Object> detalles;
}
