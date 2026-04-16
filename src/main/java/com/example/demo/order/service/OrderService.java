package com.example.demo.order.service;

import com.example.demo.entity.Order;
import com.example.demo.order.dto.OrderRequestDto;
import com.example.demo.order.dto.OrderResponseDto;
import com.example.demo.order.exception.ResourceNotFoundException;
import com.example.demo.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public OrderResponseDto create(OrderRequestDto request) {
        validate(request);
        Order saved = orderRepository.save(toEntity(request));
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDto> getAll() {
        return orderRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public OrderResponseDto update(Long id, OrderRequestDto request) {
        validate(request);
        Order existing = findOrThrow(id);
        existing.setProductName(request.productName());
        existing.setPrice(request.price());
        return toResponse(orderRepository.save(existing));
    }

    @Transactional
    public void delete(Long id) {
        orderRepository.delete(findOrThrow(id));
    }

    private Order findOrThrow(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + id));
    }

    private void validate(OrderRequestDto request) {
        if (request.productName() == null || request.productName().isBlank()) {
            throw new IllegalArgumentException("productName must not be null or blank");
        }
        if (request.price() == null || request.price().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("price must not be null, zero, or negative");
        }
    }

    private Order toEntity(OrderRequestDto request) {
        return Order.builder()
                .productName(request.productName())
                .price(request.price())
                .build();
    }

    private OrderResponseDto toResponse(Order order) {
        return new OrderResponseDto(order.getId(), order.getProductName(), order.getPrice());
    }
}
