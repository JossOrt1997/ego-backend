package com.analiticasoft.ida.controller;

import com.analiticasoft.ida.dto.InventarioAjusteDto;
import com.analiticasoft.ida.dto.InventarioDto;
import com.analiticasoft.ida.service.InventarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService inventarioService;

    /**
     * Obtiene una lista de todo el inventario (stock) de la empresa.
     */
    @GetMapping
    @PreAuthorize("hasPermission('ERP', 'leer')")
    public ResponseEntity<List<InventarioDto>> getInventarioDeMiEmpresa() {
        return ResponseEntity.ok(inventarioService.getInventarioDeMiEmpresa());
    }

    /**
     * Ajusta el stock de un producto en un almacén.
     * Recibe una cantidad (positiva para sumar, negativa para restar).
     */
    @PostMapping("/ajustar")
    @PreAuthorize("hasPermission('ERP', 'actualizar')") // Ajustar inventario requiere permiso de 'actualizar'
    public ResponseEntity<InventarioDto> ajustarInventario(@Valid @RequestBody InventarioAjusteDto ajusteDto) {
        InventarioDto inventarioActualizado = inventarioService.ajustarInventario(ajusteDto);
        return ResponseEntity.ok(inventarioActualizado);
    }
}