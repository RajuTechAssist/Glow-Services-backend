package com.glowservices.glow_services_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.glowservices.glow_services_backend.model.entity.Customer.CustomerStatus;

import com.glowservices.glow_services_backend.model.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByPhone(String phone);
    List<Customer> findByStatus(CustomerStatus status);
    List<Customer> findByCity(String city);
    
    @Query("SELECT c FROM Customer c WHERE c.totalBookings > 0 ORDER BY c.totalSpent DESC")
    List<Customer> findTopCustomers();
    
    @Query("SELECT c FROM Customer c WHERE c.loyaltyPoints >= :minPoints")
    List<Customer> findByLoyaltyPointsGreaterThanEqual(Integer minPoints);
}
