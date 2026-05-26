package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.Notification;
import com.aziz.demosec.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipient_IdOrderByCreatedAtDesc(Long recipientId);

    List<Notification> findByRecipient_IdAndIsReadFalse(Long recipientId);

    long countByRecipient_IdAndIsReadFalse(Long recipientId);
    
    List<Notification> findByRecipientOrderByCreatedAtDesc(User recipient);
    
    long countByRecipientAndIsReadFalse(User recipient);
}
