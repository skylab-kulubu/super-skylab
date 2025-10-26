package com.skylab.superapp.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "ldap_sky_number")
    private String ldapSkyNumber;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_picture_id", referencedColumnName = "id")
    private Image profilePicture;

    @Column(name = "linkedin")
    private String linkedin;

    @Column(name = "birthday")
    private LocalDateTime birthday;

    @Column(name = "university")
    private String university;

    @Column(name = "faculty")
    private String faculty;

    @Column(name = "department")
    private String department;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

}


