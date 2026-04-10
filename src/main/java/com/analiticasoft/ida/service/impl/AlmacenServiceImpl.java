package com.analiticasoft.ida.service.impl;

import com.analiticasoft.ida.dto.AlmacenDto;
import com.analiticasoft.ida.entity.Almacen;
import com.analiticasoft.ida.entity.Usuario;
import com.analiticasoft.ida.repository.AlmacenRepository;
import com.analiticasoft.ida.repository.UsuarioRepository;
import com.analiticasoft.ida.service.AuditService; // <-- 1. IMPORTAR
import com.analiticasoft.ida.service.AlmacenService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map; // <-- Importar para Auditoría
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlmacenServiceImpl implements AlmacenService {

    private final AlmacenRepository almacenRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditService auditService; // <-- 2. INYECTAR

    @Override
    @Transactional
    public AlmacenDto crearAlmacen(AlmacenDto almacenDto) {
        Usuario usuario = getUsuarioAutenticado();

        Almacen almacen = new Almacen();
        almacen.setNombre(almacenDto.getNombre());
        almacen.setDireccion(almacenDto.getDireccion());
        almacen.setEmpresa(usuario.getEmpresa());

        Almacen almacenGuardado = almacenRepository.save(almacen);

        // --- REGISTRAR AUDITORÍA: CREACIÓN ---
        auditService.logAction(
                usuario,
                "CREATE_ALMACEN",
                Map.of("almacenId", almacenGuardado.getId(), "nombre", almacenGuardado.getNombre())
        );
        // --- FIN AUDITORÍA ---

        return convertirADto(almacenGuardado);
    }

    @Override
    @Transactional
    public AlmacenDto actualizarAlmacen(Long id, AlmacenDto almacenDto) {
        Usuario usuario = getUsuarioAutenticado();
        Almacen almacen = getAlmacenSiPertenece(id, usuario.getEmpresa().getId());

        // Guardar estado anterior para auditoría
        String nombreAnterior = almacen.getNombre();
        String direccionAnterior = almacen.getDireccion();

        almacen.setNombre(almacenDto.getNombre());
        almacen.setDireccion(almacenDto.getDireccion());

        Almacen almacenActualizado = almacenRepository.save(almacen);

        // --- REGISTRAR AUDITORÍA: ACTUALIZACIÓN ---
        auditService.logAction(
                usuario,
                "UPDATE_ALMACEN",
                Map.of(
                        "almacenId", id,
                        "cambios", Map.of(
                                "nombre", Map.of("anterior", nombreAnterior, "nuevo", almacenDto.getNombre()),
                                "direccion", Map.of("anterior", direccionAnterior, "nuevo", almacenDto.getDireccion())
                        )
                )
        );
        // --- FIN AUDITORÍA ---

        return convertirADto(almacenActualizado);
    }

    @Override
    @Transactional
    public void deleteAlmacen(Long id) {
        Usuario usuario = getUsuarioAutenticado();
        Almacen almacen = getAlmacenSiPertenece(id, usuario.getEmpresa().getId());

        // Guardar datos para auditoría
        String nombreBorrado = almacen.getNombre();

        almacenRepository.delete(almacen);

        // --- REGISTRAR AUDITORÍA: ELIMINACIÓN ---
        auditService.logAction(
                usuario,
                "DELETE_ALMACEN",
                Map.of("almacenBorradoId", id, "nombre", nombreBorrado)
        );
        // --- FIN AUDITORÍA ---
    }

    // --- Métodos de Listado y Helper ---

    @Override
    @Transactional(readOnly = true)
    public List<AlmacenDto> getAlmacenesDeMiEmpresa() {
        Usuario usuario = getUsuarioAutenticado();
        return almacenRepository.findAllByEmpresaId(usuario.getEmpresa().getId())
                .stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AlmacenDto getAlmacenById(Long id) {
        Usuario usuario = getUsuarioAutenticado();
        Almacen almacen = getAlmacenSiPertenece(id, usuario.getEmpresa().getId());
        return convertirADto(almacen);
    }

    private Usuario getUsuarioAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmailWithRol(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario autenticado no encontrado en BD"));
    }

    private Almacen getAlmacenSiPertenece(Long almacenId, Long empresaId) {
        return almacenRepository.findByIdAndEmpresaId(almacenId, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Almacén no encontrado o no pertenece a su empresa"));
    }

    private AlmacenDto convertirADto(Almacen almacen) {
        AlmacenDto dto = new AlmacenDto();
        dto.setId(almacen.getId());
        dto.setNombre(almacen.getNombre());
        dto.setDireccion(almacen.getDireccion());
        return dto;
    }
}