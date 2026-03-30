package com.aziz.demosec.repository;

import com.aziz.demosec.domain.User;
import com.aziz.demosec.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientEmailAndReadFalseOrderByCreatedAtDesc(String email);
    List<Notification> findByRecipientEmailOrderByCreatedAtDesc(String email);

    @Modifying
    @Transactional
    void deleteAllByRecipient(User recipient);
}
