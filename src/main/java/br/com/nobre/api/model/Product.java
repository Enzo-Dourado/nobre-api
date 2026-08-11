package br.com.nobre.api.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name="products")
public class Product {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
    @Column(nullable=false, unique=true) public String slug;
    @Column(nullable=false) public String name;
    @Column(nullable=false) public String category;
    @Column(name="category_label", nullable=false) public String categoryLabel;
    @Column(nullable=false, precision=10, scale=2) public BigDecimal price;
    @Column(name="old_price", precision=10, scale=2) public BigDecimal oldPrice;
    @Column(name="image_url") public String img;
    @Column(name="description", columnDefinition="text") public String desc;
    @ElementCollection(fetch=FetchType.EAGER) @CollectionTable(name="product_sizes", joinColumns=@JoinColumn(name="product_id"))
    @Column(name="size", nullable=false) public List<String> sizes = new ArrayList<>();
}
