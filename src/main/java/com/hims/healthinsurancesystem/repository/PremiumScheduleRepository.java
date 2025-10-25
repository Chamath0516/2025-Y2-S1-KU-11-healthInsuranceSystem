package com.hims.healthinsurancesystem.repository;

import com.hims.healthinsurancesystem.model.PremiumSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PremiumScheduleRepository extends JpaRepository<PremiumSchedule, Integer> {
    // Corrected query method: use 'Id' for the inherited 'id' field from User
    List<PremiumSchedule> findByPolicy_Customer_Id(Integer id);
}