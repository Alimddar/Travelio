package org.example.travelio.DTO;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ItineraryDTO {
    private String email;
    private TripPlan plan;

    @Setter
    @Getter
    public static class TripPlan {
        @JsonProperty("trip_summary")
        private String tripSummary;
        private List<Day> days;
    }

    @Setter
    @Getter
    public static class Day {
        private int day;
        private String city;
        private List<Slot> itinerary;

    }

    @Setter
    @Getter
    public static class Slot {
        @JsonProperty("time_slot")
        private String timeSlot;
        private List<Activity> activities;

    }

    @Setter
    @Getter
    public static class Activity {
        @JsonProperty("place_name")
        private String placeName;
        private String description;

    }
}