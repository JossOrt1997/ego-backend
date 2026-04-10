package com.analiticasoft.ida.controller;

import com.analiticasoft.ida.dto.CategoriaDto;
import com.analiticasoft.ida.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @PostMapping
    @PreAuthorize("hasPermission('ERP', 'crear')")
    public ResponseEntity<CategoriaDto> crearCategoria(@Valid @RequestBody CategoriaDto categoriaDto) {
        CategoriaDto categoriaCreada = categoriaService.crearCategoria(categoriaDto);
        return new ResponseEntity<>(categoriaCreada, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasPermission('ERP', 'leer')")
    public ResponseEntity<List<CategoriaDto>> getCategoriasDeMiEmpresa() {
        return ResponseEntity.ok(categoriaService.getCategoriasDeMiEmpresa());
    }
}