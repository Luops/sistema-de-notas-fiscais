package dev.ellyon.sistemanotas.controller.test;

import dev.ellyon.sistemanotas.service.CriptografiaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/test")
public class TestsController {
    private final CriptografiaService criptografiaService;

    public TestsController(CriptografiaService criptografiaService) {
        this.criptografiaService = criptografiaService;
    }

    @GetMapping("/criptografia")
    public ResponseEntity<?> testar() {
        try {
            // Validar configuração
            boolean valido = criptografiaService.validarConfiguracao();

            if (!valido) {
                return ResponseEntity.status(500).body(Map.of(
                        "erro", "Configuração de criptografia inválida"
                ));
            }

            // Testar com senha de exemplo
            String senhaOriginal = "MinhaSenhaSecreta@123";

            // Criptografar
            String senhaCriptografada = criptografiaService.criptografar(senhaOriginal);

            // Descriptografar
            String senhaDescriptografada = criptografiaService.descriptografar(senhaCriptografada);

            return ResponseEntity.ok(Map.of(
                    "valido", true,
                    "senhaOriginal", senhaOriginal,
                    "senhaCriptografada", senhaCriptografada,
                    "senhaDescriptografada", senhaDescriptografada,
                    "sucesso", senhaOriginal.equals(senhaDescriptografada)
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "erro", e.getMessage()
            ));
        }
    }
}
