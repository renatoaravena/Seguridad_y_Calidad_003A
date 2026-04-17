package com.duoc.backend;

import com.duoc.backend.User;
import com.duoc.backend.JWTAuthenticationConfig;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;



@RestController
@Tag(name = "Autenticación", description = "Operaciones de login")
public class LoginController {

    @Autowired
    JWTAuthenticationConfig jwtAuthenticationConfig;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Operation(
            summary = "Iniciar sesión",
            description = "Autentica al usuario y retorna un token JWT",
            security = {},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Token JWT",
                            content = @Content(schema = @Schema(type = "string", example = "eyJhbGci..."))),
                    @ApiResponse(responseCode = "500", description = "Credenciales inválidas", content = @Content)
            }
    )
    @PostMapping("login")
    public String login(@RequestBody LoginRequest loginRequest) {

        logger.info("Recibida solicitud de login para usuario: {}", loginRequest.getUsername());

        /**
         * En el ejemplo no se realiza la correcta validación del usuario
         */

        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
        logger.info("Usuario encontrado en la base de datos: {}", userDetails.getUsername());

        if (!passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
            throw new RuntimeException("Invalid login");
        }

        String token = jwtAuthenticationConfig.getJWTToken(loginRequest.getUsername());

        return token;

    }

    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }
        public void setUsername(String username) {
            this.username = username;
        }
        public String getPassword() {
            return password;
        }
        public void setPassword(String password) {
            this.password = password;
        }
    }

}