package com.skylab.superapp.entities;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "staff")
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "linkedin")
    private String linkedin;

    @Column(name = "department")
    private String department;

    @OneToOne
    @JoinColumn(name = "photo_id")
    private Photo photo;

    @Column(name = "tenant")
    private String tenant;

}
