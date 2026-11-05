package com.mss301.brand.repository;
import com.mss301.brand.entity.BrandWatch;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface BrandWatchRepository extends JpaRepository<BrandWatch, Long> {
    Optional<BrandWatch> findByWatchId(Integer watchId);
    List<BrandWatch> findByBrandIdOrderByWatchIdAsc(Integer brandId);
    long countByBrandId(Integer brandId);
}

