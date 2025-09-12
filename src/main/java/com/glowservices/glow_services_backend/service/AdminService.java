package com.glowservices.glow_services_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.glowservices.glow_services_backend.model.entity.Admin;
import com.glowservices.glow_services_backend.repository.AdminRepository;

@Service
public class AdminService {
  @Autowired private AdminRepository repo;
  @Autowired private PasswordEncoder encoder;

  public Admin create(Admin admin) {
    admin.setPassword(encoder.encode(admin.getPassword()));
    return repo.save(admin);
  }

  public Admin update(Long id, Admin updated) {
    return repo.findById(id).map(admin -> {
      admin.setEmail(updated.getEmail());
      admin.setFullName(updated.getFullName());
      if (!updated.getPassword().isBlank()) {
        admin.setPassword(encoder.encode(updated.getPassword()));
      }
      admin.setActive(updated.isActive());
      return repo.save(admin);
    }).orElseThrow(() -> new RuntimeException("Not found"));
  }

  public void delete(Long id) {
    repo.deleteById(id);
  }

  public List<Admin> list() {
    return repo.findAll();
  }

  public Admin get(Long id) {
    return repo.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
  }
}