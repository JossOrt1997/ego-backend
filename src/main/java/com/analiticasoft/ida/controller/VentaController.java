package com.analiticasoft.ida.controller;

import com.analiticasoft.ida.dto.VentaRequestDto;
import com.analiticasoft.ida.dto.VentaResponseDto;
import com.analiticasoft.ida.service.VentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    /**
     * Endpoint principal para registrar una nueva venta.
     * Recibe el ID del cliente y una lista de productos/cantidades/almacenes.
     * El servicio se encarga de descontar el inventario.
     */
    @PostMapping
    @PreAuthorize("hasPermission('ERP', 'crear')")
    public ResponseEntity<VentaResponseDto> crearVenta(@Valid @RequestBody VentaRequestDto ventaRequestDto) {
        VentaResponseDto ventaCreada = ventaService.crearVenta(ventaRequestDto);
        return new ResponseEntity<>(ventaCreada, HttpStatus.CREATED);
    }

    /**
     * Obtiene el historial de ventas de la empresa.
     */
    @GetMapping
    @PreAuthorize("hasPermission('ERP', 'leer')")
    public ResponseEntity<List<VentaResponseDto>> getVentasDeMiEmpresa() {
        return ResponseEntity.ok(ventaService.getVentasDeMiEmpresa());
    }

    /**
     * Obtiene el detalle de una venta específica.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasPermission('ERP', 'leer')")
    public ResponseEntity<VentaResponseDto> getVentaById(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.getVentaById(id));
    }

    // (Aquí se podrían añadir endpoints para CANCELAR una venta,
    // lo cual debería REVERTIR el ajuste de inventario).
}