package com.kursova.kursovaapi.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entity representing a tour.
 */
@Data
@Entity
@Table(name = "tour")
public class TourEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transport_id")
    private TransportEntity transport;

    private String mealOption;
    private int numberOfDays;
    private int price;
    private double rating;

    public TourEntity() {
        // Default constructor for JPA
    }
}
