package com.analiticasoft.ida.service;

import com.analiticasoft.ida.entity.RefreshToken;
import com.analiticasoft.ida.entity.Usuario;
import com.analiticasoft.ida.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.secret}")
    private String secretKey; // Base64 recomendado

    @Value("${jwt.access.expiration-ms:900000}") // 15 min
    private long accessExpirationMs;

    @Value("${jwt.refresh.expiration-ms:604800000}") // 7 días
    private long refreshExpirationMs;

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(Usuario usuario) {
        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("rol", usuario.getRol() != null ? usuario.getRol().getNombre() : null)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessExpirationMs))
                .signWith(getSignInKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username != null && username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    /**
     * Crea (o reemplaza) el refresh token 1:1 por usuario (según tu entidad).
     */
    @Transactional
    public String createRefreshToken(Usuario usuario) {
        String token = UUID.randomUUID().toString();
        Instant expiry = Instant.now().plusMillis(refreshExpirationMs);

        // Tu repos ofrece findByUsuarioId (no findByUsuario)
        Optional<RefreshToken> existing = refreshTokenRepository.findByUsuarioId(usuario.getId());
        RefreshToken rt = existing.orElseGet(RefreshToken::new);
        rt.setUsuario(usuario);
        rt.setToken(token);
        rt.setExpiryDate(expiry);
        refreshTokenRepository.save(rt);

        return token;
    }

    /**
     * Valida si el Refresh Token existe y no expiró.
     */
    public Optional<RefreshToken> validateRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken)
                .filter(rt -> rt.getExpiryDate() != null && rt.getExpiryDate().isAfter(Instant.now()));
    }
}
