package com.fudn.inventory_service.service;

import com.fudn.inventory_service.client.InventoryClient;
import com.fudn.inventory_service.dto.OrderRequest;
import com.fudn.inventory_service.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private InventoryClient inventoryClient;

    @InjectMocks
    private OrderService orderService;

    @Test
    void savesAnOrderWhenInventoryHasEnoughStock() {
        OrderRequest request = new OrderRequest(null, "iphone_15", new BigDecimal("1000"), 1);
        when(inventoryClient.isInStock("iphone_15", 1)).thenReturn(true);

        assertDoesNotThrow(() -> orderService.placeOrder(request));

        ArgumentCaptor<com.fudn.inventory_service.model.Order> savedOrder =
                ArgumentCaptor.forClass(com.fudn.inventory_service.model.Order.class);
        verify(orderRepository).save(savedOrder.capture());
        assertEquals("iphone_15", savedOrder.getValue().getSkuCode());
        verify(inventoryClient).isInStock(eq("iphone_15"), eq(1));
    }

    @Test
    void rejectsAnOrderWhenInventoryHasInsufficientStock() {
        OrderRequest request = new OrderRequest(null, "iphone_15", new BigDecimal("1000"), 101);
        when(inventoryClient.isInStock("iphone_15", 101)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.placeOrder(request));

        assertEquals("Product with Skucode iphone_15 is not in stock", exception.getMessage());
        verify(orderRepository, never()).save(any());
        verify(inventoryClient).isInStock(eq("iphone_15"), eq(101));
    }
}
