package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.ServiceReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceReviewRepository extends JpaRepository<ServiceReview, Long> {
    Optional<ServiceReview> findByRequest_Id(Long requestId);
    List<ServiceReview> findByProvider_Id(Long providerId);
    List<ServiceReview> findByProvider_IdOrderByCreatedAtDesc(Long providerId);
}
