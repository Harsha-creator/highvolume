package com.example.highvolume.controller;


import com.example.highvolume.entity.OrderRequest;
import com.example.highvolume.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping //Endpoint to Create new Orders
    public ResponseEntity<String> createOrder(@RequestBody OrderRequest orderRequest) {
        CompletableFuture<String> responseFuture = orderService.takeOrder(orderRequest);
        try {
            return ResponseEntity.ok(responseFuture.get());
        } catch (InterruptedException | ExecutionException e) {
            return ResponseEntity.status(500).body("Order not processed");
        }
    }
}