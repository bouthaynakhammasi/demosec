package com.aziz.demosec.delivery;

/**
 * Abstraction over any external delivery agency.
 * Switch implementations by environment profile without touching business logic.
 */
public interface DeliveryGateway {

    /**
     * Request a delivery from the external agency.
     * @return DeliveryResponse containing the tracking id and url.
     */
    DeliveryResponse createDelivery(DeliveryRequest request);
}
