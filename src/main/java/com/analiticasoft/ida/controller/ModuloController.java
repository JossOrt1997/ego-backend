package com.analiticasoft.ida.controller;

import com.analiticasoft.ida.dto.ModuloDto;
import com.analiticasoft.ida.service.ModuloService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Importante
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/modulos") // Ruta base para módulos
@RequiredArgsConstructor
public class ModuloController {

    private final ModuloService moduloService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // <-- NUESTRA PRIMERA REGLA DE AUTORIZACIÓN
    public ResponseEntity<List<ModuloDto>> listarModulos() {
        List<ModuloDto> modulos = moduloService.getAllModulos();
        return ResponseEntity.ok(modulos);
    }
}