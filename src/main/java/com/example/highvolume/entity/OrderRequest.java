package com.example.highvolume.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Document(collection = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {
    @Id
    private String id;
    private String customerId;
    private List<OrderItem> items;
}