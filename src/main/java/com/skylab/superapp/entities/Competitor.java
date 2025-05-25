package com.skylab.superapp.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Formula;

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

    @Formula("(SELECT COALESCE(SUM(cer.points), 0) FROM competitor_event_results cer WHERE cer.competitor_id = id)")
    private double totalPoints;

    @Formula("(SELECT COUNT(*) FROM competitor_event_results cer WHERE cer.competitor_id = id)")
    private int competitionCount;

    @OneToMany(mappedBy = "competitor")
    private List<CompetitorEventResult> eventResults; // Yarışmacının katıldığı tüm event'ler


}
