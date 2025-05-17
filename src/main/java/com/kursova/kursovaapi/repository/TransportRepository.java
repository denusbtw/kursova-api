package com.kursova.kursovaapi.repository;

import com.kursova.kursovaapi.entity.TransportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransportRepository extends JpaRepository<TransportEntity, Integer> {
}
