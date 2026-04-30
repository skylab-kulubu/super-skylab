package com.skylab.superapp.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "event_days")
public class EventDay extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "name")
    private String name;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @JsonManagedReference
    @OneToMany(mappedBy = "eventDay")
    private List<Session> sessions;


}
