package com.mss301.watch.saga;

import com.mss301.watch.entity.Watch;
import java.math.BigDecimal;
import java.time.LocalDate;

public record WatchData(Integer watchId, String name, Integer categoryId, Integer brandId, String caseMaterial,
                        String strapMaterial, String waterResistance, BigDecimal price, LocalDate releaseDate, Integer stock) {
    public static WatchData from(Watch watch) {
        return new WatchData(watch.getWatchId(), watch.getName(), watch.getCategory().getCategoryId(), watch.getBrandId(),
            watch.getCaseMaterial(), watch.getStrapMaterial(), watch.getWaterResistance(), watch.getPrice(), watch.getReleaseDate(), watch.getStock());
    }
}

