package com.analiticasoft.ida.service.impl;

import com.analiticasoft.ida.dto.AuditLogResponseDto;
import com.analiticasoft.ida.entity.AuditLog;
import com.analiticasoft.ida.entity.Empresa;
import com.analiticasoft.ida.entity.Usuario;
import com.analiticasoft.ida.repository.AuditLogRepository;
import com.analiticasoft.ida.repository.UsuarioRepository;
import com.analiticasoft.ida.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {

    private final AuditLogRepository auditLogRepository;
    private final UsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponseDto> getAllLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByTimestampDesc(pageable)
                .map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponseDto> getLogsByMiEmpresa(Pageable pageable) {
        Usuario usuario = getUsuarioAutenticado();
        Long empresaId = usuario.getEmpresa().getId();

        return auditLogRepository.findByEmpresaIdOrderByTimestampDesc(empresaId, pageable)
                .map(this::convertToDto);
    }

    @Override
    @Transactional
    public void ingestLog(String userEmail, Long empresaId, String accion, Map<String, Object> detalles) {
        AuditLog log = new AuditLog();
        log.setUsuarioEmail(userEmail);
        log.setAccion(accion);

        if (empresaId != null) {
            Empresa empresa = new Empresa();
            empresa.setId(empresaId);
            log.setEmpresa(empresa);
        }

        log.setDetalles(detalles);
        auditLogRepository.save(log);
    }

    // --- Helpers ---

    private AuditLogResponseDto convertToDto(AuditLog log) {
        JsonNode detallesJson = objectMapper.valueToTree(log.getDetalles());

        return AuditLogResponseDto.builder()
                .id(log.getId())
                .timestamp(log.getTimestamp())
                .userEmail(log.getUsuarioEmail())
                .empresaId(log.getEmpresa() != null ? log.getEmpresa().getId() : null)
                .accion(log.getAccion())
                .detalles(detallesJson)
                .build();
    }

    private Usuario getUsuarioAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario autenticado no encontrado en BD"));
    }
}
