
package com.example.demo.entity;

import com.example.demo.Enum.QuotationEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Random;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class QuotationProcess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Code can not be blank!")
    @Pattern(regexp = "QOP\\d{7}", message = "Invalid code!")
    @Column(unique = true)
    private String quotationProcessId;

    @PrePersist
    private void prePersist() {
        this.quotationProcessId = generateFarmId();
        this.createdAt = LocalDateTime.now();
    }

    public String generateFarmId() {
        Random random = new Random();
        int number = random.nextInt(10000000);
        return String.format("QOP%07d", number);
    }

    @Column(nullable = false)
    private boolean isDeleted = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private QuotationEnum status;

    private String notes;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "quotation_id")
    private Quotation quotation;
}
