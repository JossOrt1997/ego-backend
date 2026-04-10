package com.analiticasoft.ida.service.impl;

// Imports de DTOs
import com.analiticasoft.ida.dto.ProductoDto;

// Imports de Entidades
import com.analiticasoft.ida.entity.CategoriaProducto;
import com.analiticasoft.ida.entity.Empresa;
import com.analiticasoft.ida.entity.Producto;
import com.analiticasoft.ida.entity.Usuario;

// Imports de Repositorios
import com.analiticasoft.ida.repository.CategoriaProductoRepository;
import com.analiticasoft.ida.repository.ProductoRepository;
import com.analiticasoft.ida.repository.UsuarioRepository;

// Imports de Servicios
import com.analiticasoft.ida.service.AuditService; // <-- 1. IMPORTAR
import com.analiticasoft.ida.service.ProductoService;

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
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaProductoRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditService auditService; // <-- 2. INYECTAR

    @Override
    @Transactional
    public ProductoDto crearProducto(ProductoDto productoDto) {
        Usuario usuario = getUsuarioAutenticado();
        Empresa empresa = usuario.getEmpresa();

        // 1. Validar SKU duplicado
        if (productoRepository.existsBySkuAndEmpresaId(productoDto.getSku(), empresa.getId())) {
            throw new IllegalStateException("El SKU '" + productoDto.getSku() + "' ya existe en su empresa.");
        }

        // 2. Validar que la Categoría exista y pertenezca a la empresa
        CategoriaProducto categoria = categoriaRepository.findById(productoDto.getCategoriaId())
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada"));
        if (!categoria.getEmpresa().getId().equals(empresa.getId())) {
            throw new SecurityException("La categoría seleccionada no pertenece a su empresa.");
        }

        // 3. Mapear y guardar
        Producto producto = new Producto();
        producto.setEmpresa(empresa);
        producto.setCategoria(categoria);
        producto.setSku(productoDto.getSku());
        producto.setNombre(productoDto.getNombre());
        producto.setDescripcion(productoDto.getDescripcion());
        producto.setPrecioVenta(productoDto.getPrecioVenta());
        producto.setPrecioCompra(productoDto.getPrecioCompra());
        producto.setIsActive(true); // Por defecto es activo

        Producto productoGuardado = productoRepository.save(producto);

        // --- 4. REGISTRAR AUDITORÍA ---
        auditService.logAction(
                usuario,
                "CREATE_PRODUCTO",
                Map.of(
                        "productoId", productoGuardado.getId(),
                        "sku", productoGuardado.getSku(),
                        "nombre", productoGuardado.getNombre()
                )
        );
        // --- FIN AUDITORÍA ---

        return convertirAProductoDto(productoGuardado);
    }

    @Override
    @Transactional
    public ProductoDto actualizarProducto(Long id, ProductoDto productoDto) {
        Usuario usuario = getUsuarioAutenticado();

        // 1. Validar que el producto exista y pertenezca a la empresa
        Producto producto = getProductoSiPertenece(id, usuario.getEmpresa().getId());

        // Guardar estado anterior para auditoría
        String nombreAnterior = producto.getNombre();
        BigDecimal precioVentaAnterior = producto.getPrecioVenta();

        // 2. Validar Categoría
        CategoriaProducto categoria = categoriaRepository.findById(productoDto.getCategoriaId())
                .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada"));
        if (!categoria.getEmpresa().getId().equals(usuario.getEmpresa().getId())) {
            throw new SecurityException("La categoría seleccionada no pertenece a su empresa.");
        }

        // 3. Actualizar campos
        producto.setNombre(productoDto.getNombre());
        producto.setDescripcion(productoDto.getDescripcion());
        producto.setCategoria(categoria);
        producto.setPrecioVenta(productoDto.getPrecioVenta());
        producto.setPrecioCompra(productoDto.getPrecioCompra());
        // El SKU no se actualiza por seguridad de datos

        Producto productoActualizado = productoRepository.save(producto);

        // --- 4. REGISTRAR AUDITORÍA ---
        auditService.logAction(
                usuario,
                "UPDATE_PRODUCTO",
                Map.of(
                        "productoId", id,
                        "sku", producto.getSku(),
                        "cambios", Map.of(
                                "nombre", Map.of("anterior", nombreAnterior, "nuevo", productoDto.getNombre()),
                                "precioVenta", Map.of("anterior", precioVentaAnterior, "nuevo", productoDto.getPrecioVenta())
                        )
                )
        );
        // --- FIN AUDITORÍA ---

        return convertirAProductoDto(productoActualizado);
    }

    @Override
    @Transactional
    public void deleteProducto(Long id) {
        Usuario usuario = getUsuarioAutenticado();

        // 1. Validar que el producto exista y pertenezca a la empresa
        Producto producto = getProductoSiPertenece(id, usuario.getEmpresa().getId());

        // Guardar datos para auditoría ANTES de borrar
        String skuBorrado = producto.getSku();
        String nombreBorrado = producto.getNombre();

        // 2. Borrar
        productoRepository.delete(producto);

        // --- 3. REGISTRAR AUDITORÍA ---
        auditService.logAction(
                usuario,
                "DELETE_PRODUCTO",
                Map.of(
                        "productoBorradoId", id,
                        "sku", skuBorrado,
                        "nombre", nombreBorrado
                )
        );
        // --- FIN AUDITORÍA ---
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoDto getProductoById(Long id) {
        Usuario usuario = getUsuarioAutenticado();
        Producto producto = getProductoSiPertenece(id, usuario.getEmpresa().getId());
        return convertirAProductoDto(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoDto> getProductosDeMiEmpresa() {
        Usuario usuario = getUsuarioAutenticado();
        return productoRepository.findAllByEmpresaId(usuario.getEmpresa().getId())
                .stream()
                .map(this::convertirAProductoDto)
                .collect(Collectors.toList());
    }

    // --- Métodos Helper ---

    private Usuario getUsuarioAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmailWithRol(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario autenticado no encontrado en BD"));
    }

    private Producto getProductoSiPertenece(Long productoId, Long empresaId) {
        return productoRepository.findByIdAndEmpresaId(productoId, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado o no pertenece a su empresa"));
    }

    private ProductoDto convertirAProductoDto(Producto producto) {
        ProductoDto dto = new ProductoDto();
        dto.setId(producto.getId());
        dto.setSku(producto.getSku());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setCategoriaId(producto.getCategoria() != null ? producto.getCategoria().getId() : null);
        dto.setPrecioVenta(producto.getPrecioVenta());
        dto.setPrecioCompra(producto.getPrecioCompra());
        return dto;
    }
}