package com.analiticasoft.ida.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistroRequest {

    @NotBlank(message = "El nombre de la empresa no puede estar vacío.")
    private String nombreEmpresa;

    @NotBlank(message = "El email no puede estar vacío.")
    @Email(message = "El formato del email no es válido.")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía.")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres.")
    private String password;

}