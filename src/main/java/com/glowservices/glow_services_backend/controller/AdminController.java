package com.glowservices.glow_services_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.glowservices.glow_services_backend.model.entity.Admin;
import com.glowservices.glow_services_backend.service.AdminService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api/admin")
public class AdminController {
  @Autowired private AdminService service;

  @GetMapping("/admins")
  public List<Admin> list() { return service.list(); }

  @GetMapping("/admins/{id}")
  public Admin get(@PathVariable Long id) { return service.get(id); }

  @PostMapping("/admins")
  public Admin create(@RequestBody Admin admin) { return service.create(admin); }

  @PutMapping("/admins/{id}")
  public Admin update(@PathVariable Long id, @RequestBody Admin admin) {
    return service.update(id, admin);
  }

  @DeleteMapping("/admins/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }
}
