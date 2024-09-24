package com.example.demo.api;

import com.example.demo.entity.Breed;
import com.example.demo.entity.ConsultingStaff;
import com.example.demo.service.BreedService;
import com.example.demo.service.ConsultingStaffService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/breed")
public class BreedAPI {
    @Autowired
    BreedService breedService;
    @PostMapping
    public ResponseEntity create(@Valid @RequestBody Breed breed) {
        Breed newBreed = breedService.createNewBreed(breed);
        //return ve font end
        return ResponseEntity.ok(newBreed);
    }

    // Get danh sách breed
    @GetMapping
    public ResponseEntity get(){
        List<Breed> breeds = breedService.getAllBreed();
        return ResponseEntity.ok(breeds);
    }
    // /api/breed/{id} => id cua thang breed minh muon update
    @PutMapping("{id}")
    public ResponseEntity updateBreed(@Valid @RequestBody Breed breed, @PathVariable long id){//valid kich hoat co che vadilation
        Breed newBreed = breedService.updateBreed(breed,id);
        return ResponseEntity.ok(newBreed);
    }
    @DeleteMapping("{id}")
    public ResponseEntity deleteBreed(@PathVariable long id){
        Breed newBreed = breedService.deleteBreed(id);
        return ResponseEntity.ok(newBreed);
    }
}