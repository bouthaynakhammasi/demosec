package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.BabyProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BabyProfileRepository extends JpaRepository<BabyProfile, Long> {
    List<BabyProfile> findByParentId(Long parentId);
}
