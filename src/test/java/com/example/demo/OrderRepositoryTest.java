package com.example.demo;

import com.example.demo.entity.Order;
import com.example.demo.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@Transactional
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void save_assignsGeneratedId() {
        Order saved = orderRepository.save(
                Order.builder().productName("Widget").price(new BigDecimal("9.99")).build());

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getProductName()).isEqualTo("Widget");
        assertThat(saved.getPrice()).isEqualByComparingTo("9.99");
    }

    @Test
    void findById_returnsPersistedOrder() {
        Order saved = orderRepository.save(
                Order.builder().productName("Gadget").price(new BigDecimal("49.99")).build());

        assertThat(orderRepository.findById(saved.getId())).isPresent();
    }

    @Test
    void update_persistsNewValues() {
        Order saved = orderRepository.save(
                Order.builder().productName("Old").price(new BigDecimal("1.00")).build());

        saved.setPrice(new BigDecimal("2.00"));
        orderRepository.save(saved);
        orderRepository.flush();

        assertThat(orderRepository.findById(saved.getId()).get().getPrice())
                .isEqualByComparingTo("2.00");
    }

    @Test
    void delete_removesOrder() {
        Order saved = orderRepository.save(
                Order.builder().productName("ToDelete").price(new BigDecimal("5.00")).build());

        orderRepository.delete(saved);
        orderRepository.flush();

        assertThat(orderRepository.findById(saved.getId())).isEmpty();
    }

    @Test
    void findById_returnsEmpty_whenNotFound() {
        assertThat(orderRepository.findById(Long.MAX_VALUE)).isEmpty();
    }

    @Test
    void findAll_returnsAllOrders() {
        orderRepository.save(Order.builder().productName("Alpha").price(new BigDecimal("1.00")).build());
        orderRepository.save(Order.builder().productName("Beta").price(new BigDecimal("2.00")).build());

        assertThat(orderRepository.findAll()).hasSizeGreaterThanOrEqualTo(2);
    }

}
