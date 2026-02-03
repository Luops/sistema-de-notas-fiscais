package dev.ellyon.sistemanotas.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration; // Em milissegundos (ex: 86400000 = 24 horas)

    /**
     * Gera um token JWT para o usuário
     */
    public String gerarToken(Long usuarioId, String email, String nome) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("usuarioId", usuarioId);
        claims.put("email", email);
        claims.put("nome", nome);

        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extrai o email (subject) do token
     */
    public String extrairEmail(String token) {
        return extrairClaims(token).getSubject();
    }

    /**
     * Extrai o ID do usuário do token
     */
    public Long extrairUsuarioId(String token) {
        return extrairClaims(token).get("usuarioId", Long.class);
    }

    /**
     * Valida se o token é válido
     */
    public boolean validarToken(String token, String email) {
        final String emailToken = extrairEmail(token);
        return (emailToken.equals(email) && !isTokenExpirado(token));
    }

    /**
     * Verifica se o token expirou
     */
    private boolean isTokenExpirado(String token) {
        return extrairExpiracao(token).before(new Date());
    }

    /**
     * Extrai a data de expiração do token
     */
    private Date extrairExpiracao(String token) {
        return extrairClaims(token).getExpiration();
    }

    /**
     * Extrai todas as claims do token
     */
    private Claims extrairClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Gera a chave de assinatura a partir do secret
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}