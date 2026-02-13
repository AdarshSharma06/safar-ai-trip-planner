package org.example.safar_ai_trip_planner.dayplan;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DayPlanRepository extends JpaRepository<DayPlan, Long> {
    List<DayPlan> findByItineraryIdOrderByDayNumber(Long itineraryId);
}
