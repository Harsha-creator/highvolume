package com.example.highvolume.service;

import com.example.highvolume.entity.OrderRequest;
import com.example.highvolume.expections.OutOfStockException;
import com.example.highvolume.expections.StockReservationException;
import com.example.highvolume.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class OrderService {

    @Autowired
    private KafkaTemplate<String, OrderRequest> kafkaTemplate;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private WarehouseService warehouseService;

    @Autowired
    private OrderRepository orderRepository;

    private final ConcurrentMap<String, CompletableFuture<String>> orderConfirmations = new ConcurrentHashMap<>();


    public CompletableFuture<String> takeOrder(OrderRequest orderRequest) {
        CompletableFuture<String> confirmationFuture = new CompletableFuture<>();
        orderConfirmations.put(orderRequest.getId(), confirmationFuture);
        kafkaTemplate.send("orders", orderRequest);
        return confirmationFuture;
    }

    public void processOrder(OrderRequest orderRequest) {
        try {
            if (!validateOrder(orderRequest)) {
                throw new IllegalArgumentException("Invalid order request");
            }

            boolean isAvailable = inventoryService.checkInventory(orderRequest);
            if (!isAvailable) {
                throw new OutOfStockException("Requested items are out of stock");
            }

            boolean isReserved = inventoryService.reserveStock(orderRequest);
            if (!isReserved) {
                throw new StockReservationException("Failed to reserve stock");
            }

            orderRepository.save(orderRequest);
            sendOrderConfirmation(orderRequest);
        } catch (Exception e) {
            sendOrderFailure(orderRequest, e);
        }
    }

    private boolean validateOrder(OrderRequest orderRequest) {
        return orderRequest != null && orderRequest.getItems() != null && !orderRequest.getItems().isEmpty();
    }

    private void sendOrderConfirmation(OrderRequest orderRequest) {
        CompletableFuture<String> confirmationFuture = orderConfirmations.remove(orderRequest.getId());
        if (confirmationFuture != null) {
            confirmationFuture.complete("Order received");
        }
        System.out.println("Order confirmed for: " + orderRequest.getCustomerId());
    }

    private void sendOrderFailure(OrderRequest orderRequest, Exception e) {
        CompletableFuture<String> confirmationFuture = orderConfirmations.remove(orderRequest.getId());
        if (confirmationFuture != null) {
            confirmationFuture.completeExceptionally(e);
        }
        System.err.println("Order failed for customer: " + orderRequest.getCustomerId() + " due to " + e.getMessage());
    }
}
