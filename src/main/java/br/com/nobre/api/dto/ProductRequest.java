package br.com.nobre.api.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public record ProductRequest(@NotBlank @Size(max=160) String slug, @NotBlank @Size(max=180) String name,
 @NotBlank @Size(max=80) String category, @NotBlank @Size(max=100) String categoryLabel,
 @NotNull @DecimalMin("0.01") BigDecimal price, @DecimalMin("0.01") BigDecimal oldPrice,
 @Size(max=1000) String img, @Size(max=4000) String desc, @NotEmpty List<@NotBlank @Size(max=20) String> sizes) {}
