package com.aziz.demosec.service;

import com.aziz.demosec.Entities.*;
import com.aziz.demosec.delivery.DeliveryGateway;
import com.aziz.demosec.domain.User;
import com.aziz.demosec.dto.pharmacy.OrderItemRequestDTO;
import com.aziz.demosec.dto.pharmacy.PharmacyOrderRequestDTO;
import com.aziz.demosec.dto.pharmacy.PharmacyOrderResponseDTO;
import com.aziz.demosec.dto.pharmacy.UpdateOrderStatusRequestDTO;
import com.aziz.demosec.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PharmacyOrderServiceTest {

    @Mock private PharmacyOrderRepository orderRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private OrderTrackingRepository trackingRepository;
    @Mock private PharmacyStockRepository stockRepository;
    @Mock private PharmacyRepository pharmacyRepository;
    @Mock private ProductRepository productRepository;
    @Mock private DeliveryRepository deliveryRepository;
    @Mock private NotificationRepository notificationRepository;
    @Mock private PaymentRepository paymentRepository;
    @Mock private DeliveryGateway deliveryGateway;
    @Mock private PharmacistRepository pharmacistRepository;
    @Mock private UserRepository userRepository;
    @Mock private WebSocketNotificationService webSocketNotificationService;

    @InjectMocks
    private PharmacyOrderServiceImpl pharmacyOrderService;

    private Pharmacy pharmacy;
    private User patient;
    private Product product;
    private PharmacyStock stock;
    private PharmacyOrderRequestDTO orderRequest;

    @BeforeEach
    void setUp() {
        pharmacy = Pharmacy.builder().id(1L).name("Test Pharmacy").build();
        patient = User.builder().id(2L).fullName("Test Patient").email("patient@test.com").build();
        product = new Product();
        product.setId(3L);
        product.setName("Aspirin");

        stock = new PharmacyStock();
        stock.setId(4L);
        stock.setPharmacy(pharmacy);
        stock.setProduct(product);
        stock.setTotalQuantity(100);
        stock.setUnitPrice(BigDecimal.valueOf(10.0));

        OrderItemRequestDTO itemRequest = new OrderItemRequestDTO();
        itemRequest.setProductId(3L);
        itemRequest.setQuantity(2);

        orderRequest = PharmacyOrderRequestDTO.builder()
                .patientId(2L)
                .pharmacyId(1L)
                .deliveryAddress("123 Street")
                .prescriptionImageUrl("http://image.url")
                .items(List.of(itemRequest))
                .deliveryType(DeliveryType.PICKUP)
                .build();
    }

    @Test
    void createOrder_ShouldSucceed_WhenStockIsAvailable() {
        // Arrange
        when(pharmacyRepository.findById(1L)).thenReturn(Optional.of(pharmacy));
        when(userRepository.findById(2L)).thenReturn(Optional.of(patient));
        when(stockRepository.findByPharmacy_IdAndProduct_Id(1L, 3L)).thenReturn(Optional.of(stock));
        
        PharmacyOrder savedOrder = new PharmacyOrder();
        savedOrder.setId(10L);
        savedOrder.setPatient(patient);
        savedOrder.setPharmacy(pharmacy);
        savedOrder.setItems(new ArrayList<>());
        when(orderRepository.save(any(PharmacyOrder.class))).thenReturn(savedOrder);
        when(orderRepository.findById(10L)).thenReturn(Optional.of(savedOrder));

        // Act
        PharmacyOrderResponseDTO response = pharmacyOrderService.createOrder(orderRequest);

        // Assert
        assertNotNull(response);
        assertEquals(10L, response.getId());
        verify(orderRepository, times(1)).save(any(PharmacyOrder.class));
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
    }

    @Test
    void createOrder_ShouldThrowException_WhenStockIsInsufficient() {
        // Arrange
        stock.setTotalQuantity(1); // Only 1 available, but 2 requested
        when(pharmacyRepository.findById(1L)).thenReturn(Optional.of(pharmacy));
        when(userRepository.findById(2L)).thenReturn(Optional.of(patient));
        when(stockRepository.findByPharmacy_IdAndProduct_Id(1L, 3L)).thenReturn(Optional.of(stock));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> pharmacyOrderService.createOrder(orderRequest));
    }

    @Test
    void updateStatus_ToValidated_ShouldDecrementStock() {
        // Arrange
        PharmacyOrder order = new PharmacyOrder();
        order.setId(10L);
        order.setPharmacy(pharmacy);
        order.setPatient(patient);
        order.setStatus(PharmacyOrderStatus.PENDING);
        
        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(5);
        order.setItems(List.of(item));

        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(stockRepository.findByPharmacy_IdAndProduct_Id(1L, 3L)).thenReturn(Optional.of(stock));
        when(orderRepository.save(any(PharmacyOrder.class))).thenReturn(order);

        UpdateOrderStatusRequestDTO updateReq = UpdateOrderStatusRequestDTO.builder()
                .status(PharmacyOrderStatus.VALIDATED)
                .changedBy("PHARMACIST")
                .note("Validating order")
                .build();

        // Act
        pharmacyOrderService.updateStatus(10L, updateReq);

        // Assert
        assertEquals(95, stock.getTotalQuantity()); // 100 - 5
        verify(stockRepository, times(1)).save(stock);
    }
}
