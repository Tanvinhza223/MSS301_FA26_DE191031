package com.mss301.brand.controller;

import com.mss301.brand.service.BrandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/brands")
public class InternalBrandController {
    private final BrandService brands;
    public InternalBrandController(BrandService brands) { this.brands = brands; }
    @PutMapping("/{brandId}/watches/{watchId}")
    ResponseEntity<Void> assign(@PathVariable Integer brandId, @PathVariable Integer watchId) {
        brands.assign(brandId, watchId); return ResponseEntity.ok().build();
    }
    @DeleteMapping("/watches/{watchId}")
    ResponseEntity<Void> unassign(@PathVariable Integer watchId) {
        brands.unassign(watchId); return ResponseEntity.ok().build();
    }
}

