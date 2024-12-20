package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Code can not be blank!")
    @Pattern(regexp = "TOR\\d{7}", message = "Invalid code!")
    @Column(unique = true)
    private String tourId;

    @Column(nullable = false)
    private boolean isDeleted = false;

    @NotBlank(message = "Name can not be blank")
    @Pattern(regexp = "^[^\\s].*", message = "First character not have space!")
    private String tourName;

    @NotNull(message = "Start date cannot be null")
    private LocalDate startDate;

    @NotBlank(message = "Duration can not be blank")
    @Pattern(regexp = "^[2-5] days$", message = "Enter the correct format!")
    private String duration; // Chuyển sang String để dễ dàng kiểm tra định dạng

    private String image;

    private String description;

    private String status;

    private double price;

    private String schedule;

    private String time;

    private String generateTourId() {
        Random random = new Random();
        int number = random.nextInt(10000000);
        return String.format("TOR%07d", number);
    }
    @PrePersist
    private void prePersist() {
        this.tourId = generateTourId();
    }

    private double perAdultPrice;
    private double perChildrenPrice;

    @ManyToMany
    @JoinTable(
            name = "tour_farm",
            joinColumns = @JoinColumn(name = "tour_id"),
            inverseJoinColumns = @JoinColumn(name = "farm_id")
    )
    private Set<Farm> farms = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @OneToMany(mappedBy = "tour")
    @JsonIgnore
    private List<OpenTour> openTours;
}
