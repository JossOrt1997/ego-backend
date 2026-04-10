package com.analiticasoft.ida.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteDto {

    private Long id; // Para respuestas (GET)

    // Para clientes B2B (Empresas)
    private String nombreComercial;

    // Para clientes B2C (Individuos)
    private String nombre;
    private String apellido;

    @NotBlank(message = "El RFC o identificador fiscal es obligatorio")
    private String rfc;

    @Email(message = "El formato del email no es válido")
    private String emailContacto;

    private String telefonoContacto;

    private String status; // Lo asignará el servicio (ej. "ACTIVO", "PROSPECTO")
}