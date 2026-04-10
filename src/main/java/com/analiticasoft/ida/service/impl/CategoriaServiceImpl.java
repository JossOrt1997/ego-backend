package com.analiticasoft.ida.service.impl;

import com.analiticasoft.ida.dto.CategoriaDto;
import com.analiticasoft.ida.entity.CategoriaProducto;
import com.analiticasoft.ida.entity.Empresa;
import com.analiticasoft.ida.entity.Usuario;
import com.analiticasoft.ida.repository.CategoriaProductoRepository;
import com.analiticasoft.ida.repository.UsuarioRepository;
import com.analiticasoft.ida.service.AuditService; // <-- 1. IMPORTAR
import com.analiticasoft.ida.service.CategoriaService;
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
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaProductoRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditService auditService; // <-- 2. INYECTAR

    @Override
    @Transactional
    public CategoriaDto crearCategoria(CategoriaDto categoriaDto) {
        Usuario usuario = getUsuarioAutenticado();

        CategoriaProducto nuevaCategoria = new CategoriaProducto();
        nuevaCategoria.setNombre(categoriaDto.getNombre());
        nuevaCategoria.setDescripcion(categoriaDto.getDescripcion());
        nuevaCategoria.setEmpresa(usuario.getEmpresa()); // Asignar a su empresa

        CategoriaProducto categoriaGuardada = categoriaRepository.save(nuevaCategoria);

        // --- REGISTRAR AUDITORÍA: CREACIÓN ---
        auditService.logAction(
                usuario,
                "CREATE_CATEGORIA",
                Map.of(
                        "categoriaId", categoriaGuardada.getId(),
                        "nombre", categoriaGuardada.getNombre()
                )
        );
        // --- FIN AUDITORÍA ---

        return convertirADto(categoriaGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoriaDto> getCategoriasDeMiEmpresa() {
        Usuario usuario = getUsuarioAutenticado();
        return categoriaRepository.findAllByEmpresaId(usuario.getEmpresa().getId())
                .stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    // --- Métodos Helper ---

    private Usuario getUsuarioAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmailWithRol(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario autenticado no encontrado en BD"));
    }

    private CategoriaDto convertirADto(CategoriaProducto categoria) {
        CategoriaDto dto = new CategoriaDto();
        dto.setId(categoria.getId());
        dto.setNombre(categoria.getNombre());
        dto.setDescripcion(categoria.getDescripcion());
        return dto;
    }
}