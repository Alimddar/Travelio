package org.example.travelio.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.travelio.DTO.CustomErrorMessage;
import org.example.travelio.DTO.Request.*;
import org.example.travelio.DTO.Response.JourneyResponse;
import org.example.travelio.Exceptions.InvalidStepException;
import org.example.travelio.Exceptions.JourneyAlreadyCompletedException;
import org.example.travelio.Exceptions.JourneyNotFoundException;
import org.example.travelio.Services.JourneyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/V1")
@Tag(name = "Journey", description = "Journey onboarding management APIs")
public class JourneyController {

    private final JourneyService journeyService;

    public JourneyController(JourneyService journeyService) {
        this.journeyService = journeyService;
    }

    @Operation(
            summary = "Create a new journey",
            description = "Creates a new journey for user onboarding. Returns the journey with status 'START' and currentStep 1."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Journey created successfully",
                    content = @Content(schema = @Schema(implementation = JourneyResponse.class))
            )
    })
    @PostMapping("/journeys")
    public ResponseEntity<JourneyResponse> createJourney() {
        JourneyResponse response = journeyService.createJourney();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Complete Step 1 - Travel Style",
            description = "Set the travel style preference: ACTIVE or PASSIVE. Moves journey to step 2."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Step 1 completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid step or journey already completed"),
            @ApiResponse(responseCode = "404", description = "Journey not found")
    })
    @PutMapping("/journeys/step1")
    public ResponseEntity<?> completeStep1(@Valid @RequestBody Step1Request request) {
        return handleStepCompletion(() -> journeyService.completeStep1(request));
    }

    @Operation(
            summary = "Complete Step 2 - Traveling With",
            description = "Set who you're traveling with: SOLO, COUPLE, FAMILY, or FRIENDS. Include adults_count and children_count (required for FAMILY). Moves journey to step 3."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Step 2 completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid step, validation error, or journey already completed"),
            @ApiResponse(responseCode = "404", description = "Journey not found")
    })
    @PutMapping("/journeys/step2")
    public ResponseEntity<?> completeStep2(@Valid @RequestBody Step2Request request) {
        return handleStepCompletion(() -> journeyService.completeStep2(request));
    }

    @Operation(
            summary = "Complete Step 3 - Interests",
            description = "Select 1 to 3 interests for your trip. Moves journey to step 4."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Step 3 completed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid step, validation error, or journey already completed"),
            @ApiResponse(responseCode = "404", description = "Journey not found")
    })
    @PutMapping("/journeys/step3")
    public ResponseEntity<?> completeStep3(@Valid @RequestBody Step3Request request) {
        return handleStepCompletion(() -> journeyService.completeStep3(request));
    }

    @Operation(
            summary = "Complete Step 4 - Trip Details",
            description = "Set budget type (BASIC, STANDARD, LUXURY) and trip duration (1-30 days). Marks journey as COMPLETED."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Step 4 completed, journey finished"),
            @ApiResponse(responseCode = "400", description = "Invalid step, validation error, or journey already completed"),
            @ApiResponse(responseCode = "404", description = "Journey not found")
    })
    @PutMapping("/journeys/step4")
    public ResponseEntity<?> completeStep4(@Valid @RequestBody Step4Request request) {
        return handleStepCompletion(() -> journeyService.completeStep4(request));
    }

    @Operation(
            summary = "Get journey by ID",
            description = "Retrieves the current state of a journey including all completed step data"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Journey found"),
            @ApiResponse(responseCode = "404", description = "Journey not found")
    })
    @GetMapping("/journeys/{id}")
    public ResponseEntity<?> getJourney(
            @Parameter(description = "Journey ID", required = true)
            @PathVariable Long id
    ) {
        try {
            JourneyResponse response = journeyService.getJourney(id);
            return ResponseEntity.ok(response);
        } catch (JourneyNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(CustomErrorMessage.notFound("Journey", e.getJourneyId()));
        }
    }

    @Operation(
            summary = "Request a tour guide",
            description = "Submits a guide request for the given journey"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "Request accepted",content = @Content(mediaType = "text/plain",schema = @Schema(example = "Request accepted"))),
            @ApiResponse(responseCode = "400",content = @Content(mediaType = "application/json",schema = @Schema(implementation = CustomErrorMessage.class))),
            @ApiResponse(responseCode = "404",description = "Journey not found",content = @Content(mediaType = "application/json",schema = @Schema(implementation = CustomErrorMessage.class))),
            @ApiResponse(responseCode = "500",description = "Internal server error",content = @Content(mediaType = "application/json",schema = @Schema(implementation = CustomErrorMessage.class)))
    })

    @PostMapping("/journeys/guide")
    public ResponseEntity<?> guideRequest(@Valid @RequestBody JourneyRequest request) {
        try {
            journeyService.handleGuideRequest(request);
            return ResponseEntity.ok("Request accepted");
        } catch (JourneyNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomErrorMessage.notFound("Journey", e.getJourneyId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomErrorMessage.badRequest(e.getMessage()));
        }
    }



    @Operation(
            summary = "Request airport pickup",
            description = "Submits an airport pickup request for the given journey"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "Airport pickup request accepted",content = @Content(schema = @Schema(example = "Request accepted"))),
            @ApiResponse(responseCode = "400",description = "Validation or business error",content = @Content(schema = @Schema(implementation = CustomErrorMessage.class)))
    })
    @PostMapping("/journeys/airport-pickup")
    public ResponseEntity<?> airportPickupRequest(@Valid @RequestBody JourneyRequest request) {
        try {
            journeyService.handleAirportPickupRequest(request);
            return ResponseEntity.ok("Request accepted");
        } catch (JourneyNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomErrorMessage.notFound("Journey", e.getJourneyId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomErrorMessage.badRequest(e.getMessage()));
        }
    }

    // ============ Helper Method for Exception Handling ============

    @FunctionalInterface
    private interface StepAction {
        JourneyResponse execute();
    }

    private ResponseEntity<?> handleStepCompletion(StepAction action) {
        try {
            JourneyResponse response = action.execute();
            return ResponseEntity.ok(response);
        } catch (JourneyNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(CustomErrorMessage.notFound("Journey", e.getJourneyId()));
        } catch (JourneyAlreadyCompletedException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(CustomErrorMessage.journeyCompleted());
        } catch (InvalidStepException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new CustomErrorMessage(
                            "INVALID_STEP",
                            "Expected step " + e.getExpectedStep() + " but journey is at step " + e.getCurrentStep()
                    ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(CustomErrorMessage.badRequest(e.getMessage()));
        }
    }
}
