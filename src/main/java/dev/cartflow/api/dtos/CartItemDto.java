package dev.cartflow.api.dtos;
import java.math.BigDecimal;
public record CartItemDto(CartProductDto product, int quantity, BigDecimal totalPrice) {}
