package com.mss301.brand.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "WatchBrands")
public class WatchBrand {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "BrandID")
    private Integer brandId;
    @Column(name = "BrandName", nullable = false, length = 200)
    private String brandName;
    @Column(name = "CountryOfOrigin", nullable = false, length = 50)
    private String countryOfOrigin;
    protected WatchBrand() {}
    public WatchBrand(String brandName, String countryOfOrigin) { this.brandName = brandName; this.countryOfOrigin = countryOfOrigin; }
    public Integer getBrandId() { return brandId; }
    public String getBrandName() { return brandName; }
    public String getCountryOfOrigin() { return countryOfOrigin; }
}

