package com.duoc.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @Mock
    private JWTAuthenticationConfig jwtAuthenticationConfig;

    @Mock
    private MyUserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private LoginController loginController;

    private LoginController.LoginRequest loginRequest;
    private UserDetails mockUserDetails;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginController.LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        mockUserDetails = mock(UserDetails.class);
    }

    @Test
    void login_credencialesValidas_retornaToken() {
        // Arrange
        String expectedToken = "Bearer eyJhbGciOiJIUzI1NiJ9.test";
        when(mockUserDetails.getPassword()).thenReturn("encodedPassword");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(mockUserDetails);
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);
        when(jwtAuthenticationConfig.getJWTToken("testuser")).thenReturn(expectedToken);

        // Act
        String result = loginController.login(loginRequest);

        // Assert
        assertEquals(expectedToken, result);
        verify(userDetailsService).loadUserByUsername("testuser");
        verify(passwordEncoder).matches("password123", "encodedPassword");
        verify(jwtAuthenticationConfig).getJWTToken("testuser");
    }

    @Test
    void login_contrasenaInvalida_lanzaRuntimeException() {
        // Arrange
        when(mockUserDetails.getPassword()).thenReturn("encodedPassword");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(mockUserDetails);
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> loginController.login(loginRequest));
        assertEquals("Invalid login", exception.getMessage());
        verify(jwtAuthenticationConfig, never()).getJWTToken(anyString());
    }

    @Test
    void login_usuarioNoExiste_lanzaUsernameNotFoundException() {
        // Arrange
        when(userDetailsService.loadUserByUsername("testuser"))
                .thenThrow(new UsernameNotFoundException("testuser"));

        // Act & Assert
        assertThrows(UsernameNotFoundException.class,
                () -> loginController.login(loginRequest));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtAuthenticationConfig, never()).getJWTToken(anyString());
    }

    @Test
    void loginRequest_gettersYSetters_retornanValoresCorrectamente() {
        // Arrange
        LoginController.LoginRequest request = new LoginController.LoginRequest();

        // Act
        request.setUsername("usuario");
        request.setPassword("secreto");

        // Assert
        assertEquals("usuario", request.getUsername());
        assertEquals("secreto", request.getPassword());
    }
}
