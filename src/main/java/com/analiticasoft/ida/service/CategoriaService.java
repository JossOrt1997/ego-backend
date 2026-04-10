package com.analiticasoft.ida.service;

import com.analiticasoft.ida.dto.CategoriaDto;
import java.util.List;

public interface CategoriaService {
    CategoriaDto crearCategoria(CategoriaDto categoriaDto);
    List<CategoriaDto> getCategoriasDeMiEmpresa();
    // (Podríamos añadir Update y Delete después si se necesita)
}