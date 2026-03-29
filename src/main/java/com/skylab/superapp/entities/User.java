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
@Table(name = "users")
public class User {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "sky_number")
    private String skyNumber;

    @Column(name = "is_ldap_user")
    @Builder.Default
    private boolean isLdapUser = false;

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

}


