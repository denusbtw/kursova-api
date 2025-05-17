package com.kursova.kursovaapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Entity
@Table(name = "favorite")
public class FavoriteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "tour_id")
    private TourEntity tour;

    public FavoriteEntity() {

    }

    public FavoriteEntity(TourEntity tour) {
        this.tour = tour;
    }
}
