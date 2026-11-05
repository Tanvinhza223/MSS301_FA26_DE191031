package com.mss301.watch.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record WatchRequest(
    @NotBlank @Size(min = 11, max = 255) String name,
    @NotNull Integer categoryId,
    @NotNull Integer brandId,
    @NotBlank String caseMaterial,
    @NotBlank String strapMaterial,
    @NotBlank String waterResistance,
    @NotNull @DecimalMin("100.00") BigDecimal price,
    @NotNull LocalDate releaseDate,
    @NotNull @PositiveOrZero Integer stock) {}

