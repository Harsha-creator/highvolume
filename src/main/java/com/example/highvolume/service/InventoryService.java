package com.example.highvolume.service;

import com.example.highvolume.entity.*;
import com.example.highvolume.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class InventoryService {

    @Autowired
    private RedisTemplate<String, Integer> redisTemplate;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private WarehouseService warehouseService;

    public boolean checkInventory(OrderRequest orderRequest) {
        for (OrderItem item : orderRequest.getItems()) {
            String cacheKey = "inventory:" + item.getProductId();
            Integer stockLevel = redisTemplate.opsForValue().get(cacheKey);

            if (stockLevel == null) {
                // Cache miss, fetch from MongoDB and update cache
                stockLevel = inventoryRepository.findByProductId(item.getProductId()).stream()
                        .mapToInt(Inventory::getQuantity)
                        .sum();
                redisTemplate.opsForValue().set(cacheKey, stockLevel);
            }

            if (stockLevel < item.getQuantity()) {
                return false;
            }
        }
        return true;
    }

    @Transactional
    public boolean reserveStock(OrderRequest orderRequest) {
        //Function to check the availability and reserve stock
        return warehouseService.findWarehousesWithStock(orderRequest);
    }

    @Transactional
    public void updateStock(InventoryUpdateRequest updateRequest) {
        //To updating Mongo Database and Clearing Redis
        Inventory inventory = inventoryRepository.findByProductIdAndWarehouseId(updateRequest.getProductId(), updateRequest.getWarehouseId());
        if (inventory != null) {
            inventory.setQuantity(updateRequest.getNewStockLevel());
            inventoryRepository.save(inventory);
        }

        // Delete related keys from Redis
        String inventoryPattern = "inventory:*" + updateRequest.getProductId();
        deleteKeysByPattern(inventoryPattern);
        String productInventoryPattern = "warehouse:*:" + updateRequest.getProductId();
        deleteKeysByPattern(productInventoryPattern);
    }

    private void deleteKeysByPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

}