package com.analiticasoft.ida.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpresaUpdateDto {

    @NotBlank(message = "El tipo de plan no puede estar vacío")
    private String planType;

    @NotBlank(message = "El estado no puede estar vacío")
    private String status;
}