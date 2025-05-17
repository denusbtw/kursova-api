package com.kursova.kursovaapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "transport")
public class TransportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Setter
    private String name;

    public TransportEntity() {

    }

    public TransportEntity(String name) {
        this.name = name;
    }
}
