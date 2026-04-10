package com.analiticasoft.ida.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder // Usamos @Builder para una construcción más fácil en el servicio
public class DashboardStatsDto {

    private long totalEmpresas;
    private long totalUsuarios;
    private long totalUsuariosActivos;
    private String planMasPopular;

    // Aquí podríamos añadir más, como:
    // private long nuevosRegistrosMes;
    // private double ingresosMensualesEstimados;
}