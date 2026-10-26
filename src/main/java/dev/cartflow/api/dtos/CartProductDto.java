package dev.cartflow.api.dtos;
import java.math.BigDecimal;
public record CartProductDto(Long id, String name, BigDecimal price) {}
