package com.aziz.demosec.Entities;

public enum DeliveryStatus {
    PENDING,        // Ready to be dispatched
    REQUESTED,      // Delivery requested from external agency
    ASSIGNED,       // Agent assigned to delivery
    PICKED_UP,      // Courier has collected the order
    IN_TRANSIT,     // On the way to the patient
    OUT_FOR_DELIVERY, // Final leg of delivery
    DELIVERED,      // Successfully delivered
    CANCELLED,      // Delivery cancelled
    FAILED          // Delivery failed (recipient not found, etc.)
}
