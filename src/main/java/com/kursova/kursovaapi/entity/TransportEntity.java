package com.kursova.kursovaapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing a transport option.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "transport")
public class TransportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    public TransportEntity(String name) {
        this.name = name;
    }
}
