package com.aziz.demosec.service;

import com.aziz.demosec.Entities.PharmacyOrder;
import com.aziz.demosec.dto.pharmacy.PaymentRequestDTO;
import com.aziz.demosec.dto.pharmacy.PaymentResponseDTO;

public interface IPaymentService {
    PaymentResponseDTO initiatePayment(PaymentRequestDTO dto);
    PaymentResponseDTO createPaymentIntent(Long orderId);
    PaymentResponseDTO getByOrderId(Long orderId);
    PaymentResponseDTO getById(Long paymentId);
    PaymentResponseDTO verifyPaymentWithGateway(Long paymentId);
    void confirmOrderPaid(PharmacyOrder order);
}
