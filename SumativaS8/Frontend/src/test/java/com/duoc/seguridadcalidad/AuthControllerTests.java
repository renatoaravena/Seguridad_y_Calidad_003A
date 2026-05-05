package com.duoc.seguridadcalidad;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTests {

    @Mock
    private BackendService backendService;

    @Mock
    private JwtCookieService jwtCookieService;

    @InjectMocks
    private AuthController authController;

    // ── login ─────────────────────────────────────────────────────────────────

    @Test
    void loginShouldReturnNoCOntentAndSetCookieWhenCredentialsAreValid() {
        AuthRequest request = new AuthRequest();
        request.setUsername("user");
        request.setPassword("pass");

        AuthResponse authResponse = new AuthResponse("jwt-token");

        org.springframework.http.ResponseCookie cookie =
                org.springframework.http.ResponseCookie.from("AUTH_TOKEN", "jwt-token").build();

        when(backendService.login(request)).thenReturn(authResponse);
        when(jwtCookieService.createAuthCookie("jwt-token")).thenReturn(cookie);

        ResponseEntity<?> response = authController.createAuthenticationToken(request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNotNull(response.getHeaders().get("Set-Cookie"));
    }

    @Test
    void loginShouldReturnStatusFromBackendWhenHttpErrorOccurs() {
        AuthRequest request = new AuthRequest();
        request.setUsername("user");
        request.setPassword("wrong");

        when(backendService.login(request))
                .thenThrow(HttpClientErrorException.create(
                        HttpStatus.UNAUTHORIZED, "Unauthorized", null, null, null));

        ResponseEntity<?> response = authController.createAuthenticationToken(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void loginShouldReturnServiceUnavailableWhenBackendIsDown() {
        AuthRequest request = new AuthRequest();
        request.setUsername("user");
        request.setPassword("pass");

        when(backendService.login(request))
                .thenThrow(new ResourceAccessException("Connection refused"));

        ResponseEntity<?> response = authController.createAuthenticationToken(request);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    }

    @Test
    void loginShouldReturnInternalServerErrorOnUnexpectedException() {
        AuthRequest request = new AuthRequest();
        request.setUsername("user");
        request.setPassword("pass");

        when(backendService.login(request))
                .thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<?> response = authController.createAuthenticationToken(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    // ── logout ────────────────────────────────────────────────────────────────

    @Test
    void logoutShouldReturnNoContentAndClearCookie() {
        org.springframework.http.ResponseCookie clearedCookie =
                org.springframework.http.ResponseCookie.from("AUTH_TOKEN", "").maxAge(0).build();

        when(jwtCookieService.clearAuthCookie()).thenReturn(clearedCookie);

        ResponseEntity<Void> response = authController.logout();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNotNull(response.getHeaders().get("Set-Cookie"));
    }

    // ── session ───────────────────────────────────────────────────────────────

    @Test
    void sessionShouldReturnNoContentWhenTokenIsPresent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        when(jwtCookieService.extractToken(request)).thenReturn("valid-token");

        ResponseEntity<Void> response = authController.session(request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void sessionShouldReturnUnauthorizedWhenTokenIsAbsent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        when(jwtCookieService.extractToken(request)).thenReturn(null);

        ResponseEntity<Void> response = authController.session(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}