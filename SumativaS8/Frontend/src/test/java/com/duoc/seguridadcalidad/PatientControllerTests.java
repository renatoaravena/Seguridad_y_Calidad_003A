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
class PatientRestControllerTests {

    @Mock
    private BackendService backendService;

    @Mock
    private JwtCookieService jwtCookieService;

    @InjectMocks
    private PatientRestController patientRestController;

    // ── getAll ────────────────────────────────────────────────────────────────

    @Test
    void getAllShouldReturnOkWithPatientsWhenTokenIsPresent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        List<Map<String, Object>> patients = List.of(
                Map.of("id", 1, "name", "Luna"),
                Map.of("id", 2, "name", "Milo")
        );

        when(jwtCookieService.extractToken(request)).thenReturn("valid-token");
        when(backendService.getPatients("valid-token")).thenReturn(patients);

        ResponseEntity<List<Map<String, Object>>> response = patientRestController.getAll(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void getAllShouldReturnUnauthorizedWhenTokenIsAbsent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        when(jwtCookieService.extractToken(request)).thenReturn(null);

        ResponseEntity<List<Map<String, Object>>> response = patientRestController.getAll(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
        verify(backendService, never()).getPatients(any());
    }

    // ── create ────────────────────────────────────────────────────────────────

    @Test
    void createShouldReturnOkWithSavedPatientWhenTokenIsPresent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Map<String, Object> payload = Map.of("name", "Luna", "species", "Dog");
        Map<String, Object> saved = Map.of("id", 5, "name", "Luna", "species", "Dog");

        when(jwtCookieService.extractToken(request)).thenReturn("valid-token");
        when(backendService.createPatient("valid-token", payload)).thenReturn(saved);

        ResponseEntity<Map<String, Object>> response = patientRestController.create(request, payload);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(5, response.getBody().get("id"));
    }

    @Test
    void createShouldReturnUnauthorizedWhenTokenIsAbsent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        Map<String, Object> payload = Map.of("name", "Luna", "species", "Dog");

        when(jwtCookieService.extractToken(request)).thenReturn(null);

        ResponseEntity<Map<String, Object>> response = patientRestController.create(request, payload);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
        verify(backendService, never()).createPatient(any(), any());
    }
}