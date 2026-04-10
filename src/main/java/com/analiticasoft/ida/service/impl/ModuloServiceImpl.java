package com.analiticasoft.ida.service.impl;

import com.analiticasoft.ida.dto.ModuloDto;
import com.analiticasoft.ida.repository.ModuloRepository;
import com.analiticasoft.ida.service.ModuloService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModuloServiceImpl implements ModuloService {

    private final ModuloRepository moduloRepository;

    @Override
    public List<ModuloDto> getAllModulos() {
        return moduloRepository.findAll()
                .stream()
                .map(this::convertirAModuloDto)
                .collect(Collectors.toList());
    }

    private ModuloDto convertirAModuloDto(com.analiticasoft.ida.entity.Modulo modulo) {
        ModuloDto dto = new ModuloDto();
        dto.setId(modulo.getId());
        dto.setNombre(modulo.getNombre());
        dto.setDescripcion(modulo.getDescripcion());
        return dto;
    }
}