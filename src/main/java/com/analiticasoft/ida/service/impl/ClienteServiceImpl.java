package com.analiticasoft.ida.service.impl;

import com.analiticasoft.ida.dto.ClienteDto;
import com.analiticasoft.ida.entity.Cliente;
import com.analiticasoft.ida.entity.Empresa;
import com.analiticasoft.ida.entity.Persona;
import com.analiticasoft.ida.entity.Usuario;
import com.analiticasoft.ida.repository.ClienteRepository;
import com.analiticasoft.ida.repository.PersonaRepository;
import com.analiticasoft.ida.repository.UsuarioRepository;
import com.analiticasoft.ida.service.AuditService; // <-- 1. IMPORTAR AuditService
import com.analiticasoft.ida.service.ClienteService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map; // Importar para auditoría
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final PersonaRepository personaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditService auditService; // <-- 2. INYECTAR AuditService

    @Override
    @Transactional
    public ClienteDto crearCliente(ClienteDto clienteDto) {
        Usuario usuario = getUsuarioAutenticado();
        Empresa empresa = usuario.getEmpresa();

        Persona personaGuardada = null;
        // Si el DTO incluye un nombre (B2C), creamos una Persona
        if (clienteDto.getNombre() != null && !clienteDto.getNombre().isEmpty()) {
            Persona nuevaPersona = new Persona();
            nuevaPersona.setNombre(clienteDto.getNombre());
            nuevaPersona.setApellido(clienteDto.getApellido());
            nuevaPersona.setRfc(clienteDto.getRfc());
            personaGuardada = personaRepository.save(nuevaPersona);
        }

        // Creamos la entidad Cliente
        Cliente nuevoCliente = new Cliente();
        nuevoCliente.setEmpresa(empresa); // <-- Seguridad Multi-Tenant
        nuevoCliente.setPersona(personaGuardada);
        nuevoCliente.setNombreComercial(clienteDto.getNombreComercial());
        nuevoCliente.setRfc(clienteDto.getRfc());
        nuevoCliente.setEmailContacto(clienteDto.getEmailContacto());
        nuevoCliente.setTelefonoContacto(clienteDto.getTelefonoContacto());
        nuevoCliente.setStatus("ACTIVO"); // Estado por defecto

        Cliente clienteGuardado = clienteRepository.save(nuevoCliente);

        // --- 3. REGISTRAR AUDITORÍA ---
        auditService.logAction(
                usuario, // El objeto Usuario completo
                "CREATE_CLIENTE",
                Map.of(
                        "clienteId", clienteGuardado.getId(),
                        "nombreComercial", clienteGuardado.getNombreComercial(),
                        "rfc", clienteGuardado.getRfc()
                )
        );
        // --- FIN AUDITORÍA ---

        return convertirAClienteDto(clienteGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteDto> getClientesDeMiEmpresa() {
        Usuario usuario = getUsuarioAutenticado();
        Long empresaId = usuario.getEmpresa().getId();

        return clienteRepository.findAllByEmpresaId(empresaId)
                .stream()
                .map(this::convertirAClienteDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ClienteDto> getClienteById(Long clienteId) {
        Usuario usuario = getUsuarioAutenticado();
        Long empresaId = usuario.getEmpresa().getId();

        return clienteRepository.findByIdAndEmpresaId(clienteId, empresaId)
                .map(this::convertirAClienteDto);
    }

    @Override
    @Transactional
    public ClienteDto actualizarCliente(Long clienteId, ClienteDto clienteDto) {
        Usuario usuario = getUsuarioAutenticado();
        Long empresaId = usuario.getEmpresa().getId();

        // Buscamos el cliente asegurándonos que pertenezca a la empresa del usuario
        Cliente clienteExistente = clienteRepository.findByIdAndEmpresaId(clienteId, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado o no pertenece a su empresa"));

        // Guardar estado anterior para auditoría
        String rfcAnterior = clienteExistente.getRfc();
        String emailAnterior = clienteExistente.getEmailContacto();

        // Actualizamos la entidad
        clienteExistente.setNombreComercial(clienteDto.getNombreComercial());
        clienteExistente.setRfc(clienteDto.getRfc());
        clienteExistente.setEmailContacto(clienteDto.getEmailContacto());
        clienteExistente.setTelefonoContacto(clienteDto.getTelefonoContacto());

        if (clienteExistente.getPersona() != null) {
            Persona persona = clienteExistente.getPersona();
            persona.setNombre(clienteDto.getNombre());
            persona.setApellido(clienteDto.getApellido());
            persona.setRfc(clienteDto.getRfc());
            personaRepository.save(persona);
        }

        Cliente clienteActualizado = clienteRepository.save(clienteExistente);

        // --- 3. REGISTRAR AUDITORÍA ---
        auditService.logAction(
                usuario,
                "UPDATE_CLIENTE",
                Map.of(
                        "clienteId", clienteId,
                        "cambios", Map.of(
                                "rfc", Map.of("anterior", rfcAnterior, "nuevo", clienteDto.getRfc()),
                                "emailContacto", Map.of("anterior", emailAnterior, "nuevo", clienteDto.getEmailContacto())
                        )
                )
        );
        // --- FIN AUDITORÍA ---

        return convertirAClienteDto(clienteActualizado);
    }

    @Override
    @Transactional
    public void deleteCliente(Long clienteId) {
        Usuario usuario = getUsuarioAutenticado();
        Long empresaId = usuario.getEmpresa().getId();

        Cliente cliente = clienteRepository.findByIdAndEmpresaId(clienteId, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado o no pertenece a su empresa"));

        // Guardar datos para auditoría ANTES de borrar
        String nombreBorrado = cliente.getNombreComercial() != null ? cliente.getNombreComercial() : cliente.getRfc();

        clienteRepository.delete(cliente);

        if (cliente.getPersona() != null) {
            personaRepository.delete(cliente.getPersona());
        }

        // --- 3. REGISTRAR AUDITORÍA ---
        auditService.logAction(
                usuario,
                "DELETE_CLIENTE",
                Map.of(
                        "clienteBorradoId", clienteId,
                        "nombreCliente", nombreBorrado
                )
        );
        // --- FIN AUDITORÍA ---
    }

    // --- Métodos Helper ---

    private Usuario getUsuarioAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmailWithRol(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario autenticado no encontrado en BD"));
    }

    private ClienteDto convertirAClienteDto(Cliente cliente) {
        ClienteDto dto = new ClienteDto();
        dto.setId(cliente.getId());
        dto.setNombreComercial(cliente.getNombreComercial());
        dto.setRfc(cliente.getRfc());
        dto.setEmailContacto(cliente.getEmailContacto());
        dto.setTelefonoContacto(cliente.getTelefonoContacto());
        dto.setStatus(cliente.getStatus());

        if (cliente.getPersona() != null) {
            dto.setNombre(cliente.getPersona().getNombre());
            dto.setApellido(cliente.getPersona().getApellido());
        }
        return dto;
    }
}