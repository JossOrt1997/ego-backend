package com.analiticasoft.ida.controller;

import com.analiticasoft.ida.dto.ClienteDto;
import com.analiticasoft.ida.service.ClienteService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Importar
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clientes") // Ruta base para clientes
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    /**
     * Endpoint para crear un nuevo cliente.
     * Requiere permiso de 'crear' en el módulo 'CRM'.
     */
    @PostMapping
    @PreAuthorize("hasPermission('CRM', 'crear')")
    public ResponseEntity<ClienteDto> crearCliente(@Valid @RequestBody ClienteDto clienteDto) {
        ClienteDto clienteCreado = clienteService.crearCliente(clienteDto);
        return new ResponseEntity<>(clienteCreado, HttpStatus.CREATED);
    }

    /**
     * Endpoint para obtener todos los clientes de la empresa del usuario.
     * Requiere permiso de 'leer' en el módulo 'CRM'.
     */
    @GetMapping
    @PreAuthorize("hasPermission('CRM', 'leer')")
    public ResponseEntity<List<ClienteDto>> getClientesDeMiEmpresa() {
        List<ClienteDto> clientes = clienteService.getClientesDeMiEmpresa();
        return ResponseEntity.ok(clientes);
    }

    /**
     * Endpoint para obtener un cliente por su ID.
     * Requiere permiso de 'leer' en el módulo 'CRM'.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasPermission('CRM', 'leer')")
    public ResponseEntity<ClienteDto> getClienteById(@PathVariable Long id) {
        return clienteService.getClienteById(id)
                .map(ResponseEntity::ok) // Si lo encuentra, devuelve 200 OK con el cliente
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado")); // Si no, lanza 404
    }

    /**
     * Endpoint para actualizar un cliente.
     * Requiere permiso de 'actualizar' en el módulo 'CRM'.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasPermission('CRM', 'actualizar')")
    public ResponseEntity<ClienteDto> actualizarCliente(@PathVariable Long id, @Valid @RequestBody ClienteDto clienteDto) {
        ClienteDto clienteActualizado = clienteService.actualizarCliente(id, clienteDto);
        return ResponseEntity.ok(clienteActualizado);
    }

    // Aquí podríamos añadir un @DeleteMapping("/{id}") con hasPermission('CRM', 'borrar')

    /**
     * Endpoint para borrar un cliente.
     * Requiere permiso de 'borrar' en el módulo 'CRM'.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission('CRM', 'borrar')")
    public ResponseEntity<Void> deleteCliente(@PathVariable Long id) {
        clienteService.deleteCliente(id);
        return ResponseEntity.noContent().build(); // 204 No Content es la respuesta estándar para DELETE
    }
}