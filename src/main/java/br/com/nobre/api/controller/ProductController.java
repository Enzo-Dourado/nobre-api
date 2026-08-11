package br.com.nobre.api.controller;

import br.com.nobre.api.dto.ProductRequest;
import br.com.nobre.api.model.Product;
import br.com.nobre.api.repository.ProductRepository;
import br.com.nobre.api.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/products")
public class ProductController {
 private final ProductRepository products; private final AuthService auth;
 public ProductController(ProductRepository products,AuthService auth){this.products=products;this.auth=auth;}
 @GetMapping public List<Product> list(@RequestParam(required=false) String category,@RequestParam(required=false,name="q") String query){
   var list=category==null?products.findAll():products.findByCategoryIgnoreCaseOrderByIdAsc(category);
   if(query!=null&&!query.isBlank()){var q=query.toLowerCase(Locale.ROOT);return list.stream().filter(p->p.name.toLowerCase(Locale.ROOT).contains(q)).toList();} return list;
 }
 @GetMapping("/{idOrSlug}") ResponseEntity<?> one(@PathVariable String idOrSlug){return find(idOrSlug).<ResponseEntity<?>>map(ResponseEntity::ok).orElseGet(()->ResponseEntity.notFound().build());}
 @PostMapping ResponseEntity<?> create(HttpServletRequest req,@Valid @RequestBody ProductRequest body){
   if(!admin(req))return forbidden(); if(products.findBySlug(body.slug()).isPresent())return conflict(); var p=apply(new Product(),body);return ResponseEntity.status(201).body(products.save(p));
 }
 @PutMapping("/{id}") ResponseEntity<?> update(HttpServletRequest req,@PathVariable Long id,@Valid @RequestBody ProductRequest body){
   if(!admin(req))return forbidden(); var p=products.findById(id).orElse(null);if(p==null)return ResponseEntity.notFound().build();if(products.existsBySlugAndIdNot(body.slug(),id))return conflict();return ResponseEntity.ok(products.save(apply(p,body)));
 }
 @DeleteMapping("/{id}") ResponseEntity<?> delete(HttpServletRequest req,@PathVariable Long id){if(!admin(req))return forbidden();if(!products.existsById(id))return ResponseEntity.notFound().build();products.deleteById(id);return ResponseEntity.noContent().build();}
 private Optional<Product> find(String v){try{return products.findById(Long.valueOf(v));}catch(NumberFormatException e){return products.findBySlug(v);}}
 private boolean admin(HttpServletRequest r){return auth.currentUser(r).map(u->u.role==br.com.nobre.api.model.User.Role.ADMIN).orElse(false);}
 private ResponseEntity<?> forbidden(){return ResponseEntity.status(403).body(Map.of("error","Acesso exclusivo de administrador."));}
 private ResponseEntity<?> conflict(){return ResponseEntity.status(409).body(Map.of("error","Já existe um produto com este slug."));}
 private Product apply(Product p,ProductRequest b){p.slug=b.slug().trim();p.name=b.name().trim();p.category=b.category().trim();p.categoryLabel=b.categoryLabel().trim();p.price=b.price();p.oldPrice=b.oldPrice();p.img=b.img();p.desc=b.desc();p.sizes=new ArrayList<>(b.sizes());return p;}
}
