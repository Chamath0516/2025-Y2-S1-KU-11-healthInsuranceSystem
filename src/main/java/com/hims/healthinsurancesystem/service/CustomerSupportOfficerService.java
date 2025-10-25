package com.hims.healthinsurancesystem.service;

import com.hims.healthinsurancesystem.model.CustomerSupportOfficer;
import com.hims.healthinsurancesystem.repository.CustomerSupportOfficerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerSupportOfficerService {

    @Autowired
    private CustomerSupportOfficerRepository customerSupportOfficerRepository;

    public CustomerSupportOfficer save(CustomerSupportOfficer officer) {
        return customerSupportOfficerRepository.save(officer);
    }

    public Optional<CustomerSupportOfficer> findById(Long id) {
        return customerSupportOfficerRepository.findById(id);
    }

    public List<CustomerSupportOfficer> findAll() {
        return customerSupportOfficerRepository.findAll();
    }

    public void deleteById(Long id) {
        customerSupportOfficerRepository.deleteById(id);
    }
}