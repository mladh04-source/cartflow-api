package dev.cartflow.api.dtos;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public record ProductRequest(
        @NotBlank String name,
        @NotBlank String description,
        @NotNull @DecimalMin("0.01") BigDecimal price) {}
