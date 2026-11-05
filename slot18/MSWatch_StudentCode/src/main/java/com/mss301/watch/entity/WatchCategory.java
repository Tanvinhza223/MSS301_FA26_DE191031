package com.mss301.watch.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "WatchCategories")
public class WatchCategory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Column(name = "CategoryID")
    private Integer categoryId;
    @Column(name = "CategoryName", nullable = false, length = 100) private String categoryName;
    @Column(name = "Description", nullable = false, columnDefinition = "NVARCHAR(MAX)") private String description;
    @Column(name = "MovementType", nullable = false, length = 50) private String movementType;
    @Column(name = "PriceRange", nullable = false, length = 100) private String priceRange;
    protected WatchCategory() {}
    public WatchCategory(String categoryName, String description, String movementType, String priceRange) {
        this.categoryName = categoryName; this.description = description; this.movementType = movementType; this.priceRange = priceRange;
    }
    public Integer getCategoryId() { return categoryId; }
    public String getCategoryName() { return categoryName; }
}

