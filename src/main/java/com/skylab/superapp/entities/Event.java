package com.skylab.superapp.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "guest_name")
    private String guestName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    @Column(name = "date")
    private Date date;

    @Column(name = "linkedin")
    private String linkedin;

    @Column(name = "is_active")
    private boolean isActive;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "event", cascade = CascadeType.ALL)
    private List<Photo> photos;

    @Column(name = "description")
    private String description;

    @Column(name = "tenant")
    private String tenant;

    @Column(name = "type")
    private String type;

    @Column(name = "form_url")
    private String formUrl;

    @ManyToOne
    @JoinColumn(name = "season_id")
    private Season season; // Event'in bağlı olduğu sezon

    @OneToMany(mappedBy = "event")
    private List<CompetitorEventResult> competitorResults; // Event'e katılan yarışmacılar ve puanları


}
