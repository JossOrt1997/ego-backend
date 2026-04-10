package com.analiticasoft.ida.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor // Constructor vacío
@AllArgsConstructor // Constructor con todos los argumentos
public class LoginResponse {
    private String jwt; // Aquí devolveremos el token
}