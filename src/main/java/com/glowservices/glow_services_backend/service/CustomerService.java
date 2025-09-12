package com.glowservices.glow_services_backend.service;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.glowservices.glow_services_backend.model.entity.Customer;
import com.glowservices.glow_services_backend.repository.CustomerRepository;

@Service
public class CustomerService {
  @Autowired private CustomerRepository repo;

  public List<Customer> list() { return repo.findAll(); }
  public Customer get(Long id) { return repo.findById(id).orElseThrow(); }
  public Customer create(Customer c) { return repo.save(c); }
  public Customer update(Long id, Customer upd) {
    return repo.findById(id).map(c -> {
      BeanUtils.copyProperties(upd, c, "id", "createdAt");
      return repo.save(c);
    }).orElseThrow();
  }
  public void delete(Long id) { repo.deleteById(id); }
}
