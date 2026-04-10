package com.analiticasoft.ida.service.impl;

import com.analiticasoft.ida.dto.PermisoRequestDto;
import com.analiticasoft.ida.dto.RolDto;
import com.analiticasoft.ida.entity.*; // Importar todas las entidades
import com.analiticasoft.ida.repository.ModuloRepository;
import com.analiticasoft.ida.repository.RolAccesoModuloRepository;
import com.analiticasoft.ida.repository.RolRepository;
import com.analiticasoft.ida.repository.UsuarioRepository;
import com.analiticasoft.ida.service.RolService;
import jakarta.persistence.EntityNotFoundException; // Importar
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder; // Importar
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RolServiceImpl implements RolService {

    private final RolRepository rolRepository;
    private final ModuloRepository moduloRepository;
    private final RolAccesoModuloRepository rolAccesoModuloRepository;
    private final UsuarioRepository usuarioRepository; // Para obtener la empresa del admin

    @Override
    @Transactional
    public RolDto crearRol(RolDto rolDto) {
        // 1. Obtener la empresa del usuario ADMIN que está creando el rol
        Usuario admin = getUsuarioAutenticado();

        // 2. Crear la nueva entidad Rol
        Rol nuevoRol = new Rol();
        nuevoRol.setNombre(rolDto.getNombre().toUpperCase());
        nuevoRol.setDescripcion(rolDto.getDescripcion());
        nuevoRol.setEmpresa(admin.getEmpresa()); // Asignar el rol a la empresa del admin

        // 3. Guardar y convertir a DTO para devolver
        Rol rolGuardado = rolRepository.save(nuevoRol);
        return convertirARolDto(rolGuardado);
    }

    @Override
    @Transactional
    public void asignarPermisos(PermisoRequestDto permisoRequest) {
        // 1. Obtener el Rol y el Módulo de la BD
        Rol rol = rolRepository.findById(permisoRequest.getRolId())
                .orElseThrow(() -> new EntityNotFoundException("Rol no encontrado"));
        Modulo modulo = moduloRepository.findById(permisoRequest.getModuloId())
                .orElseThrow(() -> new EntityNotFoundException("Módulo no encontrado"));

        // 2. (Seguridad Multi-tenant) Validar que el Admin pueda modificar este rol
        Usuario admin = getUsuarioAutenticado();
        if (!rol.getEmpresa().getId().equals(admin.getEmpresa().getId())) {
            throw new SecurityException("No tiene permisos para modificar este rol");
        }

        // 3. Buscar si ya existe una entrada de permiso
        RolAccesoModuloId permisoId = new RolAccesoModuloId();
        permisoId.setRolId(rol.getId());
        permisoId.setModuloId(modulo.getId());

        RolAccesoModulo permiso = rolAccesoModuloRepository.findById(permisoId)
                .orElse(new RolAccesoModulo()); // Si no existe, crea una nueva

        // 4. Establecer/actualizar los valores
        permiso.setId(permisoId);
        permiso.setRol(rol);
        permiso.setModulo(modulo);
        permiso.setPermisos(permisoRequest.getPermisos()); // Guarda el JSONB

        // 5. Guardar en la BD
        rolAccesoModuloRepository.save(permiso);
    }

    // --- Métodos Helper ---

    private Usuario getUsuarioAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmailWithRol(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario autenticado no encontrado en BD"));
    }

    private RolDto convertirARolDto(Rol rol) {
        RolDto dto = new RolDto();
        dto.setId(rol.getId());
        dto.setNombre(rol.getNombre());
        dto.setDescripcion(rol.getDescripcion());
        return dto;
    }
}