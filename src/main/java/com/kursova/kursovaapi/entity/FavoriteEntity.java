package com.kursova.kursovaapi.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entity representing a favorite tour entry.
 */
@Data
@Entity
@Table(name = "favorite")
public class FavoriteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "tour_id")
    private TourEntity tour;

    public FavoriteEntity() {
        // Default constructor for JPA
    }

    public FavoriteEntity(TourEntity tour) {
        this.tour = tour;
    }
}
