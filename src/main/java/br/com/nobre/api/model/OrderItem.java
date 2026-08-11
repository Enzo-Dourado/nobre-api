package br.com.nobre.api.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity @Table(name="order_items")
public class OrderItem {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @ManyToOne(optional=false, fetch=FetchType.LAZY) @JoinColumn(name="order_id") public CustomerOrder order;
    @Column(name="product_id", nullable=false) public Long productId;
    @Column(name="product_name", nullable=false) public String productName;
    public String size;
    @Column(nullable=false) public Integer quantity;
    @Column(nullable=false, precision=10, scale=2) public BigDecimal price;
}
