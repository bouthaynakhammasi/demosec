package com.aziz.demosec.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping("")
public class DemoController {

    // Accessible uniquement aux ADMIN
    @GetMapping("/admin/hello")
    public String admin() {
        return "Hello ADMIN";
    }

    // Accessible uniquement aux DOCTOR
    @GetMapping("/doctor/hello")
    public String doctor() {
        return "Hello DOCTOR";
    }

    // Accessible uniquement aux CLINIC
    @GetMapping("/clinic/hello")
    public String clinic() {
        return "Hello CLINIC";
    }

    // Accessible uniquement aux PHARMACIST
    @GetMapping("/pharmacist/hello")
    public String pharmacist() {
        return "Hello PHARMACIST";
    }

    // Accessible uniquement aux LABORATORY
    @GetMapping("/laboratory/hello")
    public String laboratory() {
        return "Hello LABORATORY";
    }

    // Accessible uniquement aux NUTRITIONIST
    @GetMapping("/nutritionist/hello")
    public String nutritionist() {
        return "Hello NUTRITIONIST";
    }

    // Accessible uniquement aux VISITOR
    @GetMapping("/visitor/hello")
    public String visitor() {
        return "Hello VISITOR";
    }

    // Accessible uniquement aux PATIENT
    @GetMapping("/patient/hello")
    public String patient() {
        return "Hello PATIENT";
    }

    // Accessible uniquement aux HOME_CARE_PROVIDER
    @GetMapping("/home-care/hello")
    public String homecare() {
        return "Hello HOME_CARE_PROVIDER";
    }
}

