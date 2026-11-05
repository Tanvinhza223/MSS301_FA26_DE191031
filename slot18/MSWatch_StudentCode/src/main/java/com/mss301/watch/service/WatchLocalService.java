package com.mss301.watch.service;

import com.mss301.watch.dto.WatchRequest;
import com.mss301.watch.entity.*;
import com.mss301.watch.repository.*;
import com.mss301.watch.saga.WatchData;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class WatchLocalService {
    private final WatchRepository watches;
    private final WatchCategoryRepository categories;
    public WatchLocalService(WatchRepository watches, WatchCategoryRepository categories) { this.watches = watches; this.categories = categories; }

    @Transactional public Watch create(WatchRequest request) {
        Watch watch = new Watch(request.name(), category(request.categoryId()), request.brandId(), request.caseMaterial(),
            request.strapMaterial(), request.waterResistance(), request.price(), request.releaseDate(), request.stock());
        return watches.saveAndFlush(watch);
    }
    @Transactional(readOnly = true) public WatchData snapshot(Integer id) { return WatchData.from(require(id)); }
    @Transactional public Watch update(Integer id, WatchRequest request) {
        Watch watch = require(id);
        watch.apply(request.name(), category(request.categoryId()), request.brandId(), request.caseMaterial(), request.strapMaterial(),
            request.waterResistance(), request.price(), request.releaseDate(), request.stock());
        return watches.saveAndFlush(watch);
    }
    @Transactional public void restore(WatchData data) {
        Watch watch = require(data.watchId());
        watch.apply(data.name(), category(data.categoryId()), data.brandId(), data.caseMaterial(), data.strapMaterial(),
            data.waterResistance(), data.price(), data.releaseDate(), data.stock());
        watches.saveAndFlush(watch);
    }
    @Transactional public void delete(Integer id) { watches.delete(require(id)); watches.flush(); }
    @Transactional public void deleteIfExists(Integer id) { if (watches.existsById(id)) { watches.deleteById(id); watches.flush(); } }
    private Watch require(Integer id) { return watches.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Watch not found with id: " + id)); }
    private WatchCategory category(Integer id) { return categories.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Category not found with id: " + id)); }
}

