package com.analiticasoft.ida.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Getter
@Setter
public class EmpresaDto {
    private Long id;
    private String name;
    private String rfc;
    private String planType;
    private String status;
    private Instant createdAt;
}