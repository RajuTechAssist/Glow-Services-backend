
package com.glowservices.glow_services_backend.controller;

import com.glowservices.glow_services_backend.model.entity.ServiceEntity;
import com.glowservices.glow_services_backend.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/services")
@CrossOrigin(origins = {"http://localhost:5173", "https://glow-services-frontend.vercel.app/"}, allowCredentials = "true")
public class ServiceAdminController {

    @Autowired
    private ServiceService serviceService;

    @GetMapping
    public ResponseEntity<List<ServiceEntity>> getAllServicesAdmin() {
        // This method can return all services, including inactive ones if needed
        List<ServiceEntity> services = serviceService.getAllActiveServices();
        return ResponseEntity.ok(services);
    }

    @PostMapping
    public ResponseEntity<ServiceEntity> createService(@RequestBody ServiceEntity service) {
        ServiceEntity createdService = serviceService.createService(service);
        return ResponseEntity.ok(createdService);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceEntity> updateService(@PathVariable Long id, @RequestBody ServiceEntity serviceDetails) {
        Optional<ServiceEntity> updatedService = serviceService.updateService(id, serviceDetails);
        return updatedService.map(ResponseEntity::ok)
                             .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        if (serviceService.deleteService(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}