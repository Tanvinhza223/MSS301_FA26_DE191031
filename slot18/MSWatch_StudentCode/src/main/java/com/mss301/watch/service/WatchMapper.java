package com.mss301.watch.service;
import com.mss301.watch.dto.WatchResponse;
import com.mss301.watch.entity.Watch;
final class WatchMapper {
    private WatchMapper() {}
    static WatchResponse toResponse(Watch watch) {
        return new WatchResponse(watch.getWatchId(), watch.getName(), watch.getCategory().getCategoryId(),
            watch.getCategory().getCategoryName(), watch.getBrandId(), watch.getCaseMaterial(), watch.getStrapMaterial(),
            watch.getWaterResistance(), watch.getPrice(), watch.getReleaseDate(), watch.getStock());
    }
}

