package com.example.demo.repository;


import com.example.demo.entity.Breed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BreedRepository extends JpaRepository<Breed,Long> {
    Breed findBreedById(long BreedId);

    List<Breed> findBreedsByIsDeletedFalse();
}