package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class ShoppingCart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "koi_fish_id")
    private KoiFish koiFish;

    @ManyToOne
    @JoinColumn(name="koi_fish_order_id")
    private KoiFishOrder koiFishOrder;

}
