package br.com.nobre.api.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name="orders")
public class CustomerOrder {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @ManyToOne(optional=false, fetch=FetchType.LAZY) @JoinColumn(name="user_id") public User user;
    @Column(nullable=false, precision=10, scale=2) public BigDecimal total;
    @Column(nullable=false) public String status = "pago";
    @Column(name="payment_method", nullable=false) public String paymentMethod;
    @Column(name="shipping_address", nullable=false, columnDefinition="text") public String shippingAddress;
    @Column(name="created_at", nullable=false) public Instant createdAt = Instant.now();
    @OneToMany(mappedBy="order", cascade=CascadeType.ALL, orphanRemoval=true) public List<OrderItem> items = new ArrayList<>();
}
