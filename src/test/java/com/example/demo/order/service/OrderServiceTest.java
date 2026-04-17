package com.example.demo.order.service;

import com.example.demo.entity.Order;
import com.example.demo.order.dto.OrderRequestDto;
import com.example.demo.order.dto.OrderResponseDto;
import com.example.demo.order.exception.ResourceNotFoundException;
import com.example.demo.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    // --- create ---

    @Test
    void create_savesAndReturnsOrder() {
        OrderRequestDto request = new OrderRequestDto("Widget", new BigDecimal("9.99"));
        Order saved = Order.builder().id(1L).productName("Widget").price(new BigDecimal("9.99")).build();
        when(orderRepository.save(any(Order.class))).thenReturn(saved);

        OrderResponseDto result = orderService.create(request);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.productName()).isEqualTo("Widget");
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void create_throwsWhenProductNameIsNull() {
        OrderRequestDto request = new OrderRequestDto(null, new BigDecimal("9.99"));

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("productName");

        verify(orderRepository, never()).save(any());
    }

    @Test
    void create_throwsWhenProductNameIsBlank() {
        OrderRequestDto request = new OrderRequestDto("  ", new BigDecimal("9.99"));

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("productName");

        verify(orderRepository, never()).save(any());
    }

    @Test
    void create_throwsWhenPriceIsNull() {
        OrderRequestDto request = new OrderRequestDto("Widget", null);

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("price");

        verify(orderRepository, never()).save(any());
    }

    @Test
    void create_throwsWhenPriceIsZero() {
        OrderRequestDto request = new OrderRequestDto("Widget", BigDecimal.ZERO);

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("price");

        verify(orderRepository, never()).save(any());
    }

    @Test
    void create_throwsWhenPriceIsNegative() {
        OrderRequestDto request = new OrderRequestDto("Widget", new BigDecimal("-1.00"));

        assertThatThrownBy(() -> orderService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("price");

        verify(orderRepository, never()).save(any());
    }

    // --- getAll ---

    @Test
    void getAll_returnsAllOrders() {
        List<Order> orders = List.of(
                Order.builder().id(1L).productName("A").price(BigDecimal.ONE).build(),
                Order.builder().id(2L).productName("B").price(BigDecimal.TEN).build()
        );
        when(orderRepository.findAll()).thenReturn(orders);

        assertThat(orderService.getAll()).hasSize(2);
        verify(orderRepository).findAll();
    }

    // --- getById ---

    @Test
    void getById_returnsOrder_whenFound() {
        Order order = Order.builder().id(1L).productName("Widget").price(new BigDecimal("9.99")).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThat(orderService.getById(1L).productName()).isEqualTo("Widget");
    }

    @Test
    void getById_throwsResourceNotFoundException_whenNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // --- update ---

    @Test
    void update_updatesAndReturnsOrder() {
        Order existing = Order.builder().id(1L).productName("Old").price(new BigDecimal("1.00")).build();
        OrderRequestDto request = new OrderRequestDto("New", new BigDecimal("2.00"));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(orderRepository.save(existing)).thenReturn(existing);

        OrderResponseDto result = orderService.update(1L, request);

        assertThat(result.productName()).isEqualTo("New");
        assertThat(result.price()).isEqualByComparingTo("2.00");
    }

    @Test
    void update_throwsResourceNotFoundException_whenNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.update(99L,
                new OrderRequestDto("X", BigDecimal.ONE)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // --- delete ---

    @Test
    void delete_deletesOrder_whenFound() {
        Order order = Order.builder().id(1L).productName("Widget").price(new BigDecimal("9.99")).build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.delete(1L);

        verify(orderRepository).delete(order);
    }

    @Test
    void delete_throwsResourceNotFoundException_whenNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
