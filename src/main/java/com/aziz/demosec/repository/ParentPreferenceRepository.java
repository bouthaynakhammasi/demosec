package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.ParentPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ParentPreferenceRepository extends JpaRepository<ParentPreference, Long> {
    List<ParentPreference> findByBabyProfileId(Long babyProfileId);
    void deleteByBabyProfileId(Long babyProfileId);
}
