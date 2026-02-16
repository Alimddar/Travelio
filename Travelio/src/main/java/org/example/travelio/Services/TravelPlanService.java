package org.example.travelio.Services;

import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class TravelPlanService {
    public void enrichPlanWithExploreUrls(Map<String, Object> plan) {
        Object daysObj = plan.get("days");
        if (!(daysObj instanceof List<?> days)) return;

        for (Object dayObj : days) {
            if (!(dayObj instanceof Map<?, ?> dayMapRaw)) continue;
            Map<String, Object> day = (Map<String, Object>) dayMapRaw;

            String city = day.get("city") != null ? day.get("city").toString() : "";

            Object itineraryObj = day.get("itinerary");
            if (!(itineraryObj instanceof List<?> itinerary)) continue;

            for (Object slotObj : itinerary) {
                if (!(slotObj instanceof Map<?, ?> slotMapRaw)) continue;
                Map<String, Object> slot = (Map<String, Object>) slotMapRaw;

                Object activitiesObj = slot.get("activities");
                if (!(activitiesObj instanceof List<?> activities)) continue;

                for (Object actObj : activities) {
                    if (!(actObj instanceof Map<?, ?> actMapRaw)) continue;
                    Map<String, Object> activity = (Map<String, Object>) actMapRaw;

                    String placeName = activity.get("place_name") != null
                            ? activity.get("place_name").toString().trim() : "";

                    activity.put("city_name", city);

                    if (placeName.isEmpty()) {
                        throw new IllegalArgumentException("place_name is missing");
                    }

                    String query = (placeName + " " + city + " Azerbaijan images").trim();
                    String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
                    activity.put("explore_url", "https://www.google.com/search?tbm=isch&q=" + encodedQuery);
                }
            }
        }
    }
}
