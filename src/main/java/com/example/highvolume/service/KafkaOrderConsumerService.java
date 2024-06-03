package com.example.highvolume.service;

import com.example.highvolume.entity.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaOrderConsumerService {

    @Autowired
    private OrderService orderService;

    @KafkaListener(topics = "orders", groupId = "order-consumers")
    public void listenOrderRequest(OrderRequest orderRequest) {
        try {
            orderService.processOrder(orderRequest);
            System.out.println("Order processed successfully for customer: " + orderRequest.getCustomerId());
        } catch (Exception e) {
            System.err.println("Failed to process order for customer: " + orderRequest.getCustomerId());
        }
    }
}
