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
@Table(name = "announcements")
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    @Column(name = "date")
    private Date date;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "tenant")
    private String tenant;

    @Column(name = "created_at")
    private Date createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "announcement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Photo> photos;

    @Column(name = "type")
    private String type;

    @Column(name = "form_url")
    private String formUrl;


}
