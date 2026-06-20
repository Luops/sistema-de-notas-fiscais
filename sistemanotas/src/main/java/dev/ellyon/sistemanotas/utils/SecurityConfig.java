package dev.ellyon.sistemanotas.utils;

import dev.ellyon.sistemanotas.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // Habilitar @PreAuthorize
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Configuração CORS personalizada
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // ========================================
                        // ROTAS PÚBLICAS
                        // ========================================
                        .requestMatchers("/api/v1/usuario/auth/**").permitAll() // Login
                        .requestMatchers("/api/v1/nfe/test/**").permitAll() // Testes (remover em produção)

                        // ========================================
                        // EMPRESAS
                        // ========================================
                        .requestMatchers(HttpMethod.GET, "/api/v1/empresa/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/empresa/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/empresa/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/empresa/**").hasRole("ADMIN")

                        // ========================================
                        // CLIENTES
                        // ========================================
                        .requestMatchers(HttpMethod.GET, "/api/v1/cliente/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/cliente/**").hasAnyRole("ADMIN", "VENDEDOR")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/cliente/**").hasAnyRole("ADMIN", "VENDEDOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/cliente/**").hasRole("ADMIN")

                        // ========================================
                        // PRODUTOS
                        // ========================================
                        .requestMatchers(HttpMethod.GET, "/api/v1/produto/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/produto/**").hasAnyRole("ADMIN", "VENDEDOR")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/produto/**").hasAnyRole("ADMIN", "VENDEDOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/produto/**").hasRole("ADMIN")

                        // ========================================
                        // TIPO PRODUTOS
                        // ========================================
                        .requestMatchers(HttpMethod.GET, "/api/v1/tipoProduto/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/tipoProduto/**").hasAnyRole("ADMIN", "VENDEDOR")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/tipoProduto/**").hasAnyRole("ADMIN", "VENDEDOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/tipoProduto/**").hasRole("ADMIN")

                        // ========================================
                        // NOTAS
                        // ========================================
                        .requestMatchers(HttpMethod.GET, "/api/v1/notas/**").hasAnyRole("ADMIN", "VENDEDOR")
                        .requestMatchers(HttpMethod.POST, "/api/v1/notas/**").hasAnyRole("ADMIN", "VENDEDOR")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/notas/**").hasAnyRole("ADMIN", "VENDEDOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/notas/**").hasRole("ADMIN")

                        // ========================================
                        // NF-e
                        // ========================================
                        .requestMatchers(HttpMethod.POST, "/api/v1/nfe/emitir/**").hasAnyRole("ADMIN", "VENDEDOR")
                        .requestMatchers(HttpMethod.POST, "/api/v1/nfe/cancelar/**").hasAnyRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/nfe/*/danfe").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/nfe/*/danfe/visualizar").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/nfe/status-servico").permitAll()

                        // ========================================
                        // USUÁRIOS
                        // ========================================
                        .requestMatchers(HttpMethod.GET, "/api/v1/usuario/**").authenticated() // Autenticado edita (validação no @PreAuthorize)
                        .requestMatchers(HttpMethod.POST, "/api/v1/usuario/create").permitAll() // Qualquer um pode se registrar
                        .requestMatchers(HttpMethod.PUT, "/api/v1/usuario/update/{id}").authenticated() // Autenticado edita (validação no @PreAuthorize)
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/usuario/**").hasRole("ADMIN")

                        // ========================================
                        // EMPRESA-USUARIO
                        // ========================================
                        .requestMatchers("/api/v1/empresa-usuario/**").hasRole("ADMIN")

                        // ========================================
                        // NCM
                        // ========================================
                        .requestMatchers("/api/v1/ncm/**").permitAll()

                        // ========================================
                        // QUALQUER OUTRA ROTA: AUTENTICADO
                        // ========================================
                        .anyRequest().authenticated()
                )
                // Stateless - não mantém sessão
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Adicionar filtro JWT ANTES do filtro padrão
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Configuração CORS personalizada para permitir requisições do frontend
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
            CorsConfiguration configuration = new CorsConfiguration();

            // Permitir requisições do frontend
            configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));

            // Métodos HTTP permitidos
            configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

            // Headers permitidos
            configuration.setAllowedHeaders(Arrays.asList("*"));

            // Permitir envio de credenciais (cookies, authorization header)
            configuration.setAllowCredentials(true);

            // Cache da configuração CORS (1 hora)
            configuration.setMaxAge(3600L);

            // Registrar configuração para todas as rotas /api/**
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/api/**", configuration);

            return source;
    }
}