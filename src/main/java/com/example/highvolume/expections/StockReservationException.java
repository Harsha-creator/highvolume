package com.example.highvolume.expections;


public class StockReservationException extends RuntimeException {
    public StockReservationException(String message) {
        super(message);
    }
}