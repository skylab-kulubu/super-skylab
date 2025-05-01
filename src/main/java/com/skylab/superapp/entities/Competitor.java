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
@Table(name = "competitors")
public class Competitor {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "tenant")
    private String tenant;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "total_points")
    private double totalPoints;

    @Column(name = "competition_count" )
    private int competitionCount;

    @ManyToMany(mappedBy = "competitors")
    private List<Season> seasons;


}
