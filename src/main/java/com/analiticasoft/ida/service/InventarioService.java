package com.analiticasoft.ida.service;

import com.analiticasoft.ida.dto.InventarioAjusteDto;
import com.analiticasoft.ida.dto.InventarioDto;
import java.util.List;

public interface InventarioService {

    /**
     * Obtiene todo el inventario de la empresa del usuario.
     */
    List<InventarioDto> getInventarioDeMiEmpresa();

    /**
     * Realiza un ajuste de inventario (suma o resta stock).
     * Esta es la ÚNICA forma de modificar el inventario.
     */
    InventarioDto ajustarInventario(InventarioAjusteDto ajusteDto);

}