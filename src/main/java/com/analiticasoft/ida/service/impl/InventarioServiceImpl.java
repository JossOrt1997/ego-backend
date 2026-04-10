package com.analiticasoft.ida.service.impl;

// Imports de DTOs
import com.analiticasoft.ida.dto.InventarioAjusteDto;
import com.analiticasoft.ida.dto.InventarioDto;

// Imports de Entidades
import com.analiticasoft.ida.entity.Almacen;
import com.analiticasoft.ida.entity.Inventario;
import com.analiticasoft.ida.entity.Producto;
import com.analiticasoft.ida.entity.Usuario;

// Imports de Repositorios
import com.analiticasoft.ida.repository.AlmacenRepository;
import com.analiticasoft.ida.repository.InventarioRepository;
import com.analiticasoft.ida.repository.ProductoRepository;
import com.analiticasoft.ida.repository.UsuarioRepository;

// Imports de Servicios
import com.analiticasoft.ida.service.AuditService; // <-- 1. IMPORTAR
import com.analiticasoft.ida.service.InventarioService;

// Imports de Java y Spring
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map; // <-- Importar para Auditoría
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventarioServiceImpl implements InventarioService {

    private final InventarioRepository inventarioRepository;
    private final ProductoRepository productoRepository;
    private final AlmacenRepository almacenRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditService auditService; // <-- 2. INYECTAR

    @Override
    @Transactional(readOnly = true)
    public List<InventarioDto> getInventarioDeMiEmpresa() {
        Usuario usuario = getUsuarioAutenticado();
        // Usamos la consulta optimizada que trae los nombres
        return inventarioRepository.findAllByEmpresaIdWithDetails(usuario.getEmpresa().getId())
                .stream()
                .map(this::convertirADto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional // ¡Esta transacción es crítica!
    public InventarioDto ajustarInventario(InventarioAjusteDto ajusteDto) {
        Usuario usuario = getUsuarioAutenticado();
        Long empresaId = usuario.getEmpresa().getId();

        // 1. Validar Producto y Almacén
        Producto producto = productoRepository.findByIdAndEmpresaId(ajusteDto.getProductoId(), empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado o no pertenece a su empresa"));

        Almacen almacen = almacenRepository.findByIdAndEmpresaId(ajusteDto.getAlmacenId(), empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Almacén no encontrado o no pertenece a su empresa"));

        // 2. Obtener o crear registro de inventario
        Inventario inventario = inventarioRepository.findByProductoIdAndAlmacenId(producto.getId(), almacen.getId())
                .orElseGet(() -> {
                    // Si no existe, crea un nuevo registro de inventario
                    Inventario nuevoInventario = new Inventario();
                    nuevoInventario.setProducto(producto);
                    nuevoInventario.setAlmacen(almacen);
                    nuevoInventario.setCantidad(BigDecimal.ZERO);
                    return nuevoInventario;
                });

        BigDecimal cantidadAnterior = inventario.getCantidad(); // Para auditoría

        // 3. Aplicar el ajuste
        BigDecimal nuevaCantidad = inventario.getCantidad().add(ajusteDto.getCantidad());

        // 4. VALIDACIÓN CRÍTICA: No permitir stock negativo
        if (nuevaCantidad.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("El ajuste resultaría en stock negativo para el SKU: " + producto.getSku() +
                    ". Cantidad actual: " + inventario.getCantidad() +
                    ", Ajuste: " + ajusteDto.getCantidad());
        }

        inventario.setCantidad(nuevaCantidad);

        Inventario inventarioGuardado = inventarioRepository.save(inventario);

        // --- 5. REGISTRAR AUDITORÍA ---
        // (No se registra si el tipo es 'VENTA', porque VentaServiceImpl ya lo registra)
        if (!ajusteDto.getTipoMovimiento().equals("VENTA")) {
            // Conversión de BigDecimal a String o Double para el Map<String, Object>
            auditService.logAction(
                    usuario,
                    "AJUSTE_INVENTARIO",
                    Map.of(
                            "productoId", producto.getId(),
                            "sku", producto.getSku(),
                            "almacenId", almacen.getId(),
                            "almacenNombre", almacen.getNombre(),
                            "tipoMovimiento", ajusteDto.getTipoMovimiento(),
                            // Utilizamos String.valueOf() para una conversión segura del BigDecimal
                            "cantidadAjustada", String.valueOf(ajusteDto.getCantidad()),
                            "stockAnterior", String.valueOf(cantidadAnterior),
                            "stockNuevo", String.valueOf(nuevaCantidad)
                    )
            );
        }
        // --- FIN AUDITORÍA ---

        return convertirADto(inventarioGuardado);
    }

    // --- Métodos Helper ---

    private Usuario getUsuarioAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmailWithRol(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario autenticado no encontrado en BD"));
    }

    private InventarioDto convertirADto(Inventario inventario) {
        // Usamos el @Builder que definimos en el DTO
        return InventarioDto.builder()
                .id(inventario.getId())
                .productoId(inventario.getProducto().getId())
                .productoNombre(inventario.getProducto().getNombre())
                .productoSku(inventario.getProducto().getSku())
                .almacenId(inventario.getAlmacen().getId())
                .almacenNombre(inventario.getAlmacen().getNombre())
                .cantidad(inventario.getCantidad())
                .updatedAt(inventario.getUpdatedAt())
                .build();
    }
}