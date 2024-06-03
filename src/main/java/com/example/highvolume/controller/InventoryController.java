package com.example.highvolume.controller;

import com.example.highvolume.entity.InventoryUpdateRequest;
import com.example.highvolume.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @PostMapping("/update") //Dummy controller to update stock values
    public ResponseEntity<String> updateInventory(@RequestBody InventoryUpdateRequest updateRequest) {
        inventoryService.updateStock(updateRequest);
        return ResponseEntity.ok("Stock updated");
    }
}