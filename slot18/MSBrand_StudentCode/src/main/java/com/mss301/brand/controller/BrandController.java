package com.mss301.brand.controller;

import com.mss301.brand.dto.*;
import com.mss301.brand.service.BrandService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/brands")
public class BrandController {
    private final BrandService brands;
    public BrandController(BrandService brands) { this.brands = brands; }
    @GetMapping public List<BrandResponse> findAll() { return brands.findAll(); }
    @GetMapping("/{id}") public BrandResponse findById(@PathVariable Integer id) { return brands.findById(id); }
    @GetMapping("/{id}/watches") public BrandWatchesResponse watches(@PathVariable Integer id) { return brands.findWatches(id); }
}

