package com.kursova.kursovaapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing a tour.
 */
@Getter
@Setter
@NoArgsConstructor
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

    public TourEntity(String name, String type, TransportEntity transport, String mealOption, Integer numberOfDays, Integer price, Double rating) {
        this.name = name;
        this.type = type;
        this.transport = transport;
        this.mealOption = mealOption;
        this.numberOfDays = numberOfDays;
        this.price = price;
        this.rating = rating;
    }
}
