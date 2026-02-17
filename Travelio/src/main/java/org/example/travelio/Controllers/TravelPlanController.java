package org.example.travelio.Controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import org.example.travelio.Services.TravelPlanService;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "https://travel-a3qc8z9tp-tunars-projects-e1489b74.vercel.app", "https://travel-chi-jade.vercel.app"})
@RequestMapping("/api")
public class TravelPlanController {

    private final TravelPlanService planService;

    public TravelPlanController(TravelPlanService planService) {
        this.planService = planService;
    }

    @PostMapping("/plan/enrich")
    public ResponseEntity<Map<String, Object>> enrichPlan(@RequestBody Map<String, Object> plan) {
        planService.enrichPlanWithExploreUrls(plan);
        return ResponseEntity.ok(plan);
    }
}
