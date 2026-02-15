package org.example.safar_ai_trip_planner.step;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StepRepository extends JpaRepository<Step, Long> {
    List<Step> findByDayPlanIdOrderByStepOrder(Long dayPlanId);
    boolean existsByPlaceId(Long placeId);
}

