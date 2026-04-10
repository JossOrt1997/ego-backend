package com.analiticasoft.ida.service;

import com.analiticasoft.ida.dto.*; // Importar todos los DTOs
import java.util.List;

public interface SuperAdminService {

    // --- Endpoints de Dashboard (NUEVO) ---
    DashboardStatsDto getDashboardStats();

    // --- Empresas ---
    List<EmpresaDto> getAllEmpresas();
    EmpresaDto updateEmpresa(Long empresaId, EmpresaUpdateDto empresaDto);

    // --- Usuarios ---
    List<UsuarioDto> getAllUsuarios();
    UsuarioDto updateUsuario(Long usuarioId, UsuarioUpdateDto usuarioDto);
    void deleteUsuario(Long usuarioId);

    // --- Roles (NUEVO) ---
    List<SuperAdminRolDto> getAllRoles();

    // --- Permisos (NUEVOS) ---
    List<PermisoDto> getPermisosByRol(Long rolId);
    void updatePermisos(PermisoRequestDto permisoRequest);

    List<ModuloDto> getAllModulos(); // Ya teníamos un DTO para esto
    ModuloDto createModulo(ModuloDto moduloDto);
    ModuloDto updateModulo(Long moduloId, ModuloDto moduloDto);
    void deleteModulo(Long moduloId);
}