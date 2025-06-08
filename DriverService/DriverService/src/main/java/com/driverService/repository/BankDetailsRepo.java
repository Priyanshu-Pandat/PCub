package com.driverService.repository;

import com.driverService.entity.BankDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankDetailsRepo extends JpaRepository<BankDetails , Long> {

}
