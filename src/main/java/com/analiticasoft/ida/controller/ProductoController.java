package com.analiticasoft.ida.controller;

import com.analiticasoft.ida.dto.ProductoDto;
import com.analiticasoft.ida.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    /**
     * Crear un nuevo producto.
     * Requiere permiso 'crear' en el módulo 'ERP'.
     */
    @PostMapping
    @PreAuthorize("hasPermission('ERP', 'crear')")
    public ResponseEntity<ProductoDto> crearProducto(@Valid @RequestBody ProductoDto productoDto) {
        ProductoDto productoCreado = productoService.crearProducto(productoDto);
        return new ResponseEntity<>(productoCreado, HttpStatus.CREATED);
    }

    /**
     * Obtener todos los productos de la empresa del usuario.
     * Requiere permiso 'leer' en el módulo 'ERP'.
     */
    @GetMapping
    @PreAuthorize("hasPermission('ERP', 'leer')")
    public ResponseEntity<List<ProductoDto>> getProductosDeMiEmpresa() {
        return ResponseEntity.ok(productoService.getProductosDeMiEmpresa());
    }

    /**
     * Obtener un producto específico por ID.
     * Requiere permiso 'leer' en el módulo 'ERP'.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasPermission('ERP', 'leer')")
    public ResponseEntity<ProductoDto> getProductoById(@PathVariable Long id) {
        // El servicio ya valida la pertenencia a la empresa
        return ResponseEntity.ok(productoService.getProductoById(id));
    }

    /**
     * Actualizar un producto existente.
     * Requiere permiso 'actualizar' en el módulo 'ERP'.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasPermission('ERP', 'actualizar')")
    public ResponseEntity<ProductoDto> actualizarProducto(@PathVariable Long id, @Valid @RequestBody ProductoDto productoDto) {
        ProductoDto productoActualizado = productoService.actualizarProducto(id, productoDto);
        return ResponseEntity.ok(productoActualizado);
    }

    /**
     * Borrar un producto.
     * Requiere permiso 'borrar' en el módulo 'ERP'.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission('ERP', 'borrar')")
    public ResponseEntity<Void> deleteProducto(@PathVariable Long id) {
        productoService.deleteProducto(id);
        return ResponseEntity.noContent().build();
    }
}