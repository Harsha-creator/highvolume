package com.example.highvolume.repository;

import com.example.highvolume.entity.Inventory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends MongoRepository<Inventory, String> {
    List<Inventory> findByWarehouseId(String warehouseId);
    List<Inventory> findByProductId(String productId);
    Inventory findByProductIdAndWarehouseId(String productId, String warehouseId);
}