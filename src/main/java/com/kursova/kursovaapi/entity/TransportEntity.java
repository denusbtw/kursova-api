package com.kursova.kursovaapi.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "transport")
public class TransportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    public TransportEntity() {

    }

    public TransportEntity(String name) {
        this.name = name;
    }
}
