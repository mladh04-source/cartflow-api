package dev.cartflow.api.dtos;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
public record CartDto(UUID id, List<CartItemDto> items, BigDecimal totalPrice) {}
