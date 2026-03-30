package com.aziz.demosec.repository;

import com.aziz.demosec.Entities.BabyProfile;
import com.aziz.demosec.Entities.Gender;
import com.aziz.demosec.Entities.Patient;
import com.aziz.demosec.Entities.Vaccination;
import com.aziz.demosec.domain.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test") // Use h2-test profile if needed, or defaults to H2 since it is on classpath
class VaccinationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VaccinationRepository vaccinationRepository;

    @Test
    @DisplayName("Should find vaccinations by baby ID")
    void findByBabyId_Success() {
        // Arrange
        Patient p = new Patient();
        p.setFullName("Test Parent");
        p.setEmail("test@email.com");
        p.setPassword("pass");
        p.setRole(Role.PATIENT);
        p = entityManager.persistFlushFind(p);

        BabyProfile b = BabyProfile.builder()
                .name("Test Baby")
                .birthDate(LocalDate.now())
                .gender(Gender.FEMALE)
                .parent(p)
                .build();
        b = entityManager.persistFlushFind(b);

        Vaccination v1 = Vaccination.builder()
                .baby(b)
                .vaccineName("BCG")
                .administeredDate(LocalDate.now())
                .build();
        entityManager.persistFlushFind(v1);

        Vaccination v2 = Vaccination.builder()
                .baby(b)
                .vaccineName("Polio")
                .administeredDate(LocalDate.now())
                .build();
        entityManager.persistFlushFind(v2);

        // Act
        List<Vaccination> results = vaccinationRepository.findByBabyId(b.getId());

        // Assert
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(v -> v.getVaccineName().equals("BCG")));
        assertTrue(results.stream().anyMatch(v -> v.getVaccineName().equals("Polio")));
    }

    @Test
    @DisplayName("Should find vaccinations by baby ID and vaccine name")
    void findByBabyIdAndVaccineName_Success() {
        // Arrange
        Patient p = new Patient();
        p.setFullName("Test Parent");
        p.setEmail("test@email.com");
        p.setPassword("pass");
        p.setRole(Role.PATIENT);
        p = entityManager.persistFlushFind(p);

        BabyProfile b = BabyProfile.builder()
                .name("Alex")
                .birthDate(LocalDate.now())
                .gender(Gender.MALE)
                .parent(p)
                .build();
        b = entityManager.persistFlushFind(b);

        Vaccination v = Vaccination.builder()
                .baby(b)
                .vaccineName("Hepatitis B")
                .administeredDate(LocalDate.now())
                .build();
        entityManager.persistFlushFind(v);

        // Act
        List<Vaccination> results = vaccinationRepository.findByBabyIdAndVaccineName(b.getId(), "Hepatitis B");

        // Assert
        assertFalse(results.isEmpty());
        assertEquals("Hepatitis B", results.get(0).getVaccineName());
    }
}
