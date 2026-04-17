package com.duoc.seguridadcalidad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patients")
public class PatientRestController {

    private static final Logger log = LoggerFactory.getLogger(PatientRestController.class);
    private final BackendService backendService;

    public PatientRestController(BackendService backendService) {
        this.backendService = backendService;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAll(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        log.debug("GET /api/patients Authorization={}", authorizationHeader);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.warn("GET /api/patients missing or invalid Authorization header");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authorizationHeader.substring(7);
        List<Map<String, Object>> patients = backendService.getPatients(token);
        log.debug("GET /api/patients returning {} patients", patients.size());
        return ResponseEntity.ok(patients);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                         @RequestBody Map<String, Object> patient) {
        log.debug("POST /api/patients Authorization={} payload={}", authorizationHeader, patient);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.warn("POST /api/patients missing or invalid Authorization header");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authorizationHeader.substring(7);
        Map<String, Object> saved = backendService.createPatient(token, patient);
        log.debug("POST /api/patients saved={}", saved);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                         @PathVariable Integer id,
                                         @RequestBody Map<String, Object> patient) {
        log.debug("PUT /api/patients/{} Authorization={} payload={}", id, authorizationHeader, patient);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.warn("PUT /api/patients/{} missing or invalid Authorization header", id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authorizationHeader.substring(7);
        Map<String, Object> updated = backendService.updatePatient(token, id, patient);
        log.debug("PUT /api/patients/{} updated={}", id, updated);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@RequestHeader(value = "Authorization", required = false) String authorizationHeader,
                                          @PathVariable Integer id) {
        log.debug("DELETE /api/patients/{} Authorization={}", id, authorizationHeader);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.warn("DELETE /api/patients/{} missing or invalid Authorization header", id);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authorizationHeader.substring(7);
        Map<String, Object> deleted = backendService.deletePatient(token, id);
        log.debug("DELETE /api/patients/{} deleted={}", id, deleted);
        return ResponseEntity.ok(deleted);
    }
}
