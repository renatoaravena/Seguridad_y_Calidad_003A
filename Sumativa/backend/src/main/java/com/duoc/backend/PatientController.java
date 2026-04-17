package com.duoc.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
public class PatientController {

    @Autowired
    private PatientRepository patientRepository;

    @PostMapping("/patients")
    public ResponseEntity<?> createPatient(@RequestBody Patient patient) {
        try {
            patientRepository.save(patient);
            return ResponseEntity.status(HttpStatus.CREATED).body(patient);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error al registrar: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
            //return ResponseEntity.badRequest().body("Error registering patient: " + e.getMessage());
        }
    }

    @GetMapping("/patients")
    public ResponseEntity<Iterable<Patient>> getAllPatients() {
        try {
            Iterable<Patient> patients = patientRepository.findAll();
            return ResponseEntity.ok(patients);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/patients/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Integer id) {
        try {
            Optional<Patient> patient = patientRepository.findById(id);
            if (patient.isPresent()) {
                return ResponseEntity.ok(patient.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/patients/{id}")
    public ResponseEntity<?> updatePatient(@PathVariable Integer id, @RequestBody Patient patientDetails) {
        try {
            Optional<Patient> patient = patientRepository.findById(id);
            if (patient.isPresent()) {
                Patient patientToUpdate = patient.get();
                if (patientDetails.getName() != null) patientToUpdate.setName(patientDetails.getName());
                if (patientDetails.getSpecies() != null) patientToUpdate.setSpecies(patientDetails.getSpecies());
                if (patientDetails.getBreed() != null) patientToUpdate.setBreed(patientDetails.getBreed());
                if (patientDetails.getAge() != null) patientToUpdate.setAge(patientDetails.getAge());
                if (patientDetails.getOwner() != null) patientToUpdate.setOwner(patientDetails.getOwner());
                patientRepository.save(patientToUpdate);
                return ResponseEntity.ok(patientToUpdate);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error al actualizar: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/patients/{id}")
    public ResponseEntity<?> deletePatient(@PathVariable Integer id) {
        try {
            Optional<Patient> patient = patientRepository.findById(id);
            if (patient.isPresent()) {
                patientRepository.deleteById(id);
                Map<String, String> response = new HashMap<>();
                response.put("message", "Paciente eliminado correctamente");
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error al eliminar: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}