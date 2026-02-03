package dev.ellyon.sistemanotas.service;

import dev.ellyon.sistemanotas.dto.auth.LoginRequestDTO;
import dev.ellyon.sistemanotas.dto.auth.LoginResponseDTO;

public interface AuthService {
    LoginResponseDTO autenticar(LoginRequestDTO dto);
}