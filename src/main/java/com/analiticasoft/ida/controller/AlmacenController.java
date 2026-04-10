package com.analiticasoft.ida.controller;

import com.analiticasoft.ida.dto.AlmacenDto;
import com.analiticasoft.ida.service.AlmacenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/almacenes")
@RequiredArgsConstructor
public class AlmacenController {

    private final AlmacenService almacenService;

    @PostMapping
    @PreAuthorize("hasPermission('ERP', 'crear')")
    public ResponseEntity<AlmacenDto> crearAlmacen(@Valid @RequestBody AlmacenDto almacenDto) {
        AlmacenDto almacenCreado = almacenService.crearAlmacen(almacenDto);
        return new ResponseEntity<>(almacenCreado, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasPermission('ERP', 'leer')")
    public ResponseEntity<List<AlmacenDto>> getAlmacenesDeMiEmpresa() {
        return ResponseEntity.ok(almacenService.getAlmacenesDeMiEmpresa());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission('ERP', 'leer')")
    public ResponseEntity<AlmacenDto> getAlmacenById(@PathVariable Long id) {
        return ResponseEntity.ok(almacenService.getAlmacenById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasPermission('ERP', 'actualizar')")
    public ResponseEntity<AlmacenDto> actualizarAlmacen(@PathVariable Long id, @Valid @RequestBody AlmacenDto almacenDto) {
        return ResponseEntity.ok(almacenService.actualizarAlmacen(id, almacenDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission('ERP', 'borrar')")
    public ResponseEntity<Void> deleteAlmacen(@PathVariable Long id) {
        almacenService.deleteAlmacen(id);
        return ResponseEntity.noContent().build();
    }
}