package com.yashmerino.ecommerce.services;

import com.yashmerino.ecommerce.model.Order;
import com.yashmerino.ecommerce.model.Payment;
import com.yashmerino.ecommerce.model.User;
import com.yashmerino.ecommerce.model.dto.OrderDTO;
import com.yashmerino.ecommerce.model.dto.OrderWithPaymentDTO;
import com.yashmerino.ecommerce.model.dto.PaginatedDTO;
import com.yashmerino.ecommerce.repositories.OrderRepository;
import com.yashmerino.ecommerce.repositories.PaymentRepository;
import com.yashmerino.ecommerce.services.interfaces.UserService;
import com.yashmerino.ecommerce.utils.OrderStatus;
import com.yashmerino.ecommerce.utils.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private UserService userService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderRepository, paymentRepository, userService);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testPlaceOrder() {
        String username = "testuser";
        User user = new User();
        user.setId(1L);
        user.setUsername(username);

        Order order = new Order();
        order.setId(10L);
        order.setTotalAmount(BigDecimal.valueOf(99.99));
        order.setUser(user);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(username);
        when(userService.getByUsername(username)).thenReturn(user);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setTotalAmount(BigDecimal.valueOf(99.99));

        Long orderId = orderService.placeOrder(orderDTO);

        assertEquals(10L, orderId);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void testGetUserOrders() {
        String username = "testuser";
        User user = new User();
        user.setId(1L);
        user.setUsername(username);

        Order order1 = new Order();
        order1.setId(10L);
        order1.setTotalAmount(BigDecimal.valueOf(99.99));
        order1.setStatus(OrderStatus.PAYMENT_PENDING);
        order1.setCreatedAt(LocalDateTime.now());
        order1.setUser(user);

        Order order2 = new Order();
        order2.setId(11L);
        order2.setTotalAmount(BigDecimal.valueOf(49.99));
        order2.setStatus(OrderStatus.PAID);
        order2.setCreatedAt(LocalDateTime.now());
        order2.setUser(user);

        List<Order> orders = Arrays.asList(order1, order2);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(orders, pageable, orders.size());

        Payment payment1 = new Payment();
        payment1.setId(100L);
        payment1.setAmount(BigDecimal.valueOf(99.99));
        payment1.setStatus(PaymentStatus.PENDING);
        payment1.setCreatedAt(LocalDateTime.now());
        payment1.setOrder(order1);

        Payment payment2 = new Payment();
        payment2.setId(101L);
        payment2.setAmount(BigDecimal.valueOf(49.99));
        payment2.setStatus(PaymentStatus.SUCCEEDED);
        payment2.setCreatedAt(LocalDateTime.now());
        payment2.setOrder(order2);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(username);
        when(userService.getByUsername(username)).thenReturn(user);
        when(orderRepository.findByUserIdOrderByCreatedAtDesc(eq(1L), any(Pageable.class))).thenReturn(orderPage);
        when(paymentRepository.findFirstByOrderIdOrderByCreatedAtDesc(10L)).thenReturn(payment1);
        when(paymentRepository.findFirstByOrderIdOrderByCreatedAtDesc(11L)).thenReturn(payment2);

        PaginatedDTO<OrderWithPaymentDTO> result = orderService.getUserOrders(0, 10);

        assertNotNull(result);
        assertEquals(2, result.getData().size());
        assertEquals(2, result.getTotalItems());

        OrderWithPaymentDTO dto1 = result.getData().get(0);
        assertEquals(10L, dto1.getOrderId());
        assertEquals(100L, dto1.getPaymentId());
        assertEquals(PaymentStatus.PENDING, dto1.getPaymentStatus());

        OrderWithPaymentDTO dto2 = result.getData().get(1);
        assertEquals(11L, dto2.getOrderId());
        assertEquals(101L, dto2.getPaymentId());
        assertEquals(PaymentStatus.SUCCEEDED, dto2.getPaymentStatus());
    }

    @Test
    void testGetUserOrdersWithNoPayment() {
        String username = "testuser";
        User user = new User();
        user.setId(1L);
        user.setUsername(username);

        Order order = new Order();
        order.setId(10L);
        order.setTotalAmount(BigDecimal.valueOf(99.99));
        order.setStatus(OrderStatus.PAYMENT_PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUser(user);

        List<Order> orders = Arrays.asList(order);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> orderPage = new PageImpl<>(orders, pageable, orders.size());

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(username);
        when(userService.getByUsername(username)).thenReturn(user);
        when(orderRepository.findByUserIdOrderByCreatedAtDesc(eq(1L), any(Pageable.class))).thenReturn(orderPage);
        when(paymentRepository.findFirstByOrderIdOrderByCreatedAtDesc(10L)).thenReturn(null);

        PaginatedDTO<OrderWithPaymentDTO> result = orderService.getUserOrders(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getData().size());

        OrderWithPaymentDTO dto = result.getData().get(0);
        assertEquals(10L, dto.getOrderId());
        assertNull(dto.getPaymentId());
        assertNull(dto.getPaymentStatus());
    }
}
