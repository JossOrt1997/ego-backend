package com.analiticasoft.ida.repository;

import com.analiticasoft.ida.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional; // Asegúrate de tener este import

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /**
     * Busca todos los clientes que pertenecen a un ID de empresa específico.
     * Esencial para la seguridad multi-tenant.
     */
    List<Cliente> findAllByEmpresaId(Long empresaId);

    /**
     * Busca un cliente específico por su ID Y el ID de la empresa.
     * Esto evita que un usuario de la Empresa A vea un cliente de la Empresa B
     * aunque adivine el ID.
     */
    Optional<Cliente> findByIdAndEmpresaId(Long clienteId, Long empresaId);
}