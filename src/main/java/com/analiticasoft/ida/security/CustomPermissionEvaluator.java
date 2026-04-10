package com.analiticasoft.ida.security;

import com.analiticasoft.ida.entity.RolAccesoModulo;
import com.analiticasoft.ida.entity.Usuario;
import com.analiticasoft.ida.repository.RolAccesoModuloRepository;
import com.analiticasoft.ida.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final UsuarioRepository usuarioRepository;
    private final RolAccesoModuloRepository rolAccesoModuloRepository;

    /**
     * Este método es el que se llama cuando usamos @PreAuthorize("hasPermission(...)")
     *
     * @param authentication      El objeto del usuario autenticado (nos da el email)
     * @param targetDomainObject  El primer argumento de hasPermission() (ej. "CRM")
     * @param permission          El segundo argumento de hasPermission() (ej. "CREAR")
     * @return true si tiene permiso, false si no
     */
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if ((authentication == null) || !(targetDomainObject instanceof String) || !(permission instanceof String)) {
            return false; // No podemos evaluar si los argumentos son incorrectos
        }

        // --- INICIO DE LA MODIFICACIÓN v1.5 ---
        // Si el usuario es SUPER_ADMIN, conceder permiso automáticamente sin verificar la BD.
        boolean isSuperAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));

        if (isSuperAdmin) {
            return true;
        }
        // --- FIN DE LA MODIFICACIÓN v1.5 ---

        String email = authentication.getName();
        String moduloNombre = ((String) targetDomainObject).toUpperCase();
        String permisoRequerido = ((String) permission).toLowerCase(); // ej. "crear"

        // 1. Obtener el usuario y su rol
        // Usamos findByEmailWithRol que ya trae el rol (¡eficiente!)
        Usuario usuario = usuarioRepository.findByEmailWithRol(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        Long rolId = usuario.getRol().getId();

        // 2. Buscar el permiso específico en la base de datos
        // Usamos un método que necesitamos añadir al repositorio
        Optional<RolAccesoModulo> permisoOpt = rolAccesoModuloRepository.findByRolIdAndModuloNombre(rolId, moduloNombre);

        if (permisoOpt.isEmpty()) {
            return false; // No tiene ninguna entrada de permiso para este módulo
        }

        // 3. Revisar el mapa de permisos (JSONB)
        Map<String, Boolean> permisos = permisoOpt.get().getPermisos();

        // 4. Devolver true si el permiso existe y es 'true'
        return permisos.getOrDefault(permisoRequerido, false);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        // Este método no lo usaremos por ahora, pero debe estar implementado
        return false;
    }
}