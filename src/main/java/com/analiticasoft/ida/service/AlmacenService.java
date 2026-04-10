package com.analiticasoft.ida.service;

import com.analiticasoft.ida.dto.AlmacenDto;
import java.util.List;

public interface AlmacenService {

    AlmacenDto crearAlmacen(AlmacenDto almacenDto);

    AlmacenDto actualizarAlmacen(Long id, AlmacenDto almacenDto);

    void deleteAlmacen(Long id);

    AlmacenDto getAlmacenById(Long id);

    List<AlmacenDto> getAlmacenesDeMiEmpresa();
}