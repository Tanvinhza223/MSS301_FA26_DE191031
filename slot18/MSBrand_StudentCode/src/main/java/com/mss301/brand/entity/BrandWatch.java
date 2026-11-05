package com.mss301.brand.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "BrandWatches", uniqueConstraints = @UniqueConstraint(name = "UK_BrandWatches_WatchID", columnNames = "WatchID"))
public class BrandWatch {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "BrandWatchID")
    private Long id;
    @Column(name = "BrandID", nullable = false)
    private Integer brandId;
    @Column(name = "WatchID", nullable = false)
    private Integer watchId;
    protected BrandWatch() {}
    public BrandWatch(Integer brandId, Integer watchId) { this.brandId = brandId; this.watchId = watchId; }
    public Integer getBrandId() { return brandId; }
    public Integer getWatchId() { return watchId; }
    public void assignTo(Integer brandId) { this.brandId = brandId; }
}

