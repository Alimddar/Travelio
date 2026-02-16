package org.example.travelio.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.travelio.Enums.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@Table(name = "journeys")
public class Journey {

    public static final long MAX_STEPS = 4;
    public static final long MAX_INTERESTS = 3;
    public static final long MIN_INTERESTS = 1;
    public static final long MAX_TRIP_DAYS = 30;
    public static final long MIN_TRIP_DAYS = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JourneyStatus status = JourneyStatus.START;

    @Column(name = "current_step", nullable = false)
    private Long currentStep = 1L;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "travel_style")
    private TravelStyle travelStyle;

    @Enumerated(EnumType.STRING)
    @Column(name = "travel_with")
    private TravelWith travelWith;

    @Column(name = "adults_count")
    private Long adultsCount;

    @Column(name = "children_count")
    private Long childrenCount;

    @ElementCollection
    @CollectionTable(name = "journey_interests", joinColumns = @JoinColumn(name = "journey_id"))
    @Column(name = "interest")
    private List<String> interests = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "budget_type")
    private BudgetType budgetType;

    @Column(name = "trip_days")
    private Long tripDays;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type")
    private RequestType requestType;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "user_whatsapp")
    private String userWhatsapp;

    @Column(name = "selected_guide_id")
    private Long selectedGuideId;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_status")
    private RequestStatus requestStatus;

    public Journey() {
        this.createdAt = LocalDateTime.now();
    }

    public boolean isCompleted() {
        return this.status == JourneyStatus.COMPLETED;
    }

    public boolean canProceedToNextStep() {
        return this.currentStep < MAX_STEPS;
    }
}
