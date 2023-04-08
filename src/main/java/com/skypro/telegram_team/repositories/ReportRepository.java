package com.skypro.telegram_team.repositories;

import com.skypro.telegram_team.models.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByAnimalId(long id);
}
