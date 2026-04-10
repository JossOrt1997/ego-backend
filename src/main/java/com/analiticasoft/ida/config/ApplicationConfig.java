package com.analiticasoft.ida.config;

import com.analiticasoft.ida.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
// --- NUEVOS IMPORTS ---
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
// ---
import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UsuarioRepository usuarioRepository;

    // --- BEAN MOVIDO AQUÍ ---
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    // ---

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> usuarioRepository.findByEmailWithRol(username)
                .map(this::mapToUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }

    private org.springframework.security.core.userdetails.UserDetails mapToUserDetails(com.analiticasoft.ida.entity.Usuario usuario) {
        // ... (código igual) ...
        if (usuario.getRol() == null) {
            throw new UsernameNotFoundException("El usuario no tiene un rol asignado: " + usuario.getEmail());
        }
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombre().toUpperCase());
        return org.springframework.security.core.userdetails.User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPasswordHash())
                .authorities(Collections.singletonList(authority))
                .disabled(!usuario.getIsActive())
                .build();
    }

    // Ahora este método puede obtener el PasswordEncoder localmente
    @Bean
    public AuthenticationProvider authenticationProvider() { // Ya no necesita el parámetro PasswordEncoder
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder()); // Llama al método local
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}