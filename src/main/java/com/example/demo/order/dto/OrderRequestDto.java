package com.example.demo.order.dto;

import java.math.BigDecimal;

public record OrderRequestDto(String productName, BigDecimal price) {}
