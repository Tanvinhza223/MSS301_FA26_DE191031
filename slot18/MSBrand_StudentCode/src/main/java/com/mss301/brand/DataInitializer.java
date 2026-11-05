package com.mss301.brand;

import com.mss301.brand.entity.*;
import com.mss301.brand.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    private final BrandRepository brands;
    private final BrandWatchRepository links;
    public DataInitializer(BrandRepository brands, BrandWatchRepository links) { this.brands = brands; this.links = links; }
    @Override public void run(String... args) {
        if (brands.count() == 0) brands.saveAll(List.of(
            new WatchBrand("Rolex", "Switzerland"), new WatchBrand("Omega", "Switzerland"),
            new WatchBrand("Seiko", "Japan"), new WatchBrand("Casio", "Japan"),
            new WatchBrand("TAG Heuer", "Switzerland")));
        if (links.count() == 0) links.saveAll(List.of(
            new BrandWatch(1, 1), new BrandWatch(2, 2), new BrandWatch(3, 3), new BrandWatch(4, 4),
            new BrandWatch(5, 5), new BrandWatch(2, 6), new BrandWatch(3, 7), new BrandWatch(1, 8),
            new BrandWatch(4, 9), new BrandWatch(4, 10)));
    }
}
