package com.glowservices.glow_services_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.glowservices.glow_services_backend.model.entity.Customer;
import com.glowservices.glow_services_backend.service.CustomerService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api/admin")
public class CustomerAdminController {
  @Autowired private CustomerService service;

  @GetMapping("/customers")
  public List<Customer> list() { return service.list(); }

  @GetMapping("/customers/{id}")
  public Customer get(@PathVariable Long id) { return service.get(id); }

  @PostMapping("/customers")
  public Customer create(@RequestBody Customer c) { return service.create(c); }

  @PutMapping("/customers/{id}")
  public Customer update(@PathVariable Long id, @RequestBody Customer c) {
    return service.update(id, c);
  }

  @DeleteMapping("/customers/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }
}
