package org.example.travelio.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.travelio.DTO.Request.AIRequest;
import org.example.travelio.DTO.Response.JourneyResponse;
import org.example.travelio.Services.AIService;
import org.example.travelio.Services.JourneyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = {"http://localhost:5173", "https://travel-a3qc8z9tp-tunars-projects-e1489b74.vercel.app"})
@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI", description = "AI powered travel planning APIs")
public class AIController {

    private final AIService aiService;
    private final JourneyService journeyService;

    public AIController(AIService aiService, JourneyService journeyService) {
        this.aiService = aiService;
        this.journeyService = journeyService;
    }

    @Operation(summary = "Generate travel plan from AI",description = "Creates a personalized travel plan based on journey preferences")
    @ApiResponse(responseCode = "200", description = "Successfully generated plan",content = @Content(mediaType = "application/json"))
    @ApiResponse(responseCode = "400", description = "Bad request - Invalid input parameters")
    @ApiResponse(responseCode = "500", description = "Internal server error - Could not generate plan")
    @PostMapping("/get-plan")
    public ResponseEntity<?> getPlanFromAI(@RequestBody JourneyResponse journey) {
        try {
            AIRequest dto = new AIRequest();
            dto.setInterests(journey.getInterests());
            dto.setChildrenCount(journey.getChildrenCount());
            dto.setBudgetType(journey.getBudgetType().toString());
            dto.setTravelStyle(journey.getTravelStyle().toString());
            dto.setTravelWith(journey.getTravelWith().toString());
            dto.setTripDays(journey.getTripDays());
            dto.setCurrency("AZN");
            Map<String, Object> plan = aiService.generatePlan(dto);
            return ResponseEntity.ok(plan);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Could not generate trip plan. Please try again."));
        }
    }
}
