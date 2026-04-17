package com.duoc.seguridadcalidad;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;


@Controller
public class PatientController {

    @GetMapping("/patients")
    public String listPatients() {
        return "patients";
    }

    @GetMapping("/patients/new")
    public String showCreateForm() {
        return "new_patient";
    }

    @PostMapping("/patients")
    public String savePatient() {
        return "redirect:/patients";
    }

    @GetMapping("/patients/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model) {
        model.addAttribute("patientId", id);
        return "edit_patient";
    }

    @PutMapping("/patients/{id}")
    public String updatePatient(@PathVariable Integer id) {
        return "redirect:/patients";
    }

    @DeleteMapping("/patients/{id}")
    public String deletePatient(@PathVariable Integer id) {
        return "redirect:/patients";
    }
}
