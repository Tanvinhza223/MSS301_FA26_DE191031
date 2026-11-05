package com.mss301.watch.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "brand-service", url = "${brand-service.url}")
public interface BrandClient {
    @PutMapping("/internal/brands/{brandId}/watches/{watchId}") void assign(@PathVariable Integer brandId, @PathVariable Integer watchId);
    @DeleteMapping("/internal/brands/watches/{watchId}") void unassign(@PathVariable Integer watchId);
}

