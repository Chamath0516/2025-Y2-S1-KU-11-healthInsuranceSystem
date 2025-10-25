package com.hims.healthinsurancesystem.service;

import com.hims.healthinsurancesystem.model.FinanceManager;
import com.hims.healthinsurancesystem.repository.FinanceManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FinanceManagerService {

    @Autowired
    private FinanceManagerRepository financeManagerRepository;

    public FinanceManager save(FinanceManager manager) {
        return financeManagerRepository.save(manager);
    }

    public Optional<FinanceManager> findById(Long id) {
        return financeManagerRepository.findById(id);
    }

    public List<FinanceManager> findAll() {
        return financeManagerRepository.findAll();
    }

    public void deleteById(Long id) {
        financeManagerRepository.deleteById(id);
    }
}