package com.hims.healthinsurancesystem.service;

import com.hims.healthinsurancesystem.model.Customer;
import com.hims.healthinsurancesystem.model.Dependent;
import com.hims.healthinsurancesystem.repository.DependentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DependentService {
    @Autowired
    private DependentRepository dependentRepository;

    public Dependent save(Dependent dependent) {
        return dependentRepository.save(dependent);
    }

    public List<Dependent> findByCustomer(Customer customer) {
        return dependentRepository.findByCustomer(customer);
    }

    public Optional<Dependent> findById(Long id) {
        return dependentRepository.findById(id);
    }

    public void deleteById(Long id) {
        dependentRepository.deleteById(id);
    }
}
