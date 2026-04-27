package com.skylab.superapp.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "events")
public class Event extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cover_image_id")
    private Image coverImage;

    @Column(name = "is_ranked")
    private boolean ranked;

    @Column(name = "prize_info")
    private String prizeInfo;

    @Column(name = "location")
    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_type_id", nullable = false)
    private EventType type;

    @Column(name = "form_url")
    private String formUrl;

    @Column(name = "capacity")
    private int capacity;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(name = "start_date")
    private LocalDateTime startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "linkedin")
    private String linkedin;

    @Column(name = "is_active")
    private boolean active;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "event_images",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "image_id")
    )
    private List<Image> images;

    @ManyToMany
    @JoinTable(
            name = "event_organizers",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> organizers;

    @JsonManagedReference
    @OneToMany(mappedBy = "event")
    private List<Ticket> soldTickets;

    @JsonManagedReference
    @OneToMany(mappedBy = "event")
    private List<EventDay> eventDays;

    @ManyToOne
    @JoinColumn(name = "season_id")
    private Season season;

    @OneToMany(mappedBy = "event")
    private List<Certificate> certificates = new ArrayList<>();


}
