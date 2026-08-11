package br.com.nobre.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public final class OrderDtos {
 private OrderDtos() {}
 public record ItemRequest(@NotNull Long id, @NotBlank String size, @NotNull @Min(1) @Max(20) Integer qty) {}
 public record CreateRequest(@NotEmpty List<@Valid ItemRequest> items, @NotEmpty Map<String,String> shippingAddress,
                             @NotBlank @Pattern(regexp="card|pix") String paymentMethod) {}
 public record ItemResponse(Long productId, String productName, String size, Integer quantity, BigDecimal price) {}
 public record OrderResponse(Long id, BigDecimal total, String status, String paymentMethod, String shippingAddress,
                             Instant createdAt, List<ItemResponse> items) {}
}
