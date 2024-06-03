package com.example.highvolume.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "warehouses")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Warehouse {
    @Id
    private String id;
    private String location;
    private int wDistance;
}