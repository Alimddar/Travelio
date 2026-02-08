package org.example.travelio.Services;


import org.example.travelio.DTO.Request.AIRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AIService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.endpoint}")
    private String geminiUrl;

    public Map<String, Object> generatePlan(AIRequest dto) {
        if (dto.getTravelWith() == null || dto.getBudgetType() == null ||
                dto.getTripDays() == null || dto.getTripDays() <= 0 ||
                dto.getInterests() == null || dto.getInterests().isEmpty()) {
            throw new IllegalArgumentException("Missing required trip data.");
        }

        String interestsStr = String.join(", ", dto.getInterests());

        String prompt = """
        You are an expert travel consultant specializing in cross-country itineraries for Azerbaijan.
       
        GOAL: Create a comprehensive day-by-day travel itinerary that covers multiple regions of Azerbaijan based on the trip duration.
        
        INPUT:
        Travel destination: Azerbaijan
        Travel style: %s
        Travel with: %s
        Adults: %d
        Children: %d
        Interests: %s
        Budget: %s
        Trip days: %d
        Currency: %s
        
        STRICT RULES:
        - Do not limit the plan to only one city. If the trip is more than 3 days, suggest moving between regions.
        - The "trip_summary" must be exactly 2-3 sentences long.
        - Each day MUST have between 2 to 4 activities.
        - "place_name" is mandatory and cannot be empty for any activity.
        - Match activities to these interests: %s.
        - If 'gastronomy' is selected, mention specific regional dishes.
        - Respond ONLY with valid JSON. No markdown (no ```json), no extra text.
        
        RESPONSE FORMAT:
        {
          "trip_summary": "Summary text...",
          "days": [
            {
              "day": 1,
              "city": "Current City/Region",
              "activities": [
                {
                  "place_name": "Specific Location Name",
                  "description": "Short info"
                }
              ]
            }
          ]
        }
        """.formatted(
                dto.getTravelStyle(), dto.getTravelWith(), dto.getAdultsCount(),
                dto.getChildrenCount(), interestsStr, dto.getBudgetType(),
                dto.getTripDays(), dto.getCurrency() != null ? dto.getCurrency() : "AZN",
                interestsStr
        );
        Map<String, Object> body = Map.of("contents", List.of(Map.of("parts", List.of(Map.of("text", prompt)))));

        try {
            WebClient webClient = WebClient.builder().defaultHeader("Content-Type", "application/json").build();

            Map response = webClient.post()
                    .uri(geminiUrl + "?key=" + geminiApiKey)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null || !response.containsKey("candidates")) {
                throw new RuntimeException("AI response is empty");
            }

            List<Map> candidates = (List<Map>) response.get("candidates");
            Map content = (Map) candidates.get(0).get("content");
            List<Map> parts = (List<Map>) content.get("parts");
            String aiText = parts.get(0).get("text").toString();

            Matcher matcher = Pattern.compile("\\{.*\\}", Pattern.DOTALL).matcher(aiText.trim());
            String cleanedJson = matcher.find() ? matcher.group() : aiText;

            Map<String, Object> plan = new ObjectMapper().readValue(cleanedJson, Map.class);
            enrichPlanWithExploreUrls(plan);
            return plan;

        } catch (Exception e) {
            System.err.println("Xəta baş verdi: " + e.getMessage());
            throw new RuntimeException("Could not generate trip plan. Please try again.");
        }
    }

    public void enrichPlanWithExploreUrls(Map<String, Object> plan) {
        Object daysObj = plan.get("days");
        if (!(daysObj instanceof List<?> days)) return;

        for (Object dayObj : days) {
            if (!(dayObj instanceof Map<?, ?> dayMapRaw)) continue;
            Map<String, Object> day = (Map<String, Object>) dayMapRaw;

            String city = day.get("city") != null ? day.get("city").toString() : "";

            Object activitiesObj = day.get("activities");
            if (!(activitiesObj instanceof List<?> activities)) continue;

            for (Object actObj : activities) {
                if (!(actObj instanceof Map<?, ?> actMapRaw)) continue;
                Map<String, Object> activity = (Map<String, Object>) actMapRaw;

                String placeName = activity.get("place_name") != null
                        ? activity.get("place_name").toString().trim()
                        : "";

                activity.put("city_name", city);

                if (placeName.isEmpty()) {
                    throw new IllegalArgumentException("place_name is missing for an activity");
                }

                String query = (placeName + " " + city + " Azerbaijan images").trim();
                String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
                activity.put("explore_url", "https://www.google.com/search?tbm=isch&q=" + encodedQuery);
            }
        }
    }


}