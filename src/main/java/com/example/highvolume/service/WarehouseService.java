package com.example.highvolume.service;

import com.example.highvolume.entity.OrderItem;
import com.example.highvolume.entity.OrderRequest;
import com.example.highvolume.entity.Warehouse;
import com.example.highvolume.entity.Inventory;
import com.example.highvolume.repository.InventoryRepository;
import com.example.highvolume.repository.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class WarehouseService {

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private RedisTemplate<String, Integer> redisTemplate;

    @Autowired
    private InventoryRepository inventoryRepository;

    public List<Warehouse> findWarehousesWithStock(OrderRequest orderRequest) {
        //Sort the Warehouse according to distance and select Warehouse from them
        List<Warehouse> selectedWarehouses = new ArrayList<>();
        List<Warehouse> warehouses = warehouseRepository.findAll();
        warehouses.sort(Comparator.comparingInt(Warehouse::getWDistance));

        for (OrderItem item : orderRequest.getItems()) {
            int remainingQuantity = item.getQuantity();

            for (Warehouse warehouse : warehouses) {
                String warehouseInventoryKey = "warehouse:" + warehouse.getId() + ":" + item.getProductId();
                Integer stockLevel = redisTemplate.opsForValue().get(warehouseInventoryKey);

                if (stockLevel == null) {
                    stockLevel = inventoryRepository.findByWarehouseId(warehouse.getId()).stream()
                            .filter(inv -> inv.getProductId().equals(item.getProductId()))
                            .mapToInt(Inventory::getQuantity)
                            .sum();
                    redisTemplate.opsForValue().set(warehouseInventoryKey, stockLevel);
                }

                if (stockLevel > 0) {
                    selectedWarehouses.add(warehouse);
                    remainingQuantity -= Math.min(stockLevel, remainingQuantity);
                }

                if (remainingQuantity <= 0) {
                    break;
                }
            }

            if (remainingQuantity > 0) {
                return null; // Not enough stock in total
            }
        }
        return selectedWarehouses;
    }
}
