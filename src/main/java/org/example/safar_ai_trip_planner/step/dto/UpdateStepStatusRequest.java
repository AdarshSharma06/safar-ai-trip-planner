package org.example.safar_ai_trip_planner.step.dto;

import lombok.Getter;
import org.example.safar_ai_trip_planner.step.StepStatus;

@Getter
public class UpdateStepStatusRequest {
    private StepStatus status;
}
