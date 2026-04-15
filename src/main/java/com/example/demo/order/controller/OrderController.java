package com.example.demo.order.controller;

import com.example.demo.entity.Order;
import com.example.demo.order.exception.ResourceNotFoundException;
import com.example.demo.order.repository.OrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @PostMapping
    public Order create(@RequestBody Order order) {
        return orderRepository.save(order);
    }

    @GetMapping
    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    @PutMapping("/{id}")
    public Order update(@PathVariable Long id, @RequestBody Order orderDetails) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + id));

        existingOrder.setProductName(orderDetails.getProductName());
        existingOrder.setPrice(orderDetails.getPrice());

        return orderRepository.save(existingOrder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + id));

        orderRepository.delete(existingOrder);
        return ResponseEntity.noContent().build();
    }
}