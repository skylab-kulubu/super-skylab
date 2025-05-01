package com.skylab.superapp.entities;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "photos")
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "photo_url")
    private String photoUrl;

    @Column(name = "tenant")
    private String tenant;

    @OneToOne(mappedBy = "photo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Staff staff;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "announcement_id")
    private Announcement announcement;


}
