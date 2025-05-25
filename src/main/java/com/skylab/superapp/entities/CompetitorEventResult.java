package com.skylab.superapp.entities;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "competitor_event_results")
public class CompetitorEventResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "competitor_id")
    private Competitor competitor;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    private double points; // Yarışmacının bu event'de aldığı puan
}

