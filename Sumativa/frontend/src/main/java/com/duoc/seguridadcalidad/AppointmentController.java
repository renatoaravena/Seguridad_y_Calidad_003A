package com.duoc.seguridadcalidad;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;

@Controller
public class AppointmentController {

    @GetMapping("/appointments")
    public String listAppointments() {
        return "appointments";
    }

    @GetMapping("/appointments/new")
    public String showCreateForm() {
        return "new_appointment";
    }

    @PostMapping("/appointments")
    public String saveAppointment() {
        return "redirect:/appointments";
    }

    @GetMapping("/appointments/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model) {
        model.addAttribute("appointmentId", id);
        return "edit_appointment";
    }

    @PutMapping("/appointments/{id}")
    public String updateAppointment(@PathVariable Integer id) {
        return "redirect:/appointments";
    }

    @DeleteMapping("/appointments/{id}")
    public String deleteAppointment(@PathVariable Integer id) {
        return "redirect:/appointments";
    }
}
