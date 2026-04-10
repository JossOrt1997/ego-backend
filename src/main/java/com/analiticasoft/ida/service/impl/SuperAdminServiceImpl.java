package com.analiticasoft.ida.service.impl;

// Imports de DTOs
import com.analiticasoft.ida.dto.*;

// Imports de Entidades
import com.analiticasoft.ida.entity.*;

// Imports de Repositorios
import com.analiticasoft.ida.repository.*;

// Imports de Servicio
import com.analiticasoft.ida.service.AuditService; // <-- 1. IMPORTAR AuditService
import com.analiticasoft.ida.service.SuperAdminService;

// Imports de Java y Spring
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder; // Importar
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map; // Importar para los detalles de auditoría
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SuperAdminServiceImpl implements SuperAdminService {

    // --- Repositorios Inyectados ---
    private final EmpresaRepository empresaRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final ModuloRepository moduloRepository;
    private final RolAccesoModuloRepository rolAccesoModuloRepository;
    private final AuditService auditService; // <-- 2. INYECTAR AuditService

    // =======================================================
    // MÉTODOS DE DASHBOARD
    // =======================================================

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsDto getDashboardStats() {
        long totalEmpresas = empresaRepository.count();
        long totalUsuarios = usuarioRepository.count();
        long totalUsuariosActivos = usuarioRepository.countByIsActiveTrue();
        String planMasPopular = empresaRepository.findMostPopularPlanType();

        return DashboardStatsDto.builder()
                .totalEmpresas(totalEmpresas)
                .totalUsuarios(totalUsuarios)
                .totalUsuariosActivos(totalUsuariosActivos)
                .planMasPopular(planMasPopular != null ? planMasPopular : "N/A")
                .build();
    }

    // =======================================================
    // MÉTODOS DE EMPRESA
    // =======================================================

    @Override
    @Transactional(readOnly = true)
    public List<EmpresaDto> getAllEmpresas() {
        return empresaRepository.findAll().stream()
                .map(this::convertirAEmpresaDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EmpresaDto updateEmpresa(Long empresaId, EmpresaUpdateDto empresaDto) {
        Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Empresa no encontrada con id: " + empresaId));

        // Guardar estado anterior para auditoría
        String planAnterior = empresa.getPlanType();
        String statusAnterior = empresa.getStatus();

        empresa.setPlanType(empresaDto.getPlanType());
        empresa.setStatus(empresaDto.getStatus());

        Empresa empresaActualizada = empresaRepository.save(empresa);

        // --- 3. REGISTRAR AUDITORÍA ---
        auditService.logAction(
                getUsuarioEmailAutenticado(), // Email del Super Admin
                empresa.getId(), // ID de la empresa afectada
                "UPDATE_EMPRESA",
                Map.of(
                        "empresaId", empresaId,
                        "cambios", Map.of(
                                "planType", Map.of("anterior", planAnterior, "nuevo", empresaDto.getPlanType()),
                                "status", Map.of("anterior", statusAnterior, "nuevo", empresaDto.getStatus())
                        )
                )
        );
        // --- FIN AUDITORÍA ---

        return convertirAEmpresaDto(empresaActualizada);
    }

    // =======================================================
    // MÉTODOS DE USUARIO
    // =======================================================

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioDto> getAllUsuarios() {
        return usuarioRepository.findAllWithRol()
                .stream()
                .map(this::convertirAUsuarioDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UsuarioDto updateUsuario(Long usuarioId, UsuarioUpdateDto usuarioDto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + usuarioId));

        Rol nuevoRol = rolRepository.findById(usuarioDto.getRolId())
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado con id: " + usuarioDto.getRolId()));

        Empresa nuevaEmpresa = empresaRepository.findById(usuarioDto.getEmpresaId())
                .orElseThrow(() -> new EntityNotFoundException("Empresa no encontrada con id: " + usuarioDto.getEmpresaId()));

        // Guardar estado anterior para auditoría
        Long rolAnterior = usuario.getRol().getId();
        Long empresaAnterior = usuario.getEmpresa().getId();
        Boolean activoAnterior = usuario.getIsActive();

        usuario.setRol(nuevoRol);
        usuario.setEmpresa(nuevaEmpresa);
        usuario.setIsActive(usuarioDto.getIsActive());

        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        // --- 3. REGISTRAR AUDITORÍA ---
        auditService.logAction(
                getUsuarioEmailAutenticado(),
                null, // El usuario afectado puede ser de otra empresa, el log es global
                "UPDATE_USUARIO",
                Map.of(
                        "usuarioAfectadoId", usuarioId,
                        "emailAfectado", usuario.getEmail(),
                        "cambios", Map.of(
                                "rolId", Map.of("anterior", rolAnterior, "nuevo", usuarioDto.getRolId()),
                                "empresaId", Map.of("anterior", empresaAnterior, "nuevo", usuarioDto.getEmpresaId()),
                                "isActive", Map.of("anterior", activoAnterior, "nuevo", usuarioDto.getIsActive())
                        )
                )
        );
        // --- FIN AUDITORÍA ---

        return convertirAUsuarioDto(usuarioActualizado);
    }

    @Override
    @Transactional
    public void deleteUsuario(Long usuarioId) {
        if (usuarioId == 1L) {
            throw new SecurityException("No se puede borrar al usuario Super Admin principal.");
        }

        // Obtener email para auditoría ANTES de borrar
        String emailBorrado = usuarioRepository.findById(usuarioId)
                .map(Usuario::getEmail)
                .orElse("ID_NO_ENCONTRADO");

        usuarioRepository.deleteById(usuarioId);

        // --- 3. REGISTRAR AUDITORÍA ---
        auditService.logAction(
                getUsuarioEmailAutenticado(),
                null, // Log global
                "DELETE_USUARIO",
                Map.of(
                        "usuarioBorradoId", usuarioId,
                        "emailBorrado", emailBorrado
                )
        );
        // --- FIN AUDITORÍA ---
    }

    // =======================================================
    // MÉTODOS DE ROL
    // =======================================================

    @Override
    @Transactional(readOnly = true)
    public List<SuperAdminRolDto> getAllRoles() {
        return rolRepository.findAll().stream()
                .map(this::convertirASuperAdminRolDto)
                .collect(Collectors.toList());
    }

    // =======================================================
    // MÉTODOS DE PERMISOS
    // =======================================================

    @Override
    @Transactional(readOnly = true)
    public List<PermisoDto> getPermisosByRol(Long rolId) {
        if (!rolRepository.existsById(rolId)) {
            throw new EntityNotFoundException("Rol no encontrado con id: " + rolId);
        }
        return rolAccesoModuloRepository.findByRolId(rolId).stream()
                .map(this::convertirAPermisoDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updatePermisos(PermisoRequestDto permisoRequest) {
        Rol rol = rolRepository.findById(permisoRequest.getRolId())
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado con id: " + permisoRequest.getRolId()));
        Modulo modulo = moduloRepository.findById(permisoRequest.getModuloId())
                .orElseThrow(() -> new EntityNotFoundException("Módulo no encontrado con id: " + permisoRequest.getModuloId()));

        RolAccesoModuloId permisoId = new RolAccesoModuloId();
        permisoId.setRolId(rol.getId());
        permisoId.setModuloId(modulo.getId());

        RolAccesoModulo permiso = rolAccesoModuloRepository.findById(permisoId)
                .orElse(new RolAccesoModulo());

        permiso.setId(permisoId);
        permiso.setRol(rol);
        permiso.setModulo(modulo);
        permiso.setPermisos(permisoRequest.getPermisos());

        rolAccesoModuloRepository.save(permiso);

        // --- 3. REGISTRAR AUDITORÍA ---
        auditService.logAction(
                getUsuarioEmailAutenticado(),
                rol.getEmpresa() != null ? rol.getEmpresa().getId() : null, // Empresa del Rol
                "UPDATE_PERMISOS_ROL",
                Map.of(
                        "rolIdAfectado", rol.getId(),
                        "rolNombre", rol.getNombre(),
                        "moduloAfectado", modulo.getNombre(),
                        "nuevosPermisos", permisoRequest.getPermisos()
                )
        );
        // --- FIN AUDITORÍA ---
    }

    // =======================================================
    // MÉTODOS DE MÓDULO (CRUD)
    // =======================================================

    @Override
    @Transactional(readOnly = true)
    public List<ModuloDto> getAllModulos() {
        return moduloRepository.findAll().stream()
                .map(this::convertirAModuloDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ModuloDto createModulo(ModuloDto moduloDto) {
        Modulo nuevoModulo = new Modulo();
        nuevoModulo.setNombre(moduloDto.getNombre().toUpperCase());
        nuevoModulo.setDescripcion(moduloDto.getDescripcion());

        Modulo moduloGuardado = moduloRepository.save(nuevoModulo);

        // --- 3. REGISTRAR AUDITORÍA ---
        auditService.logAction(
                getUsuarioEmailAutenticado(),
                null, // Global
                "CREATE_MODULO",
                Map.of("moduloId", moduloGuardado.getId(), "nombre", moduloGuardado.getNombre())
        );
        // --- FIN AUDITORÍA ---

        return convertirAModuloDto(moduloGuardado);
    }

    @Override
    @Transactional
    public ModuloDto updateModulo(Long moduloId, ModuloDto moduloDto) {
        Modulo modulo = moduloRepository.findById(moduloId)
                .orElseThrow(() -> new EntityNotFoundException("Módulo no encontrado con id: " + moduloId));

        // Guardar estado anterior para auditoría
        String nombreAnterior = modulo.getNombre();

        modulo.setNombre(moduloDto.getNombre().toUpperCase());
        modulo.setDescripcion(moduloDto.getDescripcion());

        Modulo moduloActualizado = moduloRepository.save(modulo);

        // --- 3. REGISTRAR AUDITORÍA ---
        auditService.logAction(
                getUsuarioEmailAutenticado(),
                null, // Global
                "UPDATE_MODULO",
                Map.of(
                        "moduloId", moduloActualizado.getId(),
                        "cambios", Map.of(
                                "nombre", Map.of("anterior", nombreAnterior, "nuevo", moduloActualizado.getNombre())
                        )
                )
        );
        // --- FIN AUDITORÍA ---

        return convertirAModuloDto(moduloActualizado);
    }

    @Override
    @Transactional
    public void deleteModulo(Long moduloId) {
        // Validación: Asegurarnos de que el módulo no esté en uso
        if (rolAccesoModuloRepository.existsByModuloId(moduloId)) {
            throw new IllegalStateException("No se puede borrar el módulo porque ya tiene permisos asignados.");
        }

        // Obtener nombre para auditoría
        String nombreModulo = moduloRepository.findById(moduloId)
                .map(Modulo::getNombre)
                .orElse("ID_NO_ENCONTRADO");

        moduloRepository.deleteById(moduloId);

        // --- 3. REGISTRAR AUDITORÍA ---
        auditService.logAction(
                getUsuarioEmailAutenticado(),
                null, // Global
                "DELETE_MODULO",
                Map.of("moduloBorradoId", moduloId, "nombre", nombreModulo)
        );
        // --- FIN AUDITORÍA ---
    }

    // =======================================================
    // MÉTODOS HELPER DE CONVERSIÓN (DTOs)
    // =======================================================

    private String getUsuarioEmailAutenticado() {
        // Método helper para obtener el email del usuario que hace la petición
        // Si no hay contexto (ej. un job), devuelve "SISTEMA"
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "SISTEMA";
        }
        return authentication.getName();
    }

    private EmpresaDto convertirAEmpresaDto(Empresa empresa) {
        EmpresaDto dto = new EmpresaDto();
        dto.setId(empresa.getId());
        dto.setName(empresa.getName());
        dto.setRfc(empresa.getRfc());
        dto.setPlanType(empresa.getPlanType());
        dto.setStatus(empresa.getStatus());
        dto.setCreatedAt(empresa.getCreatedAt());
        return dto;
    }

    private UsuarioDto convertirAUsuarioDto(Usuario usuario) {
        UsuarioDto dto = new UsuarioDto();
        dto.setId(usuario.getId());
        dto.setEmail(usuario.getEmail());
        if (usuario.getEmpresa() != null) {
            dto.setEmpresaId(usuario.getEmpresa().getId());
        }
        dto.setRolNombre(usuario.getRol() != null ? usuario.getRol().getNombre() : "SIN ROL");
        dto.setIsActive(usuario.getIsActive());
        dto.setLastLogin(usuario.getLastLogin());
        return dto;
    }

    private SuperAdminRolDto convertirASuperAdminRolDto(Rol rol) {
        SuperAdminRolDto dto = new SuperAdminRolDto();
        dto.setId(rol.getId());
        dto.setNombre(rol.getNombre());
        dto.setDescripcion(rol.getDescripcion());
        dto.setEmpresaId(rol.getEmpresa() != null ? rol.getEmpresa().getId() : null);
        return dto;
    }

    private PermisoDto convertirAPermisoDto(RolAccesoModulo permiso) {
        PermisoDto dto = new PermisoDto();
        dto.setRolId(permiso.getRol().getId());
        dto.setModuloId(permiso.getModulo().getId());
        dto.setModuloNombre(permiso.getModulo().getNombre());
        dto.setPermisos(permiso.getPermisos());
        return dto;
    }

    private ModuloDto convertirAModuloDto(Modulo modulo) {
        ModuloDto dto = new ModuloDto();
        dto.setId(modulo.getId());
        dto.setNombre(modulo.getNombre());
        dto.setDescripcion(modulo.getDescripcion());
        return dto;
    }
}