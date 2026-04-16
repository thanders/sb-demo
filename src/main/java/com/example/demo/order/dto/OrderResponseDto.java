package com.example.demo.order.dto;

import java.math.BigDecimal;

public record OrderResponseDto(Long id, String productName, BigDecimal price) {}
