package com.skylab.superapp.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "seasons")
public class Season {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "tenant")
    private String tenant;

    @Column(name = "is_active")
    private boolean isActive;

    @ManyToMany
    @JoinTable(
            name = "season_competitor",
            joinColumns = @JoinColumn(name = "season_id"),
            inverseJoinColumns = @JoinColumn(name = "competitor_id")
    )
    private List<Competitor> competitors;


}
