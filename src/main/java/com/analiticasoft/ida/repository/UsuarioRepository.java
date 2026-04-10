package com.analiticasoft.ida.repository;

import com.analiticasoft.ida.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Spring Data JPA creará automáticamente la consulta para este método
    Optional<Usuario> findByEmail(String email);

    @Query("SELECT u FROM Usuario u JOIN FETCH u.rol WHERE u.email = :email")
    Optional<Usuario> findByEmailWithRol(String email);

    // Trae todos los usuarios y precarga sus roles
    @Query("SELECT u FROM Usuario u JOIN FETCH u.rol")
    List<Usuario> findAllWithRol();

    // Cuenta todos los usuarios
    long count();

    // Cuenta solo los usuarios activos
    long countByIsActiveTrue();

}