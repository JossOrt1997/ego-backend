package com.analiticasoft.ida.service;

import com.analiticasoft.ida.dto.ClienteDto;
import java.util.List;
import java.util.Optional;

public interface ClienteService {

    /**
     * Crea un nuevo cliente para la empresa del usuario autenticado.
     */
    ClienteDto crearCliente(ClienteDto clienteDto);

    /**
     * Obtiene todos los clientes de la empresa del usuario autenticado.
     */
    List<ClienteDto> getClientesDeMiEmpresa();

    /**
     * Obtiene un cliente específico por ID, validando que pertenezca a la empresa del usuario.
     */
    Optional<ClienteDto> getClienteById(Long clienteId);

    /**
     * Actualiza un cliente existente, validando la pertenencia.
     */
    ClienteDto actualizarCliente(Long clienteId, ClienteDto clienteDto);

    void deleteCliente(Long clienteId);
}