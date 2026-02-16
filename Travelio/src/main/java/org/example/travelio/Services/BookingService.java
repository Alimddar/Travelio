package org.example.travelio.Services;

import org.example.travelio.DTO.Response.HotelResponse;
import org.example.travelio.Entities.Hotel;
import org.example.travelio.Repositories.HotelRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final HotelRepository hotelRepository;
    private final WebClient webClient;

    @Value("${booking.api.key:}")
    private String apiKey;

    @Value("${booking.aid:}")
    private String aid;

    public BookingService(HotelRepository hotelRepository, WebClient.Builder webClientBuilder) {
        this.hotelRepository = hotelRepository;
        this.webClient = webClientBuilder.baseUrl("https://booking-com15.p.rapidapi.com").build();
    }

    public List<HotelResponse> getHotelsForTrip(Map<String, Object> aiPlan, Integer adults, String currency) {
        Set<String> cities = extractCitiesFromPlan(aiPlan);
        if (cities.isEmpty()) {
            cities.add("Baku");
        }

        String checkIn = LocalDate.now().plusDays(14).toString();
        String checkOut = LocalDate.now().plusDays(15).toString();
        int safeAdults = adults == null || adults < 1 ? 1 : adults;
        String safeCurrency = (currency == null || currency.isBlank())
                ? "AZN"
                : currency.toUpperCase(Locale.ROOT);

        if (apiKey != null && !apiKey.isBlank()) {
            for (String city : cities) {
                String destId = fetchDestId(city);
                if (destId != null) {
                    List<HotelResponse> hotels = fetchRealHotels(destId, safeAdults, safeCurrency, checkIn, checkOut);
                    if (!hotels.isEmpty()) {
                        return hotels.stream().limit(5).collect(Collectors.toList());
                    }
                }
            }
        }

        return fetchFallbackHotelsFromDb(cities.iterator().next());
    }

    private String fetchDestId(String city) {
        try {
            Map<String, Object> response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/hotels/searchDestination")
                            .queryParam("query", city)
                            .build())
                    .header("X-RapidAPI-Key", apiKey)
                    .header("X-RapidAPI-Host", "booking-com15.p.rapidapi.com")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            List<Map<String, Object>> data = asListOfMaps(response != null ? response.get("data") : null);
            if (!data.isEmpty()) {
                Object destId = data.get(0).get("dest_id");
                if (destId != null) {
                    return destId.toString();
                }
            }
        } catch (Exception e) {
            System.err.println("DestID Error: " + e.getMessage());
        }
        return null;
    }

    private List<HotelResponse> fetchRealHotels(String destId, int adults, String currency, String checkIn, String checkOut) {
        try {
            Map<String, Object> response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/hotels/searchHotels")
                            .queryParam("dest_id", destId)
                            .queryParam("search_type", "CITY")
                            .queryParam("arrival_date", checkIn)
                            .queryParam("departure_date", checkOut)
                            .queryParam("adults", adults)
                            .queryParam("currency_code", currency)
                            .queryParam("page_number", "1")
                            .queryParam("units", "metric")
                            .build())
                    .header("X-RapidAPI-Key", apiKey)
                    .header("X-RapidAPI-Host", "booking-com15.p.rapidapi.com")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();

            Map<String, Object> dataMap = asMap(response != null ? response.get("data") : null);
            List<Map<String, Object>> hotelsList = asListOfMaps(dataMap.get("hotels"));
            if (hotelsList.isEmpty()) {
                return Collections.emptyList();
            }

            return hotelsList.stream()
                    .map(hotel -> mapToResponse(hotel, currency))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Hotels Fetch Error: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    private HotelResponse mapToResponse(Map<String, Object> hotelData, String currency) {
        Map<String, Object> property = asMap(hotelData.get("property"));
        Map<String, Object> priceBreakdown = asMap(property.get("priceBreakdown"));
        Map<String, Object> grossPrice = asMap(priceBreakdown.get("grossPrice"));

        String name = String.valueOf(property.getOrDefault("name", "Booking Hotel"));
        Double rating = parseSafeDouble(property.get("reviewScore"));
        Integer reviews = parseSafeInt(property.get("reviewCount"));
        Double priceValue = parseSafeDouble(grossPrice.get("value"));

        List<Object> photos = asList(property.get("photoUrls"));
        String imageUrl = photos.isEmpty() ? null : String.valueOf(photos.get(0));

        String hotelId = String.valueOf(property.getOrDefault("id", ""));
        String hotelUrl = "https://www.booking.com/hotel/az/id-" + hotelId + ".html";
        if (aid != null && !aid.isBlank()) {
            hotelUrl = hotelUrl + "?aid=" + aid;
        }

        return HotelResponse.builder()
                .name(name)
                .rating(rating)
                .reviewCount(reviews)
                .price(priceValue)
                .currency(currency)
                .hotelUrl(hotelUrl)
                .imageUrl(imageUrl)
                .build();
    }

    private Set<String> extractCitiesFromPlan(Map<String, Object> plan) {
        Set<String> cities = new LinkedHashSet<>();
        try {
            List<Map<String, Object>> days = asListOfMaps(plan != null ? plan.get("days") : null);
            for (Map<String, Object> day : days) {
                Object cityRaw = day.get("city");
                if (cityRaw != null) {
                    String city = cityRaw.toString().split("/")[0].trim();
                    if (!city.isEmpty()) {
                        cities.add(city);
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return cities;
    }

    private List<HotelResponse> fetchFallbackHotelsFromDb(String city) {
        List<Hotel> hotels = hotelRepository.findByCityIgnoreCase(city);
        if (hotels.isEmpty()) {
            hotels = hotelRepository.findAll();
        }

        return hotels.stream()
                .limit(5)
                .map(hotel -> HotelResponse.builder()
                        .name(hotel.getName())
                        .rating(hotel.getRating())
                        .reviewCount(hotel.getReviewCount())
                        .price(hotel.getPrice())
                        .currency(hotel.getCurrency())
                        .hotelUrl(addAid(hotel.getHotelUrl()))
                        .imageUrl(hotel.getImageUrl())
                        .build())
                .collect(Collectors.toList());
    }

    private String addAid(String hotelUrl) {
        if (hotelUrl == null || hotelUrl.isBlank()) {
            return hotelUrl;
        }

        if (aid == null || aid.isBlank()) {
            return hotelUrl;
        }

        return hotelUrl.contains("?") ? hotelUrl + "&aid=" + aid : hotelUrl + "?aid=" + aid;
    }

    private Double parseSafeDouble(Object value) {
        if (value == null) {
            return 0.0;
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return 0.0;
        }
    }

    private Integer parseSafeInt(Object value) {
        if (value == null) {
            return 0;
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (Exception e) {
            return 0;
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> asMap(Object value) {
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return Collections.emptyMap();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> asListOfMaps(Object value) {
        if (!(value instanceof List<?> list)) {
            return Collections.emptyList();
        }
        return list.stream()
                .filter(item -> item instanceof Map<?, ?>)
                .map(item -> (Map<String, Object>) item)
                .collect(Collectors.toList());
    }

    private List<Object> asList(Object value) {
        if (value instanceof List<?> list) {
            return new ArrayList<>(list);
        }
        return Collections.emptyList();
    }
}
