package com.analiticasoft.ida.service.impl;

// Imports de DTOs
import com.analiticasoft.ida.dto.InventarioAjusteDto;
import com.analiticasoft.ida.dto.VentaRequestDto;
import com.analiticasoft.ida.dto.VentaRequestItemDto;
import com.analiticasoft.ida.dto.VentaResponseDto;

// Imports de Entidades
import com.analiticasoft.ida.entity.Cliente;
import com.analiticasoft.ida.entity.Empresa;
import com.analiticasoft.ida.entity.Producto;
import com.analiticasoft.ida.entity.Usuario;
import com.analiticasoft.ida.entity.Venta;
import com.analiticasoft.ida.entity.VentaDetalle;

// Imports de Repositorios
import com.analiticasoft.ida.repository.ClienteRepository;
import com.analiticasoft.ida.repository.ProductoRepository;
import com.analiticasoft.ida.repository.UsuarioRepository;
import com.analiticasoft.ida.repository.VentaRepository;

// Imports de Servicios
import com.analiticasoft.ida.service.AuditService; // <-- 1. IMPORTAR AuditService
import com.analiticasoft.ida.service.InventarioService;
import com.analiticasoft.ida.service.VentaService;

// Imports de Java y Spring
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map; // <-- Importar para Auditoría
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final InventarioService inventarioService;
    private final AuditService auditService; // <-- 2. INYECTAR AuditService

    // Definir la tasa de impuestos (ej. 16% IVA)
    private static final BigDecimal TASA_IMPUESTOS = new BigDecimal("0.16");

    @Override
    @Transactional // ¡CRÍTICO! Si algo falla (ej. stock), se revierte toda la transacción
    public VentaResponseDto crearVenta(VentaRequestDto ventaRequestDto) {
        Usuario usuario = getUsuarioAutenticado();
        Empresa empresa = usuario.getEmpresa();
        Long empresaId = empresa.getId();

        // 1. Validar Cliente
        Cliente cliente = clienteRepository.findByIdAndEmpresaId(ventaRequestDto.getClienteId(), empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado o no pertenece a su empresa"));

        // 2. Crear la Venta (cabecera) inicial
        Venta venta = new Venta();
        venta.setEmpresa(empresa);
        venta.setCliente(cliente);
        venta.setUsuario(usuario);
        venta.setStatus("PENDIENTE");
        venta.setSubtotal(BigDecimal.ZERO);
        venta.setImpuestos(BigDecimal.ZERO);
        venta.setTotal(BigDecimal.ZERO);

        Venta ventaGuardada = ventaRepository.save(venta);

        BigDecimal subtotalAcumulado = BigDecimal.ZERO;
        List<VentaDetalle> detalles = new ArrayList<>();
        List<Map<String, Object>> detallesParaAuditoria = new ArrayList<>(); // Para el log

        // 3. Procesar cada línea (item) de la venta
        for (VentaRequestItemDto itemDto : ventaRequestDto.getItems()) {

            Producto producto = productoRepository.findByIdAndEmpresaId(itemDto.getProductoId(), empresaId)
                    .orElseThrow(() -> new EntityNotFoundException("Producto ID " + itemDto.getProductoId() + " no encontrado"));

            // 3b. ¡Descontar de Inventario!
            InventarioAjusteDto ajuste = new InventarioAjusteDto();
            ajuste.setProductoId(producto.getId());
            ajuste.setAlmacenId(itemDto.getAlmacenId());
            ajuste.setCantidad(itemDto.getCantidad().negate()); // Convertir a negativo
            ajuste.setTipoMovimiento("VENTA");

            inventarioService.ajustarInventario(ajuste);

            // 3c. Crear el detalle de la venta
            VentaDetalle detalle = new VentaDetalle();
            detalle.setVenta(ventaGuardada);
            detalle.setProducto(producto);
            detalle.setCantidad(itemDto.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecioVenta());

            BigDecimal subtotalLinea = producto.getPrecioVenta().multiply(itemDto.getCantidad());
            detalle.setSubtotalLinea(subtotalLinea);

            detalles.add(detalle);
            subtotalAcumulado = subtotalAcumulado.add(subtotalLinea);

            // 3d. Preparar datos para auditoría
            detallesParaAuditoria.add(Map.of(
                    "productoId", producto.getId(),
                    "sku", producto.getSku(),
                    "cantidad", itemDto.getCantidad(),
                    "subtotalLinea", subtotalLinea
            ));
        }

        // 4. Calcular totales finales (usando 2 decimales)
        BigDecimal impuestos = subtotalAcumulado.multiply(TASA_IMPUESTOS).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotalAcumulado.add(impuestos);

        // 5. Actualizar la Venta con los totales y detalles
        ventaGuardada.setDetalles(detalles);
        ventaGuardada.setSubtotal(subtotalAcumulado.setScale(2, RoundingMode.HALF_UP));
        ventaGuardada.setImpuestos(impuestos);
        ventaGuardada.setTotal(total);
        ventaGuardada.setStatus("COMPLETADA");

        Venta ventaCompleta = ventaRepository.save(ventaGuardada);

        // --- 6. REGISTRAR AUDITORÍA ---
        auditService.logAction(
                usuario, // El objeto Usuario completo
                "CREATE_VENTA",
                Map.of(
                        "ventaId", ventaCompleta.getId(),
                        "clienteId", cliente.getId(),
                        "totalVenta", ventaCompleta.getTotal(),
                        "detalles", detallesParaAuditoria
                )
        );
        // --- FIN AUDITORÍA ---

        // 7. Devolver la respuesta
        return convertirAVentaResponseDto(ventaCompleta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaResponseDto> getVentasDeMiEmpresa() {
        Usuario usuario = getUsuarioAutenticado();
        return ventaRepository.findAllByEmpresaId(usuario.getEmpresa().getId())
                .stream()
                .map(this::convertirAVentaResponseDtoSimple)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public VentaResponseDto getVentaById(Long id) {
        Usuario usuario = getUsuarioAutenticado();
        Venta venta = ventaRepository.findByIdWithDetails(id, usuario.getEmpresa().getId())
                .orElseThrow(() -> new EntityNotFoundException("Venta no encontrada o no pertenece a su empresa"));
        return convertirAVentaResponseDto(venta);
    }

    // --- Métodos Helper ---

    private Usuario getUsuarioAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmailWithRol(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario autenticado no encontrado en BD"));
    }

    // DTO Completo (para GET /id y respuesta de POST)
    private VentaResponseDto convertirAVentaResponseDto(Venta venta) {
        List<VentaResponseDto.VentaDetalleResponseDto> detallesDto = venta.getDetalles()
                .stream()
                .map(detalle -> VentaResponseDto.VentaDetalleResponseDto.builder()
                        .productoId(detalle.getProducto().getId())
                        .productoSku(detalle.getProducto().getSku())
                        .productoNombre(detalle.getProducto().getNombre())
                        .cantidad(detalle.getCantidad())
                        .precioUnitario(detalle.getPrecioUnitario())
                        .subtotalLinea(detalle.getSubtotalLinea())
                        .build())
                .collect(Collectors.toList());

        return VentaResponseDto.builder()
                .id(venta.getId())
                .clienteId(venta.getCliente().getId())
                .clienteNombre(venta.getCliente().getNombreComercial() != null ?
                        venta.getCliente().getNombreComercial() :
                        (venta.getCliente().getPersona() != null ? venta.getCliente().getPersona().getNombre() : "N/A"))
                .usuarioEmail(venta.getUsuario().getEmail())
                .subtotal(venta.getSubtotal())
                .impuestos(venta.getImpuestos())
                .total(venta.getTotal())
                .status(venta.getStatus())
                .fechaVenta(venta.getFechaVenta())
                .detalles(detallesDto)
                .build();
    }

    // DTO Simple (para la lista GET /ventas, sin detalles)
    private VentaResponseDto convertirAVentaResponseDtoSimple(Venta venta) {
        return VentaResponseDto.builder()
                .id(venta.getId())
                .clienteId(venta.getCliente().getId())
                .clienteNombre(venta.getCliente().getNombreComercial() != null ?
                        venta.getCliente().getNombreComercial() :
                        (venta.getCliente().getPersona() != null ? venta.getCliente().getPersona().getNombre() : "N/A"))
                .usuarioEmail(venta.getUsuario().getEmail())
                .subtotal(venta.getSubtotal())
                .impuestos(venta.getImpuestos())
                .total(venta.getTotal())
                .status(venta.getStatus())
                .fechaVenta(venta.getFechaVenta())
                .detalles(new ArrayList<>()) // Devolver lista vacía
                .build();
    }
}