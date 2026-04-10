package com.analiticasoft.ida.service;

import com.analiticasoft.ida.dto.VentaRequestDto;
import com.analiticasoft.ida.dto.VentaResponseDto;
import java.util.List;

public interface VentaService {

    /**
     * Procesa y registra una nueva venta.
     * Esta operación es transaccional. Si falla el stock, se revierte todo.
     */
    VentaResponseDto crearVenta(VentaRequestDto ventaRequestDto);

    /**
     * Obtiene una lista de todas las ventas de la empresa del usuario.
     */
    List<VentaResponseDto> getVentasDeMiEmpresa();

    /**
     * Obtiene una venta específica por ID, con todos sus detalles.
     */
    VentaResponseDto getVentaById(Long id);
}