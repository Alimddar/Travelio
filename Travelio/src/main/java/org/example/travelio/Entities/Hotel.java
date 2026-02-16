package org.example.travelio.Entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "default_hotels")
public class Hotel {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double rating;
    private Integer reviewCount;
    private Double price;
    private String currency;
    private String hotelUrl;
    private String imageUrl;
    private String city;
}