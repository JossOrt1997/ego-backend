package com.analiticasoft.ida.controller;

import com.analiticasoft.ida.dto.PermisoRequestDto;
import com.analiticasoft.ida.dto.RolDto;
import com.analiticasoft.ida.service.RolService;
import jakarta.validation.Valid; // Importar
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Importar
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/roles") // Ruta base para roles
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')") // <-- TODA LA CLASE REQUIERE ROL ADMIN
public class RolController {

    private final RolService rolService;

    @PostMapping
    public ResponseEntity<RolDto> crearRol(@Valid @RequestBody RolDto rolDto) {
        RolDto rolCreado = rolService.crearRol(rolDto);
        return new ResponseEntity<>(rolCreado, HttpStatus.CREATED);
    }

    @PostMapping("/permisos")
    public ResponseEntity<Void> asignarPermisos(@Valid @RequestBody PermisoRequestDto permisoRequest) {
        rolService.asignarPermisos(permisoRequest);
        return ResponseEntity.ok().build();
    }

    // Aquí podrías añadir un @GetMapping para listar los roles de la empresa
}