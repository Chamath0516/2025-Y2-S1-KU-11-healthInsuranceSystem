package com.hims.healthinsurancesystem.repository;

import com.hims.healthinsurancesystem.model.Customer;
import com.hims.healthinsurancesystem.model.Dependent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DependentRepository extends JpaRepository<Dependent, Long> {
    List<Dependent> findByCustomer(Customer customer);
}
