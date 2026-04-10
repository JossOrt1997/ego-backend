package com.analiticasoft.ida.config;

import com.analiticasoft.ida.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component; // <-- Importante
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component // <-- Asegúrate de que esta anotación esté presente
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService; // Spring lo inyectará automáticamente

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Si no hay cabecera Authorization o no empieza con "Bearer ", pasamos al siguiente filtro
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extraemos el token (después de "Bearer ")
        jwt = authHeader.substring(7);

        try {
            userEmail = jwtService.extractUsername(jwt); // Extraemos el email del token
        } catch (Exception e) {
            // Si hay error al extraer (token inválido/expirado), no autenticamos y continuamos
            filterChain.doFilter(request, response);
            return;
        }


        // Si tenemos email y el usuario AÚN NO está autenticado en esta petición
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Cargamos los detalles del usuario desde la base de datos
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // Verificamos si el token es válido para este usuario
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // Creamos el objeto de autenticación
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // No usamos credenciales (password) aquí
                        userDetails.getAuthorities() // Los roles/permisos
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                // ESTABLECEMOS LA AUTENTICACIÓN en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Continuamos con el resto de la cadena de filtros de Spring Security
        filterChain.doFilter(request, response);
    }
}