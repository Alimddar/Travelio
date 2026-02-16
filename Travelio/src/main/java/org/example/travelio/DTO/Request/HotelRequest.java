package org.example.travelio.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelRequest {
    private String name;
    private Double rating;
    private Integer reviewCount;
    private Double price;
    private String currency;
    private String hotelUrl;
    private String imageUrl;
}