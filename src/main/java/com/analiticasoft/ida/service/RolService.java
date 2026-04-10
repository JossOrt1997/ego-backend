package com.analiticasoft.ida.service;

import com.analiticasoft.ida.dto.PermisoRequestDto;
import com.analiticasoft.ida.dto.RolDto;

public interface RolService {
    RolDto crearRol(RolDto rolDto);
    void asignarPermisos(PermisoRequestDto permisoRequest);
    // Aquí podríamos añadir métodos para listar roles, etc.
}