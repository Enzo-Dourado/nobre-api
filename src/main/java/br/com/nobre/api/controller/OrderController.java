package br.com.nobre.api.controller;

import br.com.nobre.api.dto.OrderDtos.*;
import br.com.nobre.api.model.*;
import br.com.nobre.api.repository.*;
import br.com.nobre.api.service.AuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/orders")
public class OrderController {
 private final OrderRepository orders; private final ProductRepository products; private final AuthService auth; private final ObjectMapper json;
 public OrderController(OrderRepository orders,ProductRepository products,AuthService auth,ObjectMapper json){this.orders=orders;this.products=products;this.auth=auth;this.json=json;}
 @GetMapping ResponseEntity<?> list(HttpServletRequest req){var user=auth.currentUser(req).orElse(null);if(user==null)return unauthorized();return ResponseEntity.ok(orders.findByUserIdOrderByCreatedAtDesc(user.id).stream().map(this::response).toList());}
 @PostMapping @Transactional ResponseEntity<?> create(HttpServletRequest req,@Valid @RequestBody CreateRequest body) throws JsonProcessingException {
   var user=auth.currentUser(req).orElse(null);if(user==null)return unauthorized();
   if(body.shippingAddress().getOrDefault("street","").isBlank()||body.shippingAddress().getOrDefault("city","").isBlank())return ResponseEntity.badRequest().body(Map.of("error","Preencha o endereço de entrega."));
   var ids=body.items().stream().map(ItemRequest::id).distinct().toList();var found=products.findAllById(ids);if(found.size()!=ids.size())return ResponseEntity.badRequest().body(Map.of("error","Um ou mais produtos são inválidos."));
   var byId=new HashMap<Long,Product>();found.forEach(p->byId.put(p.id,p));var order=new CustomerOrder();order.user=user;order.paymentMethod=body.paymentMethod();order.shippingAddress=json.writeValueAsString(body.shippingAddress());order.total=BigDecimal.ZERO;
   for(var requested:body.items()){var product=byId.get(requested.id());if(!product.sizes.contains(requested.size()))return ResponseEntity.badRequest().body(Map.of("error","Tamanho inválido para "+product.name+"."));var item=new OrderItem();item.order=order;item.productId=product.id;item.productName=product.name;item.size=requested.size();item.quantity=requested.qty();item.price=product.price;order.items.add(item);order.total=order.total.add(product.price.multiply(BigDecimal.valueOf(requested.qty())));}
   orders.save(order);return ResponseEntity.status(201).body(response(order));
 }
 private OrderResponse response(CustomerOrder o){return new OrderResponse(o.id,o.total,o.status,o.paymentMethod,o.shippingAddress,o.createdAt,o.items.stream().map(i->new ItemResponse(i.productId,i.productName,i.size,i.quantity,i.price)).toList());}
 private ResponseEntity<?> unauthorized(){return ResponseEntity.status(401).body(Map.of("error","É necessário estar logado."));}
}
