package org.example.travelio.Entities;

import jakarta.persistence.*;
import org.example.travelio.Enums.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
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

    // Step 1 - Travel Style
    @Enumerated(EnumType.STRING)
    @Column(name = "travel_style")
    private TravelStyle travelStyle;

    // Step 2 - Traveling With
    @Enumerated(EnumType.STRING)
    @Column(name = "travel_with")
    private TravelWith travelWith;

    @Column(name = "adults_count")
    private Long adultsCount;

    @Column(name = "children_count")
    private Long childrenCount;

    // Step 3 - Interests (stored as comma-separated or use @ElementCollection)
    @ElementCollection
    @CollectionTable(name = "journey_interests", joinColumns = @JoinColumn(name = "journey_id"))
    @Column(name = "interest")
    private List<String> interests = new ArrayList<>();

    // Step 4 - Trip Details
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

     // Constructors
    public Journey() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public JourneyStatus getStatus() {
        return status;
    }

    public void setStatus(JourneyStatus status) {
        this.status = status;
    }

    public Long getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(Long currentStep) {
        this.currentStep = currentStep;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public TravelStyle getTravelStyle() {
        return travelStyle;
    }

    public void setTravelStyle(TravelStyle travelStyle) {
        this.travelStyle = travelStyle;
    }

    public TravelWith getTravelWith() {
        return travelWith;
    }

    public void setTravelWith(TravelWith travelWith) {
        this.travelWith = travelWith;
    }

    public Long getAdultsCount() {
        return adultsCount;
    }

    public void setAdultsCount(Long adultsCount) {
        this.adultsCount = adultsCount;
    }

    public Long getChildrenCount() {
        return childrenCount;
    }

    public void setChildrenCount(Long childrenCount) {
        this.childrenCount = childrenCount;
    }

    public List<String> getInterests() {
        return interests;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public BudgetType getBudgetType() {
        return budgetType;
    }

    public void setBudgetType(BudgetType budgetType) {
        this.budgetType = budgetType;
    }

    public Long getTripDays() {
        return tripDays;
    }

    public void setTripDays(Long tripDays) {
        this.tripDays = tripDays;
    }

    public Long getSelectedGuideId() {
        return selectedGuideId;
    }

    public void setSelectedGuideId(Long selectedGuideId) {
        this.selectedGuideId = selectedGuideId;
    }

    public RequestStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserWhatsapp() {
        return userWhatsapp;
    }

    public void setUserWhatsapp(String userWhatsapp) {
        this.userWhatsapp = userWhatsapp;
    }

    // Helper methods
    public boolean isCompleted() {
        return this.status == JourneyStatus.COMPLETED;
    }

    public boolean canProceedToNextStep() {
        return this.currentStep < MAX_STEPS;
    }
}