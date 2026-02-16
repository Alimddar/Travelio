package org.example.travelio.Services;

import org.example.travelio.DTO.Request.*;
import org.example.travelio.DTO.Response.JourneyResponse;
import org.example.travelio.Entities.Journey;
import org.example.travelio.Enums.JourneyStatus;
import org.example.travelio.Enums.RequestStatus;
import org.example.travelio.Enums.RequestType;
import org.example.travelio.Enums.TravelWith;
import org.example.travelio.Exceptions.InvalidStepException;
import org.example.travelio.Exceptions.JourneyAlreadyCompletedException;
import org.example.travelio.Exceptions.JourneyNotFoundException;
import org.example.travelio.Repositories.JourneyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class JourneyService {

    private final JourneyRepository journeyRepository;

    public JourneyService(JourneyRepository journeyRepository) {
        this.journeyRepository = journeyRepository;
    }

    /**
     * Creates a new journey with initial status START and currentStep 1
     */
    public JourneyResponse createJourney() {
        Journey journey = new Journey();
        journeyRepository.save(journey);
        return JourneyResponse.fromEntity(journey);
    }

    /**
     * Step 1: Travel Style (ACTIVE / PASSIVE)
     */
    public JourneyResponse completeStep1(Step1Request request) {
        Journey journey = getJourneyOrThrow(request.getJourneyId());

        validateStep(journey, 1);

        journey.setTravelStyle(request.getTravelStyle());
        journey.setStatus(JourneyStatus.IN_PROGRESS);
        journey.setCurrentStep(2L);
        journey.setUpdatedAt(LocalDateTime.now());

        journeyRepository.save(journey);
        return JourneyResponse.fromEntity(journey);
    }

    /**
     * Step 2: Traveling With (SOLO / COUPLE / FAMILY / FRIENDS)
     * + adults_count, children_count (only for FAMILY)
     */
    public JourneyResponse completeStep2(Step2Request request) {
        Journey journey = getJourneyOrThrow(request.getJourneyId());

        validateStep(journey, 2);

        // Validate children_count is provided only for FAMILY
        if (request.getTravelWith() == TravelWith.FAMILY) {
            if (request.getChildrenCount() == null || request.getChildrenCount() < 1) {
                throw new IllegalArgumentException("Children count is required for family travel and must be at least 1");
            }
        }

        journey.setTravelWith(request.getTravelWith());
        journey.setAdultsCount(request.getAdultsCount());

        // Only set children count for FAMILY
        if (request.getTravelWith() == TravelWith.FAMILY) {
            journey.setChildrenCount(request.getChildrenCount());
        } else {
            journey.setChildrenCount(null);
        }

        journey.setCurrentStep(3L);
        journey.setUpdatedAt(LocalDateTime.now());

        journeyRepository.save(journey);
        return JourneyResponse.fromEntity(journey);
    }

    /**
     * Step 3: Interests (1-3 selections)
     */
    public JourneyResponse completeStep3(Step3Request request) {
        Journey journey = getJourneyOrThrow(request.getJourneyId());

        validateStep(journey, 3);

        journey.setInterests(request.getInterests());
        journey.setCurrentStep(4L);
        journey.setUpdatedAt(LocalDateTime.now());

        journeyRepository.save(journey);
        return JourneyResponse.fromEntity(journey);
    }

    /**
     * Step 4: Trip Details (budget_type, trip_days)
     * Marks a journey as COMPLETED
     */
    public JourneyResponse completeStep4(Step4Request request) {
        Journey journey = getJourneyOrThrow(request.getJourneyId());

        validateStep(journey, 4);

        journey.setBudgetType(request.getBudgetType());
        journey.setTripDays(request.getTripDays());
        journey.setStatus(JourneyStatus.COMPLETED);
        journey.setUpdatedAt(LocalDateTime.now());

        journeyRepository.save(journey);
        return JourneyResponse.fromEntity(journey);
    }

    /**
     * Get journey by ID
     */
    public JourneyResponse getJourney(Long id) {
        Journey journey = getJourneyOrThrow(id);
        return JourneyResponse.fromEntity(journey);
    }

    @Transactional
    public void handleGuideRequest(JourneyRequest request) {
        Journey journey = getJourneyOrThrow(request.getJourneyId());
        updateJourney(journey, request, RequestType.GUIDE);
    }

    @Transactional
    public void handleAirportPickupRequest(JourneyRequest request) {
        Journey journey = getJourneyOrThrow(request.getJourneyId());
        updateJourney(journey, request, RequestType.AIRPORT_PICKUP);
    }

    // ============ Helper Methods ============

    private void updateJourney(Journey journey, JourneyRequest request, RequestType requestType) {
        journey.setRequestType(requestType);
        journey.setUserEmail(request.getUserEmail());
        journey.setUserWhatsapp(request.getUserWhatsapp());
        journey.setRequestStatus(RequestStatus.REQUESTED);
        journey.setUpdatedAt(LocalDateTime.now());

        if (requestType == RequestType.GUIDE) {
            journey.setSelectedGuideId(request.getSelectedGuideId());
        } else {
            journey.setSelectedGuideId(null);
        }

        journeyRepository.save(journey);
    }

    private Journey getJourneyOrThrow(Long id) {
        return journeyRepository.findById(id)
                .orElseThrow(() -> new JourneyNotFoundException(id));
    }

    private void validateStep(Journey journey, long expectedStep) {
        if (journey.isCompleted()) {
            throw new JourneyAlreadyCompletedException(journey.getId());
        }

        if (!journey.getCurrentStep().equals(expectedStep)) {
            throw new InvalidStepException(expectedStep, journey.getCurrentStep());
        }
    }
}
