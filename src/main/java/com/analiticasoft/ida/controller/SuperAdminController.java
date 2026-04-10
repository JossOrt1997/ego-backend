package com.analiticasoft.ida.controller;

import com.analiticasoft.ida.dto.*; // Importar todos los DTOs
import com.analiticasoft.ida.service.SuperAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/superadmin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')") // Toda la clase protegida
public class SuperAdminController {

    private final SuperAdminService superAdminService;

    // --- ENDPOINT NUEVO PARA DASHBOARD ---
    @GetMapping("/dashboard-stats")
    public ResponseEntity<DashboardStatsDto> getDashboardStats() {
        return ResponseEntity.ok(superAdminService.getDashboardStats());
    }
    // --- Endpoints de Empresas ---
    @GetMapping("/empresas")
    public ResponseEntity<List<EmpresaDto>> getAllEmpresas() {
        return ResponseEntity.ok(superAdminService.getAllEmpresas());
    }

    @PutMapping("/empresas/{id}")
    public ResponseEntity<EmpresaDto> updateEmpresa(@PathVariable Long id, @Valid @RequestBody EmpresaUpdateDto empresaDto) {
        return ResponseEntity.ok(superAdminService.updateEmpresa(id, empresaDto));
    }

    // --- Endpoints de Usuarios ---
    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioDto>> getAllUsuarios() {
        return ResponseEntity.ok(superAdminService.getAllUsuarios());
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioDto> updateUsuario(@PathVariable Long id, @Valid @RequestBody UsuarioUpdateDto usuarioDto) {
        return ResponseEntity.ok(superAdminService.updateUsuario(id, usuarioDto));
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        superAdminService.deleteUsuario(id);
        return ResponseEntity.noContent().build();
    }

    // --- Endpoints de Roles (NUEVO) ---
    @GetMapping("/roles")
    public ResponseEntity<List<SuperAdminRolDto>> getAllRoles() {
        return ResponseEntity.ok(superAdminService.getAllRoles());
    }

    // --- Endpoints de Permisos (NUEVOS) ---
    @GetMapping("/roles/{rolId}/permisos")
    public ResponseEntity<List<PermisoDto>> getPermisosByRol(@PathVariable Long rolId) {
        return ResponseEntity.ok(superAdminService.getPermisosByRol(rolId));
    }

    @PostMapping("/roles/permisos")
    public ResponseEntity<Void> updatePermisos(@Valid @RequestBody PermisoRequestDto permisoRequest) {
        superAdminService.updatePermisos(permisoRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/modulos")
    public ResponseEntity<List<ModuloDto>> getAllModulos() {
        return ResponseEntity.ok(superAdminService.getAllModulos());
    }

    @PostMapping("/modulos")
    public ResponseEntity<ModuloDto> createModulo(@Valid @RequestBody ModuloDto moduloDto) {
        ModuloDto nuevoModulo = superAdminService.createModulo(moduloDto);
        return new ResponseEntity<>(nuevoModulo, HttpStatus.CREATED);
    }

    @PutMapping("/modulos/{id}")
    public ResponseEntity<ModuloDto> updateModulo(@PathVariable Long id, @Valid @RequestBody ModuloDto moduloDto) {
        return ResponseEntity.ok(superAdminService.updateModulo(id, moduloDto));
    }

    @DeleteMapping("/modulos/{id}")
    public ResponseEntity<Void> deleteModulo(@PathVariable Long id) {
        superAdminService.deleteModulo(id);
        return ResponseEntity.noContent().build();
    }


}