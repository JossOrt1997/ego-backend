package com.analiticasoft.ida.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    @GetMapping("/hello")
    public String sayHello() {
        // Obtenemos el email del usuario que ya fue autenticado por el filtro JWT
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        return "¡Hola " + email + "! Has accedido a un recurso protegido.";
    }
}