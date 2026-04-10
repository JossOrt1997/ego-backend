package com.analiticasoft.ida.service;

import com.analiticasoft.ida.dto.ProductoDto;
import java.util.List;

public interface ProductoService {

    ProductoDto crearProducto(ProductoDto productoDto);

    ProductoDto actualizarProducto(Long id, ProductoDto productoDto);

    void deleteProducto(Long id);

    ProductoDto getProductoById(Long id);

    List<ProductoDto> getProductosDeMiEmpresa();
}