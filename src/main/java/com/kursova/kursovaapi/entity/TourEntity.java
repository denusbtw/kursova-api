package com.kursova.kursovaapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "tour")
public class TourEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Setter
    private String name;
    @Setter
    private String type;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transport_id")
    private TransportEntity transport;

    @Setter
    private String mealOption;
    @Setter
    private int numberOfDays;
    @Setter
    private int price;
    @Setter
    private double rating;

    public TourEntity() {
    }

    public TourEntity(String name, String type, TransportEntity transport, String mealOption, int numberOfDays, int price, double rating) {
        this.name = name;
        this.type = type;
        this.transport = transport;
        this.mealOption = mealOption;
        this.numberOfDays = numberOfDays;
        this.price = price;
        this.rating = rating;
    }
}