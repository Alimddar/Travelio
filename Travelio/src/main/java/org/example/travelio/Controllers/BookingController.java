package org.example.travelio.Controllers;

import org.example.travelio.DTO.Response.HotelResponse;
import org.example.travelio.DTO.Response.TravelResponse;
import org.example.travelio.Services.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = {"http://localhost:5173", "https://travel-a3qc8z9tp-tunars-projects-e1489b74.vercel.app"})
@RequestMapping("/api/travel")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/fetch-hotels")
    public ResponseEntity<TravelResponse> fetchHotelsForPlan(@RequestBody Map<String, Object> aiPlan,
                                                             @RequestParam(defaultValue = "1") Integer adults,
                                                             @RequestParam(defaultValue = "AZN") String currency) {
        try {
            List<HotelResponse> hotels = bookingService.getHotelsForTrip(aiPlan, adults, currency);
            return ResponseEntity.ok(TravelResponse.builder()
                    .suggestedHotels(hotels)
                    .status("Hotels fetched successfully")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(TravelResponse.builder()
                    .status("Hotel Fetch Error: " + e.getMessage())
                    .build());
        }
    }
}
