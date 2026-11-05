package com.mss301.brand.service;

import com.mss301.brand.dto.*;
import com.mss301.brand.entity.*;
import com.mss301.brand.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class BrandService {
    private final BrandRepository brands;
    private final BrandWatchRepository links;
    public BrandService(BrandRepository brands, BrandWatchRepository links) { this.brands = brands; this.links = links; }

    @Transactional(readOnly = true)
    public List<BrandResponse> findAll() {
        return brands.findAll().stream().map(this::toResponse).toList();
    }
    @Transactional(readOnly = true)
    public BrandResponse findById(Integer brandId) { return toResponse(requireBrand(brandId)); }
    @Transactional(readOnly = true)
    public BrandWatchesResponse findWatches(Integer brandId) {
        WatchBrand brand = requireBrand(brandId);
        List<Integer> ids = links.findByBrandIdOrderByWatchIdAsc(brandId).stream().map(BrandWatch::getWatchId).toList();
        return new BrandWatchesResponse(brandId, brand.getBrandName(), ids.size(), ids);
    }
    @Transactional
    public void assign(Integer brandId, Integer watchId) {
        requireBrand(brandId);
        BrandWatch link = links.findByWatchId(watchId).orElseGet(() -> new BrandWatch(brandId, watchId));
        link.assignTo(brandId);
        links.save(link);
    }
    @Transactional
    public void unassign(Integer watchId) { links.findByWatchId(watchId).ifPresent(links::delete); }

    private WatchBrand requireBrand(Integer brandId) {
        return brands.findById(brandId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Brand not found with id: " + brandId));
    }
    private BrandResponse toResponse(WatchBrand brand) {
        return new BrandResponse(brand.getBrandId(), brand.getBrandName(), brand.getCountryOfOrigin(), links.countByBrandId(brand.getBrandId()));
    }
}

