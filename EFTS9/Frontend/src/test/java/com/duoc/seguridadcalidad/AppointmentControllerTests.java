package com.duoc.seguridadcalidad;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentRestControllerTests {

    @Mock
    private BackendService backendService;

    @Mock
    private JwtCookieService jwtCookieService;

    @InjectMocks
    private AppointmentRestController appointmentRestController;

    // ── getAll ────────────────────────────────────────────────────────────────

    @Test
    void getAllShouldReturnOkWithListWhenTokenIsPresent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        List<Map<String, Object>> appointments = List.of(
                Map.of("id", 1, "reason", "Vacuna"),
                Map.of("id", 2, "reason", "Control")
        );

        when(jwtCookieService.extractToken(request)).thenReturn("valid-token");
        when(backendService.getAppointments("valid-token")).thenReturn(appointments);

        ResponseEntity<List<Map<String, Object>>> response = appointmentRestController.getAll(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void getAllShouldReturnUnauthorizedWhenTokenIsAbsent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        when(jwtCookieService.extractToken(request)).thenReturn(null);

        ResponseEntity<List<Map<String, Object>>> response = appointmentRestController.getAll(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
        verify(backendService, never()).getAppointments(any());
    }

    // ── create ────────────────────────────────────────────────────────────────

    @Test
    void createShouldReturnOkWithSavedAppointmentWhenTokenIsPresent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Map<String, Object> payload = Map.of("patientId", 1, "reason", "Consulta");
        Map<String, Object> saved = Map.of("id", 10, "patientId", 1, "reason", "Consulta");

        when(jwtCookieService.extractToken(request)).thenReturn("valid-token");
        when(backendService.createAppointment("valid-token", payload)).thenReturn(saved);

        ResponseEntity<Map<String, Object>> response = appointmentRestController.create(request, payload);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(10, response.getBody().get("id"));
    }

    @Test
    void createShouldReturnUnauthorizedWhenTokenIsAbsent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Map<String, Object> payload = Map.of("patientId", 1, "reason", "Consulta");

        when(jwtCookieService.extractToken(request)).thenReturn(null);

        ResponseEntity<Map<String, Object>> response = appointmentRestController.create(request, payload);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
        verify(backendService, never()).createAppointment(any(), any());
    }
}