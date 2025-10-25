package com.hims.healthinsurancesystem.controller;

import com.hims.healthinsurancesystem.model.HealthcareProvider;
import com.hims.healthinsurancesystem.model.Hospital;
import com.hims.healthinsurancesystem.repository.HealthcareProviderRepository;
import com.hims.healthinsurancesystem.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class SearchController {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private HealthcareProviderRepository providerRepository;

    /**
     * Search hospitals by name for autocomplete.
     * Example: /api/search/hospitals?query=general
     */
    @GetMapping("/api/search/hospitals")
    public List<Map<String, Object>> searchHospitals(@RequestParam(name = "query", required = false) String query) {
        List<Hospital> hospitals;
        if (query == null || query.trim().isEmpty()) {
            hospitals = hospitalRepository.findAll();
        } else {
            hospitals = hospitalRepository.findByNameContainingIgnoreCase(query.trim());
        }
        // Return minimal fields to avoid exposing sensitive data
        return hospitals.stream().map(h -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", h.getId());
            m.put("name", h.getName());
            m.put("location", h.getLocation());
            return m;
        }).collect(Collectors.toList());
    }

    /**
     * Search doctors by hospitalId and query (first/last name or specialization).
     * Example: /api/search/doctors?hospitalId=5&query=smith
     */
    @GetMapping("/api/search/doctors")
    public List<Map<String, Object>> searchDoctors(@RequestParam(name = "hospitalId", required = false) Long hospitalId,
                                                   @RequestParam(name = "query", required = false) String query) {
        if (hospitalId == null) return Collections.emptyList();

        List<HealthcareProvider> providers;
        if (query == null || query.trim().isEmpty()) {
            providers = providerRepository.findByHospitalId(hospitalId);
        } else {
            providers = providerRepository.searchByHospitalAndQuery(hospitalId, query.trim());
        }

        return providers.stream().map(p -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", p.getId());
            m.put("firstName", p.getFirstName());
            m.put("lastName", p.getLastName());
            m.put("specialization", p.getSpecialization());
            return m;
        }).collect(Collectors.toList());
    }
}
